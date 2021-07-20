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

    public static class RequestTrainerRegistration implements AkkamonNexus.Command, SceneTrainerGroup.Command {
        public String trainerId;
        public String sceneId;
        public ActorRef<TrainerRegistered> replyTo;

        public RequestTrainerRegistration(String trainerId, String sceneId, ActorRef<TrainerRegistered> replyTo) {
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.replyTo = replyTo;
        }
    }

    public static class TrainerRegistered {
        private ActorRef<Trainer.Command> trainer;

        public TrainerRegistered(ActorRef<Trainer.Command> trainer) {
            this.trainer = trainer;
        }
    }

    private static class SceneTrainerGroupTerminated implements AkkamonNexus.Command {
        public SceneTrainerGroupTerminated(String sceneId) {
        }
    }


    public static Behavior<AkkamonNexus.Command> create() {
        return Behaviors.setup(AkkamonNexus::new);
    }

    private Map<String, ActorRef<SceneTrainerGroup.Command>> sceneIdToActor = new HashMap<>();

    public AkkamonNexus(ActorContext<Command> context) {
        super(context);
        getContext().getLog().info("AkkamonNexus is spinning");
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(
                        RequestTrainerRegistration.class,
                        this::onTrainerRegistration
                )
                .build();
    }

    private AkkamonNexus onTrainerRegistration(RequestTrainerRegistration registrationRequest) {
        String sceneId = registrationRequest.sceneId;
        String trainerId = registrationRequest.trainerId;

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
