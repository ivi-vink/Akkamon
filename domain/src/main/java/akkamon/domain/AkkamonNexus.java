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
                .onMessage(
                        RequestTrainerRegistration.class,
                        this::onTrainerRegistration
                )
                .onMessage(
                        TrainerRegistered.class,
                        this::onTrainerRegistered
                )
                .build();
    }

    private AkkamonNexus onTrainerRegistered(TrainerRegistered reply) {
        // TODO test when registration fails?
        getContext().getLog().info("Adding {} to Live AkkamonSessions in Messaging Engine", reply.trainerId);
        messageEngine.registerTrainerSession(reply.trainerId, reply.session);
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
