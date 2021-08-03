package akkamon.domain.actors.tasks.heartbeat;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.actors.Trainer;

import java.time.Duration;
import java.util.*;

import static akkamon.domain.actors.AkkamonNexus.*;

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
        final TrainerID trainerID;

        private TrainerOffline(TrainerID trainerID) {
            this.trainerID = trainerID;
        }
    }

    public static Behavior<Command> create(
            Map<TrainerID, ActorRef<Trainer.Command>> trainerIDToActor,
            long requestId,
            String sceneId,
            ActorRef<AkkamonNexus.Command> requester,
            Duration timeout
    ) {
        return Behaviors.setup(
                context ->
                        Behaviors.withTimers(
                                timers -> new HeartBeatQuery(
                                        trainerIDToActor,
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
    private Map<TrainerID, MovementQueueReading> repliesSoFar = new HashMap<>();
    private final Set<TrainerID> stillWaiting;

    public HeartBeatQuery(
            Map<TrainerID, ActorRef<Trainer.Command>> trainerIDToActor,
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

        for (Map.Entry<TrainerID, ActorRef<Trainer.Command>> entry : trainerIDToActor.entrySet()) {
            context.watchWith(entry.getValue(), new TrainerOffline(entry.getKey()));
            entry.getValue().tell(
                    new Trainer.ReadMovementQueue(
                            0L,
                            respondTrainerPositionAdapter
                    ));
        }
        stillWaiting = new HashSet<>(trainerIDToActor.keySet());
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(WrappedRespondMovementQueue.class, this::onRespondMovementQueue)
                .build();
    }

    private Behavior<Command> onRespondMovementQueue(WrappedRespondMovementQueue r) {
        MovementQueueReading movementQueueRead = null;
        if (r.response.value.size() != 0) {
            movementQueueRead = new MovementQueue(r.response.value);
        } else {
            Queue<Direction> queue = new LinkedList<>();
            queue.add(Direction.NONE);
            movementQueueRead = new MovementQueue(queue);
        }

        TrainerID trainerID = r.response.trainerID;
        repliesSoFar.put(trainerID, movementQueueRead);
        stillWaiting.remove(trainerID);

        return respondWhenAllCollected();
    }

    private Behavior<Command> respondWhenAllCollected() {
        if (stillWaiting.isEmpty()) {
            requester.tell(new RespondHeartBeatQuery(
                    requestId,
                    sceneId,
                    repliesSoFar));
            return Behaviors.stopped();
        } else {
            return this;
        }
    }

}
