package akkamon.domain.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class AkkamonBattle extends AbstractBehavior<AkkamonBattle.Command> {

    public interface Command { }

    public static class BattleCreatedResponse implements AkkamonNexus.Command {

    }

    public static Behavior<Command> create(
            ActorRef<AkkamonNexus.Command> replyTo
    ) {
        return Behaviors.setup(
                context -> new AkkamonBattle(context)
        );
    }

    public AkkamonBattle(ActorContext<Command> context) {
        super(context);
    }


    @Override
    public Receive<Command> createReceive() {
        return null;
    }

}
