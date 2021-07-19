package akkamon.domain.iot.supervision;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MainSupervize extends AbstractBehavior<String> {

    public static Behavior<String> create() {
        return Behaviors.setup(MainSupervize::new);
    }

    private MainSupervize(ActorContext<String> context) {
        super(context);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("start", this::start).build();
    }

    private Behavior<String> start() {
        ActorRef<String> supervisingActor = getContext().spawn(SupervisingActor.create(), "supervising-actor");
        supervisingActor.tell("failChild");
        return Behaviors.same();
    }

}
