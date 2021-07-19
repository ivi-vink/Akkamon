package akkamon.domain.iot.startstop;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MainStartStop extends AbstractBehavior<String> {

    static Behavior<String> create() {
        return Behaviors.setup(MainStartStop::new);
    }

    private MainStartStop(ActorContext<String> context) {
        super(context);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("start", this::start).build();
    }

    private Behavior<String> start() {
        ActorRef<String> first = getContext().spawn(StartStopActor1.create(), "first");
        first.tell("stop");
        return Behaviors.same();
    }

}
