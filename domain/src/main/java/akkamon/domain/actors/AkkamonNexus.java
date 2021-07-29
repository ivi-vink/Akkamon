package akkamon.domain.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkamon.domain.*;

import java.time.Duration;
import java.util.*;

public class AkkamonNexus extends AbstractBehavior<AkkamonNexus.Command> {


    public interface Command {}

    public static class TrainerID {
        public String id;
        public String scene;

        public TrainerID(String id, String scene) {
            this.id = id;
            this.scene = scene;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrainerID trainerID = (TrainerID) o;
            return Objects.equals(id, trainerID.id) && Objects.equals(scene, trainerID.scene);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, scene);
        }

        @Override
        public String toString() {
            return "{ " +
                    "\"id\": \"" + id + "\"," +
                    "\"scene\": \"" + scene + "\"" +
                    " }";
        }
    }

    public static class BattleStart implements SceneTrainerGroup.Command, Trainer.Command {

    }

    public static class RespondInteractionHandshaker implements Command {
        public String requestName;
        public String interactionType;
        public InteractionHandshaker.HandshakeResult result;
        public Set<TrainerID> waitingToStartInteraction;


        public RespondInteractionHandshaker(String requestName,
                                            String interactionType,
                                            InteractionHandshaker.HandshakeResult result,
                                            Set<TrainerID> waitingToStartInteraction) {
            this.requestName = requestName;
            this.interactionType = interactionType;
            this.result = result;
            this.waitingToStartInteraction = waitingToStartInteraction;
        }
    }

    public static class RequestInteraction
            implements Command {

        public long requestId;
        public String type;
        public String sceneId;
        public TrainerID trainerID;
        public List<TrainerID> forwardTo;
        public ActorRef<Command> replyTo;

        public RequestInteraction(long requestId, String type, TrainerID trainerID, List<TrainerID> forwardTo, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.type = type;
            this.trainerID = trainerID;
            this.forwardTo = forwardTo;
            this.replyTo = replyTo;
        }
    }

    public static class RequestTrainerRegistration
            implements AkkamonNexus.Command, SceneTrainerGroup.Command {
        public String trainerName;
        public String sceneId;
        public AkkamonSession session;
        public ActorRef<Command> replyTo;

        public RequestTrainerRegistration(
                String trainerName,
                String sceneId,
                AkkamonSession session,
                ActorRef<Command> replyTo
        ) {
            this.trainerName = trainerName;
            this.sceneId = sceneId;
            this.session = session;
            this.replyTo = replyTo;
        }
    }

    public static class TrainerRegistered implements Command {
        private TrainerID trainerID;
        private AkkamonSession session;

        public TrainerRegistered(
                TrainerID trainerID,
                AkkamonSession session
        ) {
            this.trainerID = trainerID;
            this.session = session;
        }
    }

    public static class RequestStartMoving
            implements Command, SceneTrainerGroup.Command, Trainer.Command {
        public long requestId;
        public TrainerID trainerID;
        public Direction direction;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestStartMoving(long requestId, AkkamonNexus.TrainerID trainerID, Direction direction, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.trainerID = trainerID;
            this.direction = direction;
            this.replyTo = replyTo;
        }
    }

    public static class RequestStopMoving
            implements Command, SceneTrainerGroup.Command, Trainer.Command  {
        public long requestId;
        public TrainerID trainerID;
        public Direction direction;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestStopMoving(
                long requestId,
                TrainerID trainerID,
                Direction direction,
                ActorRef<AkkamonNexus.Command> replyTo) {
            this.requestId = requestId;
            this.trainerID = trainerID;
            this.direction = direction;
            this.replyTo = replyTo;
        }
    }

    public static class RequestNewTilePos
            implements Command, SceneTrainerGroup.Command, Trainer.Command {
        public long requestId;
        public TrainerID trainerID;
        public TilePos tilePos;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestNewTilePos(long requestId, TrainerID trainerID, TilePos tilePos, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.trainerID = trainerID;
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
        public TrainerID trainerID;
        public AkkamonSession session;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public RequestTrainerOffline(long requestId, TrainerID trainerID, String sceneId, AkkamonSession session, ActorRef<Command> replyTo) {
            this.requestId = requestId;
            this.trainerID = trainerID;
            this.session = session;
            this.replyTo = replyTo;
        }
    }

    public static class RespondTrainerOffline
            implements Command {
        public long requestId;
        public TrainerID trainerID;
        public AkkamonSession session;

        public RespondTrainerOffline(long requestId, TrainerID trainerID, AkkamonSession session) {
            this.requestId = requestId;
            this.trainerID = trainerID;
            this.session = session;
        }
    }

    public static class RespondHeartBeatQuery implements Command {

        public final long requestId;
        public final String sceneId;
        public final Map<TrainerID, MovementQueueReading> trainerMovementQueues;

        public RespondHeartBeatQuery(
                long requestId,
                String sceneId,
                Map<TrainerID, MovementQueueReading> trainerPositions) {
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

                .onMessage(RequestInteraction.class, this::onInteractionRequest)
                .onMessage(RespondInteractionHandshaker.class, this::onInteractionHandshakerResponse)

                .onMessage(AkkamonBattle.BattleCreatedResponse.class, this::onBattleCreatedResponse)
                .build();
    }

    private AkkamonNexus onBattleCreatedResponse(AkkamonBattle.BattleCreatedResponse r) {
        getContext().getLog().info("Created battle between {} and {}, they should now only be listening to battle commands!");
        return this;
    }

    private Behavior<Command> onInteractionHandshakerResponse(RespondInteractionHandshaker r) {
        this.messageEngine.removeInteractionHandshaker(r.requestName);

        if (r.result.equals(InteractionHandshaker.HandshakeResult.SUCCESS)) {
            messageEngine.broadCastInteractionStart(r.requestName, r.interactionType, r.waitingToStartInteraction);

            switch (r.interactionType) {
                case "battle":
                    getContext().spawn(AkkamonBattle.create(
                                getContext().getSelf()
                            ),
                            r.requestName);
                    break;
            }

        } else if (r.result.equals(InteractionHandshaker.HandshakeResult.FAIL)) {
            messageEngine.broadCastHandshakeFail(r.requestName, r.waitingToStartInteraction);
        }
        return this;
    }

    private AkkamonNexus onInteractionRequest(RequestInteraction interactionRequest) {
        List<TrainerID> needConfirmation = interactionRequest.forwardTo;

        getContext().getLog().info("Creating interactionHandshaker of type {} from {} to {} ", interactionRequest.type, interactionRequest.trainerID, needConfirmation);

        String requestName = "interaction-handshaker-" + interactionRequest.type + "-" + interactionRequest.trainerID.id + "-" + interactionRequest.requestId;

        ActorRef<InteractionHandshaker.Command> handshaker = getContext().spawn(InteractionHandshaker.create(
                                interactionRequest.trainerID,
                                interactionRequest.type,
                                interactionRequest.forwardTo,
                                requestName,
                                interactionRequest.replyTo,
                                Duration.ofSeconds(60)
                        ), requestName);

        messageEngine.broadCastInteractionRequestToSessionWithtrainerIDs(new ArrayList<>(needConfirmation), interactionRequest.type, interactionRequest.trainerID, requestName, handshaker);
        return this;
    }

    private AkkamonNexus onTrainerOffline(RespondTrainerOffline trainerOfflineMsg) {
        getContext().getLog().info("Removing {} from akkamon sessions!", trainerOfflineMsg.session.gettrainerID());
        messageEngine.removeTrainerSessionFromScene(trainerOfflineMsg.trainerID, trainerOfflineMsg.session);
        return this;
    }

    private AkkamonNexus onTrainerOfflineRequest(RequestTrainerOffline trainerOfflineRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                trainerOfflineRequest.trainerID.scene
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(trainerOfflineRequest);
        } else {
            getContext().getLog().info("Ignoring trainerOffline request in scene {}, it isn't mapped to a sceneTrainerActor.", trainerOfflineRequest.trainerID.scene);
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
                newTilePosRequest.trainerID.scene
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(newTilePosRequest);
        } else {
            getContext().getLog().info("Ignoring newTilePos request in scene {}, it isn't mapped to a sceneTrainerActor.", newTilePosRequest.trainerID.scene);
        }
        return this;
    }

    private AkkamonNexus onStopMoving(RequestStopMoving stopMovingRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                stopMovingRequest.trainerID.scene
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(stopMovingRequest);
        } else {
            getContext().getLog().info("Ignoring stopMove request in scene {}, it isn't mapped to a sceneTrainerActor.", stopMovingRequest.trainerID.scene);
        }
        return this;
    }

    private AkkamonNexus onStartMoving(RequestStartMoving startMovingRequest) {
        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(
                startMovingRequest.trainerID.scene
        );
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(startMovingRequest);
        } else {
            getContext().getLog().info("Ignoring startMove request in scene {}, it isn't mapped to a sceneTrainerActor.", startMovingRequest.trainerID.scene);
        }
        return this;
    }

    private AkkamonNexus onTrainerRegistered(TrainerRegistered reply) {
        // TODO test when registration fails?
        getContext().getLog().info("Adding {} to scene {} Live AkkamonSessions in Messaging Engine", reply.trainerID, reply.trainerID.scene);
        reply.session.settrainerID(reply.trainerID);
        messageEngine.registerTrainerSessionToSceneAndtrainerIDMaps(reply.trainerID, reply.session);
        return this;
    }

    private AkkamonNexus onTrainerRegistration(RequestTrainerRegistration registrationRequest) {
        String sceneId = registrationRequest.sceneId;
        String trainerName = registrationRequest.trainerName;

        getContext().getLog().info("Nexus received registration request for {} in {}", trainerName, sceneId);

        ActorRef<SceneTrainerGroup.Command> sceneTrainerGroup = sceneIdToActor.get(sceneId);
        if (sceneTrainerGroup != null) {
            sceneTrainerGroup.tell(registrationRequest);
        } else {
            getContext().getLog().info("Creating sceneTrainerGroup {} for trainer {}", sceneId, trainerName);
            ActorRef<SceneTrainerGroup.Command> sceneActor =
                    getContext().spawn(SceneTrainerGroup.create(sceneId), "scene-" + sceneId);

            getContext().watchWith(sceneActor, new SceneTrainerGroupTerminated(sceneId));
            sceneActor.tell(registrationRequest);
            sceneIdToActor.put(sceneId, sceneActor);
        }
        return this;
    }

}
