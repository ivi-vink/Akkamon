package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;
import java.util.Map;

public class AkkamonNexus extends AbstractBehavior<AkkamonNexus.Command> {


    public interface Command {}

    public static class RequestTrainerRegistration
            implements AkkamonNexus.Command, SceneTrainerGroup.Command {
        public String trainerId;
        public String sceneId;
        public AkkamonSession session;
        public ActorRef<Command> replyTo;

        public RequestTrainerRegistration(
                String trainerId,
                String sceneId,
                AkkamonSession session,
                ActorRef<Command> replyTo
        ) {
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.session = session;
            this.replyTo = replyTo;
        }
    }

    public static class TrainerRegistered implements Command {
        private String trainerId;
        private AkkamonSession session;

        public TrainerRegistered(
                String trainerId,
                AkkamonSession session
        ) {
            this.trainerId = trainerId;
            this.session = session;
        }
    }

    public static class RequestStartMoving
            implements Command, SceneTrainerGroup.Command, Trainer.Command {
        public long requestId;
        public String trainerId;
        public String sceneId;
        public Direction direction;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestStartMoving(long requestId, String trainerId, String sceneId, Direction direction, ActorRef<AkkamonNexus.Command> replyTo) {
            this.requestId = requestId;
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.direction = direction;
            this.replyTo = replyTo;
        }
    }

    public static class RequestStopMoving
            implements Command, SceneTrainerGroup.Command, Trainer.Command  {
        public long requestId;
        public String trainerId;
        public String sceneId;
        public Direction direction;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestStopMoving(
                long requestId,
                String trainerId,
                String sceneId,
                Direction direction,
                ActorRef<AkkamonNexus.Command> replyTo) {
            this.requestId = requestId;
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.direction = direction;
            this.replyTo = replyTo;
        }
    }

    public static class RequestNewTilePos
            implements Command, SceneTrainerGroup.Command, Trainer.Command {
        public long requestId;
        public String trainerId;
        public String sceneId;
        public TilePos tilePos;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestNewTilePos(long requestId, String trainerId, String sceneId, TilePos tilePos, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.tilePos = tilePos;
            this.replyTo = replyTo;
        }
    }

    public static class RequestHeartBeat
            implements Command, SceneTrainerGroup.Command {

        public long requestId;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestHeartBeat(long requestId, ActorRef<AkkamonNexus.Command> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    public static class RespondHeartBeatQuery implements Command {

    }

    public interface TrainerPositionReading { }

    public static class TrainerPosition implements TrainerPositionReading {
        public final TilePos value;

        public TrainerPosition(TilePos value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TrainerPosition other = (TrainerPosition) o;

            return this.value.x == other.value.x && this.value.y == other.value.y;
        }

        @Override
        public String toString() {
            return "TrainerPosition={x: " + value.x + ", " + value.y + "}";
        }

    }

    private static class SceneTrainerGroupTerminated implements AkkamonNexus.Command {
        public SceneTrainerGroupTerminated(String sceneId) {
        }
    }

    public static Behavior<AkkamonNexus.Command> create(AkkamonMessageEngine messagingEngine) {
        return Behaviors.setup(context -> new AkkamonNexus(context, messagingEngine));
    }

    private AkkamonMessageEngine messageEngine;
    private Map<String, ActorRef<SceneTrainerGroup.Command>> sceneIdToActor = new HashMap<>();

    public AkkamonNexus(ActorContext<Command> context, AkkamonMessageEngine msgEngine) {
        super(context);
        this.messageEngine = msgEngine;
        getContext().getLog().info("AkkamonNexus is up and running, waiting eagerly for your messages!");
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(RequestTrainerRegistration.class, this::onTrainerRegistration)
                .onMessage(TrainerRegistered.class, this::onTrainerRegistered)
                .onMessage(RequestHeartBeat.class, this::onHeartBeat)
                .onMessage(RequestStartMoving.class, this::onStartMoving)
                .onMessage(RequestStopMoving.class, this::onStopMoving)
                .onMessage(RequestNewTilePos.class, this::onNewTilePos)
                .build();
    }

    private AkkamonNexus onHeartBeat(RequestHeartBeat heartBeatRequest) {
        return this;
    }

    private AkkamonNexus onNewTilePos(RequestNewTilePos newTilePosRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                newTilePosRequest.sceneId
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(newTilePosRequest);
        } else {
            getContext().getLog().info("Ignoring newTilePos request in scene {}, it isn't mapped to a sceneTrainerActor.");
        }
        return this;
    }

    private AkkamonNexus onStopMoving(RequestStopMoving stopMovingRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                stopMovingRequest.sceneId
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(stopMovingRequest);
        } else {
            getContext().getLog().info("Ignoring stopMove request in scene {}, it isn't mapped to a sceneTrainerActor.");
        }
        return this;
    }

    private AkkamonNexus onStartMoving(RequestStartMoving startMovingRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                startMovingRequest.sceneId
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(startMovingRequest);
        } else {
            getContext().getLog().info("Ignoring stopMove request in scene {}, it isn't mapped to a sceneTrainerActor.");
        }
        return this;
    }

    private AkkamonNexus onTrainerRegistered(TrainerRegistered reply) {
        // TODO test when registration fails?
        getContext().getLog().info("Adding {} to Live AkkamonSessions in Messaging Engine", reply.trainerId);
        messageEngine.registerTrainerSessionToScene(reply.trainerId, reply.session);
        return this;
    }

    private AkkamonNexus onTrainerRegistration(RequestTrainerRegistration registrationRequest) {
        String sceneId = registrationRequest.sceneId;
        String trainerId = registrationRequest.trainerId;

        getContext().getLog().info("Nexus received registration request for {} in {}", trainerId, sceneId);

        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(sceneId);
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(registrationRequest);
        } else {
            getContext().getLog().info("Creating sceneTrainerGroup {} for trainer {}", sceneId, trainerId);
            ActorRef<SceneTrainerGroup.Command> sceneActor =
                    getContext().spawn(SceneTrainerGroup.create(sceneId), "scene-" + sceneId);

            getContext().watchWith(sceneActor, new SceneTrainerGroupTerminated(sceneId));
            sceneActor.tell(registrationRequest);
            sceneIdToActor.put(sceneId, sceneActor);
        }
        return this;
    }

}
