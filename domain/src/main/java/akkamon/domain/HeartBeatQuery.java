package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HeartBeatQuery extends AbstractBehavior<HeartBeatQuery.Command> {

    public interface Command {}

    private static enum CollectionTimeout implements Command {
        INSTANCE
    }

    static class WrappedRespondTrainerPosition implements Command {
        final Trainer.RespondTrainerPosition response;

        WrappedRespondTrainerPosition(
                Trainer.RespondTrainerPosition response
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
    private Map<String, AkkamonNexus.TrainerPositionReading> repliesSoFar = new HashMap<String, AkkamonNexus.TrainerPositionReading>();
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

        ActorRef<Trainer.RespondTrainerPosition> respondTrainerPositionAdapter =
                context.messageAdapter(Trainer.RespondTrainerPosition.class, WrappedRespondTrainerPosition::new);

        for (Map.Entry<String, ActorRef<Trainer.Command>> entry : trainerIdToActor.entrySet()) {
            context.watchWith(entry.getValue(), new TrainerOffline(entry.getKey()));
            entry.getValue().tell(
                    new Trainer.ReadTrainerPosition(
                            0L,
                            respondTrainerPositionAdapter
                    )
                    );
        }
        stillWaiting = new HashSet<>(trainerIdToActor.keySet());
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(WrappedRespondTrainerPosition.class, this::onRespondTrainerPosition)
                .build();
    }

    private Behavior<Command> onRespondTrainerPosition(WrappedRespondTrainerPosition r) {
        AkkamonNexus.TrainerPositionReading trainerPositionRead =
                r.response
                .value
                .map(optionalValue -> (AkkamonNexus.TrainerPositionReading) new AkkamonNexus.TrainerPosition(optionalValue))
                .orElse(AkkamonNexus.TrainerPositionNotAvailable.INSTANCE);

        String trainerId = r.response.trainerId;
        repliesSoFar.put(trainerId, trainerPositionRead);
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
