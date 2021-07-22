package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SceneTrainerGroup extends AbstractBehavior<SceneTrainerGroup.Command> {

    public interface Command { }

    public static class TrainerOffline
            implements Command, AkkamonNexus.Command {
        public ActorRef<Trainer.Command> trainer;
        public String sceneId;
        public String trainerId;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public TrainerOffline(ActorRef<Trainer.Command> trainerActor, String sceneId, String trainerId, ActorRef<AkkamonNexus.Command> replyTo) {
            this.trainer = trainerActor;
            this.sceneId = sceneId;
            this.trainerId = trainerId;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Command> create(String TrainerGroupId) {
        return Behaviors.setup(context -> new SceneTrainerGroup(context, TrainerGroupId));
    }

    private final String sceneId;
    private final Map<String, ActorRef<Trainer.Command>> trainerIdToActor= new HashMap();

    public SceneTrainerGroup(ActorContext<Command> context, String sceneId) {
        super(context);

        this.sceneId = sceneId;

        getContext().getLog().info("SceneTrainerGroup Actor {} started", sceneId);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(
                        AkkamonNexus.RequestTrainerRegistration.class,
                        this::onTrainerRegistration
                )
                .onMessage(
                        AkkamonNexus.RequestTrainerOffline.class,
                        this::onTrainerOfflineRequest
                )
                .onMessage(
                        TrainerOffline.class,
                        this::onWatchedTrainerOffline
                )
                .onMessage(
                        AkkamonNexus.RequestStartMoving.class,
                        this::onStartMoving
                )
                .onMessage(
                        AkkamonNexus.RequestStopMoving.class,
                        this::onStopMoving
                )
                .onMessage(
                        AkkamonNexus.RequestNewTilePos.class,
                        this::onNewTilePos
                )
                .onMessage(
                        AkkamonNexus.RequestHeartBeat.class,
                        this::onHeartBeat
                )
                .build();
    }

    private SceneTrainerGroup onWatchedTrainerOffline(TrainerOffline trainerOfflineMsg) {
        trainerOfflineMsg.replyTo.tell(trainerOfflineMsg);
        trainerIdToActor.remove(trainerOfflineMsg.trainerId);
        return this;
    }

    private SceneTrainerGroup onTrainerOfflineRequest(AkkamonNexus.RequestTrainerOffline trainerOfflineRequest) {
        if (this.sceneId.equals(trainerOfflineRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(trainerOfflineRequest.trainerId);
            if (trainerActor != null) {
                trainerActor.tell(trainerOfflineRequest);
                trainerOfflineRequest.replyTo.tell(new AkkamonNexus.RespondTrainerOffline(
                        trainerOfflineRequest.requestId,
                        trainerOfflineRequest.sceneId,
                        trainerOfflineRequest.session
                ));
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring trainerOffline for trainerId {}. There is no actor mapped to it.",
                                trainerOfflineRequest.trainerId
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring trainerOffline for {}. This actor is responsible for {}.",
                            trainerOfflineRequest.sceneId,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onHeartBeat(AkkamonNexus.RequestHeartBeat heartBeatRequest) {
        Map<String, ActorRef<Trainer.Command>> trainerIdToActorCopy = new HashMap<>(this.trainerIdToActor);
        getContext()
                .spawnAnonymous(
                        HeartBeatQuery.create(
                                trainerIdToActorCopy,
                                heartBeatRequest.requestId,
                                sceneId,
                                heartBeatRequest.replyTo,
                                Duration.ofSeconds(3)
                        )
                );
        return this;
    }

    private SceneTrainerGroup onNewTilePos(AkkamonNexus.RequestNewTilePos newTilePosRequest) {
        if (this.sceneId.equals(newTilePosRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(newTilePosRequest.trainerId);
            if (trainerActor != null) {
                trainerActor.tell(newTilePosRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring newTilePos for trainerId {}. There is no actor mapped to it.",
                                newTilePosRequest.trainerId
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring newTilePos for {}. This actor is responsible for {}.",
                            newTilePosRequest.sceneId,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onStopMoving(AkkamonNexus.RequestStopMoving stopMovingRequest) {
        if (this.sceneId.equals(stopMovingRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(stopMovingRequest.trainerId);
            if (trainerActor != null) {
                trainerActor.tell(stopMovingRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring stopMovingRequest for trainerId {}. There is no actor mapped to it.",
                                stopMovingRequest.trainerId
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring stopMovingRequest for {}. This actor is responsible for {}.",
                            stopMovingRequest.sceneId,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onStartMoving(AkkamonNexus.RequestStartMoving startMovingRequest) {
        if (this.sceneId.equals(startMovingRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(startMovingRequest.trainerId);
            if (trainerActor != null) {
                trainerActor.tell(startMovingRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring startMovingRequest for trainerId {}. There is no actor mapped to it.",
                                startMovingRequest.trainerId
                                );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring startMovingRequest for {}. This actor is responsible for {}.",
                            startMovingRequest.sceneId,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onTrainerRegistration(AkkamonNexus.RequestTrainerRegistration registrationRequest) {
        if (this.sceneId.equals(registrationRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(registrationRequest.trainerId);
            if (trainerActor != null) {
                // TODO add optional already registered?
                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        registrationRequest.trainerId,
                        sceneId,
                        registrationRequest.session
                ));
            } else {
                getContext().getLog().info("Creating trainer actor for {}", registrationRequest.trainerId);
                trainerActor =
                        getContext()
                                .spawn(Trainer.create(sceneId, registrationRequest.trainerId), "trainer-" + registrationRequest.trainerId);
                getContext()
                        .watchWith(trainerActor, new SceneTrainerGroup.TrainerOffline(trainerActor, sceneId, registrationRequest.trainerId, registrationRequest.replyTo));
                trainerIdToActor.put(registrationRequest.trainerId, trainerActor);
                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        registrationRequest.trainerId,
                        sceneId,
                        registrationRequest.session
                ));
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring TrainerRegistration request for {}. This actor is responsible for {}.",
                            registrationRequest.sceneId,
                            this.sceneId);
        }
        return this;
    }

}
