package akkamon.domain;

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

    public static enum HandshakeResult implements Command {
        FAIL,
        SUCCESS
    }

    public static class InteractionReply implements Command {
        public String requestName;
        public String trainerId;
        public String sceneId;
        public boolean value;

        public InteractionReply(String requestName, String trainerId, String sceneId, boolean value) {
            this.requestName = requestName;
            this.trainerId = trainerId;
            this.sceneId = sceneId;
            this.value = value;
        }
    }

    public static Behavior<Command> create(
            String trainerId,
            String type,
            List<String> needingConfirmation,
            String sceneId,
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
                                sceneId,
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

    private Set<String> waitingToStartInteraction = new HashSet<>();

    public InteractionHandshaker(ActorContext<Command> context,
                                 String trainerId,
                                 String type,
                                 Set<String> needingToShakeHands,
                                 String sceneId,
                                 String requestName,
                                 ActorRef<AkkamonNexus.Command> replyTo,
                                 Duration timeout,
                                 TimerScheduler<Command> timers) {

        super(context);

        timers.startSingleTimer(HandshakeResult.FAIL, timeout);

        this.replyTo = replyTo;
        this.requestName = requestName;
        this.type = type;

        waitingToStartInteraction.add(trainerId);
        stillWaiting = needingToShakeHands;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InteractionReply.class, this::onReply)
                .onMessage(HandshakeResult.class, this::onHandshakeFail)
                .build();
    }

    private  Behavior<Command> onHandshakeFail(HandshakeResult instance) {
        getContext().getLog().info("Received fail instance due to timeout!");

        replyTo.tell(
                new AkkamonNexus.RespondInteractionHandshaker(
                        requestName,
                        type,
                        HandshakeResult.FAIL,
                        waitingToStartInteraction
                )
        );
        return Behaviors.stopped();
    }


    private Behavior<Command> onReply(InteractionReply r) {
        getContext().getLog().info("received reply from {} with value {}!", r.trainerId, r.value);
        stillWaiting.remove(r.trainerId);
        this.waitingToStartInteraction.add(r.trainerId);
        if (r.value) {
            return respondIfAllRepliesReceived();
        } else {
            replyTo.tell(new AkkamonNexus.RespondInteractionHandshaker(
                    requestName,
                    type,
                    HandshakeResult.FAIL,
                    waitingToStartInteraction
            ));
            return Behaviors.stopped();
        }
    }

    private Behavior<Command> respondIfAllRepliesReceived() {
        if (this.stillWaiting.isEmpty()) {
            replyTo.tell(new AkkamonNexus.RespondInteractionHandshaker(
                    requestName,
                    type,
                    HandshakeResult.SUCCESS,
                    waitingToStartInteraction
            ));
            return Behaviors.stopped();
        } else {
            return this;
        }
    }

}
