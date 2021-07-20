package akkamon.domain;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Trainer extends AbstractBehavior<Trainer.Command> {

    public interface Command { }

    public static Behavior<Command> create(String sceneId, String trainerId) {
        return Behaviors.setup(context -> new Trainer(context, sceneId, trainerId));
    }

    private String sceneId;
    private String trainerId;

    public Trainer(ActorContext<Command> context, String sceneId, String trainerId) {
        super(context);
        this.sceneId = sceneId;
        this.trainerId = trainerId;
    }

    @Override
    public Receive<Command> createReceive() {
        return null;
    }

}
