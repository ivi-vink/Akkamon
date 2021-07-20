package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.HashMap;
import java.util.Map;

public class SceneTrainerGroup extends AbstractBehavior<SceneTrainerGroup.Command> {

    public interface Command { }

    private class TrainerOffline implements Command {
        public ActorRef<Trainer.Command> trainer;
        public String sceneId;
        public String trainerId;

        public TrainerOffline(ActorRef<Trainer.Command> trainerActor, String sceneId, String trainerId) {
            this.trainer = trainerActor;
            this.sceneId = sceneId;
            this.trainerId = trainerId;
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
                        this::onTrainerRegistration)
                .build();
    }

    private SceneTrainerGroup onTrainerRegistration(AkkamonNexus.RequestTrainerRegistration registrationRequest) {
        if (this.sceneId.equals(registrationRequest.sceneId)) {
            ActorRef<Trainer.Command> trainerActor = trainerIdToActor.get(registrationRequest.trainerId);
            if (trainerActor != null) {
                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        registrationRequest.trainerId,
                        registrationRequest.session
                ));
            } else {
                getContext().getLog().info("Creating trainer actor for {}", registrationRequest.trainerId);
                trainerActor =
                        getContext()
                                .spawn(Trainer.create(sceneId, registrationRequest.trainerId), "trainer-" + registrationRequest.trainerId);
                getContext()
                        .watchWith(trainerActor, new SceneTrainerGroup.TrainerOffline(trainerActor, sceneId, registrationRequest.trainerId));
                trainerIdToActor.put(registrationRequest.trainerId, trainerActor);
                registrationRequest.replyTo.tell(new AkkamonNexus.TrainerRegistered(
                        registrationRequest.trainerId,
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
