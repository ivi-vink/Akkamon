package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.*;

public class HeartBeatQuery extends AbstractBehavior<HeartBeatQuery.Command> {

    public interface Command {}

    private static enum CollectionTimeout implements Command {
        INSTANCE
    }

    static class WrappedRespondMovementQueue implements Command {
        final Trainer.RespondMovementQueue response;

        WrappedRespondMovementQueue(
                Trainer.RespondMovementQueue response
        ) {
            this.response = response;
        }
    }

    private static class TrainerOffline implements Command {
        final String trainerId;

        private TrainerOffline(String trainerId) {
            this.trainerId = trainerId;
        }
    }

    public static Behavior<Command> create(
            Map<String, ActorRef<Trainer.Command>> trainerIdToActor,
            long requestId,
            String sceneId,
            ActorRef<AkkamonNexus.Command> requester,
            Duration timeout
    ) {
        return Behaviors.setup(
                context ->
                        Behaviors.withTimers(
                                timers -> new HeartBeatQuery(
                                        trainerIdToActor,
                                        requestId,
                                        sceneId,
                                        requester,
                                        timeout,
                                        context,
                                        timers
                        )
                )
        );
    }

    private final long requestId;
    private final String sceneId;
    private final ActorRef<AkkamonNexus.Command> requester;
    private Map<String, AkkamonNexus.MovementQueueReading> repliesSoFar = new HashMap<String, AkkamonNexus.MovementQueueReading>();
    private final Set<String> stillWaiting;

    public HeartBeatQuery(
            Map<String, ActorRef<Trainer.Command>> trainerIdToActor,
            long requestId,
            String sceneId,
            ActorRef<AkkamonNexus.Command> requester,
            Duration timeout,
            ActorContext<Command> context,
            TimerScheduler<Command> timers) {
        super(context);
        this.requestId = requestId;
        this.sceneId = sceneId;
        this.requester = requester;

        timers.startSingleTimer(CollectionTimeout.INSTANCE, timeout);

        ActorRef<Trainer.RespondMovementQueue> respondTrainerPositionAdapter =
                context.messageAdapter(Trainer.RespondMovementQueue.class, WrappedRespondMovementQueue::new);

        for (Map.Entry<String, ActorRef<Trainer.Command>> entry : trainerIdToActor.entrySet()) {
            context.watchWith(entry.getValue(), new TrainerOffline(entry.getKey()));
            entry.getValue().tell(
                    new Trainer.ReadMovementQueue(
                            0L,
                            respondTrainerPositionAdapter
                    ));
        }
        stillWaiting = new HashSet<>(trainerIdToActor.keySet());
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(WrappedRespondMovementQueue.class, this::onRespondMovementQueue)
                .build();
    }

    private Behavior<Command> onRespondMovementQueue(WrappedRespondMovementQueue r) {
        AkkamonNexus.MovementQueueReading movementQueueRead = null;
        if (r.response.value.size() != 0) {
            movementQueueRead = new AkkamonNexus.MovementQueue(r.response.value);
        } else {
            Queue<Direction> queue = new LinkedList<>();
            queue.add(Direction.NONE);
            movementQueueRead = new AkkamonNexus.MovementQueue(queue);
        }

        String trainerId = r.response.trainerId;
        repliesSoFar.put(trainerId, movementQueueRead);
        stillWaiting.remove(trainerId);

        return respondWhenAllCollected();
    }

    private Behavior<Command> respondWhenAllCollected() {
        if (stillWaiting.isEmpty()) {
            requester.tell(new AkkamonNexus.RespondHeartBeatQuery(
                    requestId,
                    sceneId,
                    repliesSoFar));
            return Behaviors.stopped();
        } else {
            return this;
        }
    }

}
