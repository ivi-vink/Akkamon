package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

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
        private String sceneId;
        private AkkamonSession session;

        public TrainerRegistered(
                String trainerId,
                String sceneId,
                AkkamonSession session
        ) {
            this.trainerId = trainerId;
            this.sceneId = sceneId;
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
        // TODO find a way to make the command Narrower
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestHeartBeat(long requestId, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    private static class SceneTrainerGroupTerminated implements AkkamonNexus.Command {
        public SceneTrainerGroupTerminated(String sceneId) {
        }
    }

    public static class RequestTrainerOffline
            implements Command, SceneTrainerGroup.Command, Trainer.Command {
        public long requestId;
        public String trainerId;
        public String sceneId;
        public AkkamonSession session;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestTrainerOffline(long requestId, String trainerId, String sceneId, AkkamonSession session, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.session = session;
            this.replyTo = replyTo;
        }
    }

    public static class RespondTrainerOffline
            implements Command {
        public long requestId;
        public String sceneId;
        public AkkamonSession session;

        public RespondTrainerOffline(long requestId, String sceneId, AkkamonSession session) {
            this.requestId = requestId;
            this.sceneId = sceneId;
            this.session = session;
        }
    }

    public static class RespondHeartBeatQuery implements Command {

        public final long requestId;
        public final String sceneId;
        public final Map<String, MovementQueueReading> trainerMovementQueues;

        public RespondHeartBeatQuery(
                long requestId,
                String sceneId,
                Map<String, MovementQueueReading> trainerPositions) {
            this.requestId = requestId;
            this.sceneId = sceneId;
            this.trainerMovementQueues = trainerPositions;
        }
    }

    public interface MovementQueueReading { }

    public static class MovementQueue implements MovementQueueReading {
        public final Queue<Direction> value;

        public MovementQueue(Queue<Direction> value) {
            this.value = value;
        }
    }

    public enum MovementQueueEmpty implements MovementQueueReading {
        INSTANCE
    }

    public enum TrainerOffline implements MovementQueueReading {
        INSTANCE
    }

    public enum TrainerTimedOut implements MovementQueueReading {
        INSTANCE
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
                .onMessage(RequestTrainerOffline.class, this::onTrainerOfflineRequest)
                .onMessage(RespondTrainerOffline.class, this::onTrainerOffline)
                .onMessage(RequestHeartBeat.class, this::onHeartBeat)
                .onMessage(RespondHeartBeatQuery.class, this::onHeartBeatQueryResponse)
                .onMessage(RequestStartMoving.class, this::onStartMoving)
                .onMessage(RequestStopMoving.class, this::onStopMoving)
                .onMessage(RequestNewTilePos.class, this::onNewTilePos)
                .build();
    }

    private AkkamonNexus onTrainerOffline(RespondTrainerOffline trainerOfflineMsg) {
        getContext().getLog().info("Removing {} from akkamon sessions!", trainerOfflineMsg.session.getTrainerId());
        messageEngine.removeTrainerSessionFromScene(trainerOfflineMsg.sceneId, trainerOfflineMsg.session);
        return this;
    }

    private AkkamonNexus onTrainerOfflineRequest(RequestTrainerOffline trainerOfflineRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                trainerOfflineRequest.sceneId
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(trainerOfflineRequest);
        } else {
            getContext().getLog().info("Ignoring trainerOffline request in scene {}, it isn't mapped to a sceneTrainerActor.", trainerOfflineRequest.sceneId);
        }
        return this;
    }

    private AkkamonNexus onHeartBeatQueryResponse(RespondHeartBeatQuery response) {
        // Turn on for logging


        // StringBuilder positions = new StringBuilder();
        // positions.append("\n" + response.sceneId.toUpperCase(Locale.ROOT) + "\n");
        // for (Map.Entry<String, MovementQueueReading> entry : response.trainerMovementQueues.entrySet()) {
        //     positions.append(entry.getKey() + ": " +entry.getValue());
        //     positions.append("\n");
        // }
        // getContext().getLog().info(String.valueOf(positions));

        messageEngine.broadCastHeartBeatToScene(response.sceneId, response.trainerMovementQueues);

       return this;
    }

    private AkkamonNexus onHeartBeat(RequestHeartBeat heartBeatRequest) {
        // TODO do some checks here?
        for (ActorRef<SceneTrainerGroup.Command> sceneGroupActor: sceneIdToActor.values()) {
            sceneGroupActor.tell(heartBeatRequest);
        }
        return this;
    }

    private AkkamonNexus onNewTilePos(RequestNewTilePos newTilePosRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                newTilePosRequest.sceneId
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(newTilePosRequest);
        } else {
            getContext().getLog().info("Ignoring newTilePos request in scene {}, it isn't mapped to a sceneTrainerActor.", newTilePosRequest.sceneId);
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
            getContext().getLog().info("Ignoring stopMove request in scene {}, it isn't mapped to a sceneTrainerActor.", stopMovingRequest.sceneId);
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
            getContext().getLog().info("Ignoring startMove request in scene {}, it isn't mapped to a sceneTrainerActor.", startMovingRequest.sceneId);
        }
        return this;
    }

    private AkkamonNexus onTrainerRegistered(TrainerRegistered reply) {
        // TODO test when registration fails?
        getContext().getLog().info("Adding {} to scene {} Live AkkamonSessions in Messaging Engine", reply.trainerId, reply.sceneId);
        reply.session.setTrainerId(reply.trainerId);
        messageEngine.registerTrainerSessionToScene(reply.sceneId, reply.session);
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
