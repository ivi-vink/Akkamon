package akkamon.domain;

import akka.actor.Timers;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InteractionHandshaker extends AbstractBehavior<InteractionHandshaker.Command> {

    public interface Command {
    }


    public static Behavior<Command> create(
            String trainerId,
            List<String> needingConfirmation,
            long requestId,
            ActorRef<AkkamonNexus.Command> replyTo,
            Duration timeout) {

        return Behaviors.setup(
                context -> Behaviors.withTimers(
                        timers -> new InteractionHandshaker(
                                context,
                                trainerId,
                                new HashSet(needingConfirmation),
                                requestId,
                                replyTo,
                                timeout,
                                timers
                        )
                )
        );

    }

    public InteractionHandshaker(ActorContext<Command> context,
                                 String trainerId,
                                 Set<String> needingConfirmation,
                                 long requestId,
                                 ActorRef<AkkamonNexus.Command> replyTo,
                                 Duration timeout,
                                 TimerScheduler<Command> timers) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return null;
    }

}
