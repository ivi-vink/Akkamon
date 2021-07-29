package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InteractionHandshaker extends AbstractBehavior<InteractionHandshaker.Command> {

    public interface Command {
    }

    public static enum HandshakeTimeout implements Command {
        INSTANCE
    }

    static class WrappedReply implements Command {

        final AkkamonNexus.InteractionReply reply;

        WrappedReply(
                AkkamonNexus.InteractionReply reply
        ) {
            this.reply = reply;
        }

    }

    public static Behavior<Command> create(
            String trainerId,
            String type,
            List<String> needingConfirmation,
            String requestName,
            ActorRef<AkkamonNexus.Command> replyTo,
            Duration timeout) {

        return Behaviors.setup(
                context -> Behaviors.withTimers(
                        timers -> new InteractionHandshaker(
                                context,
                                trainerId,
                                type,
                                new HashSet(needingConfirmation),
                                requestName,
                                replyTo,
                                timeout,
                                timers
                        )
                ));
    }

    private Set<String> stillWaiting;

    private ActorRef<AkkamonNexus.Command> replyTo;

    private String requestName;
    private String type;

    private Set<String> alreadyWaitingForInteraction = new HashSet<>();

    public InteractionHandshaker(ActorContext<Command> context,
                                 String trainerId,
                                 String type,
                                 Set<String> needingToShakeHands,
                                 String requestName,
                                 ActorRef<AkkamonNexus.Command> replyTo,
                                 Duration timeout,
                                 TimerScheduler<Command> timers) {

        super(context);

        timers.startSingleTimer(HandshakeTimeout.INSTANCE, timeout);

        this.replyTo = replyTo;
        this.requestName = requestName;
        this.type = type;

        ActorRef<AkkamonNexus.InteractionReply> respondTrainerPositionAdapter = context.messageAdapter(AkkamonNexus.InteractionReply.class, WrappedReply::new);


        stillWaiting = needingToShakeHands;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(WrappedReply.class, this::onReply)
                .onMessage(HandshakeTimeout.class, this::onHandshakeTimeOut)
                .build();
    }

    private  Behavior<Command> onHandshakeTimeOut(HandshakeTimeout timeoutInstance) {
        getContext().getLog().info("Received {}", timeoutInstance);
        replyTo.tell(
                new AkkamonNexus.RespondInteractionHandshaker(
                        requestName,
                        false
                )
        );
        return Behaviors.stopped();
    }


    private Behavior<Command> onReply(WrappedReply w) {
        getContext().getLog().info("received reply from {}!", w.reply.trainerId);
        return respondIfAllRepliesReceived();
    }

    private Behavior<Command> respondIfAllRepliesReceived() {
        if (this.stillWaiting.isEmpty()) {
            // send response
            return Behaviors.stopped();
        } else {
            return this;
        }
    }

}
