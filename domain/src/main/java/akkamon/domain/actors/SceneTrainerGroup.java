package akkamon.domain.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkamon.domain.HeartBeatQuery;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class SceneTrainerGroup extends AbstractBehavior<SceneTrainerGroup.Command> {

    public interface Command { }

    public static class TrainerOffline
            implements Command, AkkamonNexus.Command {
        public ActorRef<Trainer.Command> trainer;
        public String sceneId;
        public String trainerID;
        public ActorRef<AkkamonNexus.Command> replyTo;

        public TrainerOffline(ActorRef<Trainer.Command> trainerActor, String sceneId, String trainerID, ActorRef<AkkamonNexus.Command> replyTo) {
            this.trainer = trainerActor;
            this.sceneId = sceneId;
            this.trainerID = trainerID;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Command> create(String TrainerGroupId) {
        return Behaviors.setup(context -> new SceneTrainerGroup(context, TrainerGroupId));
    }

    private final String sceneId;
    private final Map<AkkamonNexus.TrainerID, ActorRef<Trainer.Command>> trainerIDToActor= new HashMap();

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
                .onMessage(AkkamonNexus.BattleStart.class,
                        this::onBattleStarting)
                .build();
    }

    private SceneTrainerGroup onBattleStarting(AkkamonNexus.BattleStart battle) {
        ActorRef<Trainer.Command> trainer = this.trainerIDToActor.get(battle.trainerID);
        if (trainer != null) {
            trainer.tell(battle);
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring battle for trainerID {}. There is no actor mapped to it.",
                            battle.trainerID
                    );
        }
        return this;
    }

    private SceneTrainerGroup onWatchedTrainerOffline(TrainerOffline trainerOfflineMsg) {
        trainerOfflineMsg.replyTo.tell(trainerOfflineMsg);
        trainerIDToActor.remove(trainerOfflineMsg.trainerID);
        return this;
    }

    private SceneTrainerGroup onTrainerOfflineRequest(AkkamonNexus.RequestTrainerOffline trainerOfflineRequest) {
        if (this.sceneId.equals(trainerOfflineRequest.trainerID.scene)) {
            ActorRef<Trainer.Command> trainerActor = trainerIDToActor.get(trainerOfflineRequest.trainerID);
            if (trainerActor != null) {
                trainerActor.tell(trainerOfflineRequest);
                trainerOfflineRequest.replyTo.tell(new AkkamonNexus.RespondTrainerOffline(
                        trainerOfflineRequest.requestId,
                        trainerOfflineRequest.trainerID,
                        trainerOfflineRequest.session
                ));
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring trainerOffline for trainerID {}. There is no actor mapped to it.",
                                trainerOfflineRequest.trainerID
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring trainerOffline for {}. This actor is responsible for {}.",
                            trainerOfflineRequest.trainerID.scene,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onHeartBeat(AkkamonNexus.RequestHeartBeat heartBeatRequest) {
        Map<AkkamonNexus.TrainerID, ActorRef<Trainer.Command>> trainerIDToActorCopy = new HashMap<>(this.trainerIDToActor);
        getContext()
                .spawnAnonymous(
                        HeartBeatQuery.create(
                                trainerIDToActorCopy,
                                heartBeatRequest.requestId,
                                sceneId,
                                heartBeatRequest.replyTo,
                                Duration.ofSeconds(3)
                        )
                );
        return this;
    }

    private SceneTrainerGroup onNewTilePos(AkkamonNexus.RequestNewTilePos newTilePosRequest) {
        if (this.sceneId.equals(newTilePosRequest.trainerID.scene)) {
            ActorRef<Trainer.Command> trainerActor = trainerIDToActor.get(newTilePosRequest.trainerID);
            if (trainerActor != null) {
                trainerActor.tell(newTilePosRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring newTilePos for trainerID {}. There is no actor mapped to it.",
                                newTilePosRequest.trainerID
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring newTilePos for {}. This actor is responsible for {}.",
                            newTilePosRequest.trainerID.scene,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onStopMoving(AkkamonNexus.RequestStopMoving stopMovingRequest) {
        if (this.sceneId.equals(stopMovingRequest.trainerID.scene)) {
            ActorRef<Trainer.Command> trainerActor = trainerIDToActor.get(stopMovingRequest.trainerID);
            if (trainerActor != null) {
                trainerActor.tell(stopMovingRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring stopMovingRequest for trainerID {}. There is no actor mapped to it.",
                                stopMovingRequest.trainerID
                        );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring stopMovingRequest for {}. This actor is responsible for {}.",
                            stopMovingRequest.trainerID,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onStartMoving(AkkamonNexus.RequestStartMoving startMovingRequest) {
        if (this.sceneId.equals(startMovingRequest.trainerID.scene)) {
            ActorRef<Trainer.Command> trainerActor = trainerIDToActor.get(startMovingRequest.trainerID);
            if (trainerActor != null) {
                trainerActor.tell(startMovingRequest);
            } else {
                getContext()
                        .getLog()
                        .warn(
                                "Ignoring startMovingRequest for trainerID {}. There is no actor mapped to it.",
                                startMovingRequest.trainerID
                                );
            }
        } else {
            getContext()
                    .getLog()
                    .warn(
                            "Ignoring startMovingRequest for {}. This actor is responsible for {}.",
                            startMovingRequest.trainerID.scene,
                            this.sceneId);
        }
        return this;
    }

    private SceneTrainerGroup onTrainerRegistration(AkkamonNexus.RequestTrainerRegistration registrationRequest) {

        AkkamonNexus.TrainerID existingOrNewTrainerID = new AkkamonNexus.TrainerID(registrationRequest.trainerName, this.sceneId);

        if (this.sceneId.equals(registrationRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIDToActor.get(existingOrNewTrainerID);
            if (trainerActor != null) {
                // TODO add optional already registered?
                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        existingOrNewTrainerID,
                        registrationRequest.session
                ));
            } else {
                getContext().getLog().info("Creating trainer actor for {}", registrationRequest.trainerName);
                trainerActor =
                        getContext()
                                .spawn(Trainer.create(existingOrNewTrainerID), "trainer-" + existingOrNewTrainerID.id);
                getContext()
                        .watchWith(trainerActor, new TrainerOffline(trainerActor, sceneId, registrationRequest.trainerName, registrationRequest.replyTo));

                trainerIDToActor.put(existingOrNewTrainerID, trainerActor);

                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        existingOrNewTrainerID,
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
