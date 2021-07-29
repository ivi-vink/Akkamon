package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import akkamon.domain.actors.AkkamonNexus;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static akkamon.domain.actors.AkkamonNexus.*;

public class InteractionHandshaker extends AbstractBehavior<InteractionHandshaker.Command> {

    public interface Command {
    }

    public static enum HandshakeResult implements Command {
        FAIL,
        SUCCESS
    }

    public static class InteractionReply implements Command {
        public String requestName;
        public TrainerID trainerID;
        public boolean value;

        public InteractionReply(String requestName, TrainerID trainerID, boolean value) {
            this.requestName = requestName;
            this.trainerID = trainerID;
            this.value = value;
        }
    }

    public static Behavior<Command> create(
            TrainerID trainerID,
            String type,
            List<TrainerID> needingConfirmation,
            String requestName,
            ActorRef<AkkamonNexus.Command> replyTo,
            Duration timeout) {

        return Behaviors.setup(
                context -> Behaviors.withTimers(
                        timers -> new InteractionHandshaker(
                                context,
                                trainerID,
                                type,
                                new HashSet(needingConfirmation),
                                requestName,
                                replyTo,
                                timeout,
                                timers
                        )
                ));
    }

    private Set<TrainerID> stillWaiting;

    private ActorRef<AkkamonNexus.Command> replyTo;

    private String requestName;
    private String type;

    private Set<TrainerID> waitingToStartInteraction = new HashSet<>();

    public InteractionHandshaker(ActorContext<Command> context,
                                 TrainerID trainerID,
                                 String type,
                                 Set<TrainerID> needingToShakeHands,
                                 String requestName,
                                 ActorRef<AkkamonNexus.Command> replyTo,
                                 Duration timeout,
                                 TimerScheduler<Command> timers) {

        super(context);

        timers.startSingleTimer(HandshakeResult.FAIL, timeout);

        this.replyTo = replyTo;
        this.requestName = requestName;
        this.type = type;

        waitingToStartInteraction.add(trainerID);
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
                new RespondInteractionHandshaker(
                        requestName,
                        type,
                        HandshakeResult.FAIL,
                        waitingToStartInteraction
                )
        );
        return Behaviors.stopped();
    }


    private Behavior<Command> onReply(InteractionReply r) {
        getContext().getLog().info("received reply from {} with value {}!", r.trainerID, r.value);
        getContext().getLog().info(String.valueOf(stillWaiting));
        stillWaiting.remove(r.trainerID);
        getContext().getLog().info(String.valueOf(stillWaiting));
        this.waitingToStartInteraction.add(r.trainerID);
        if (r.value) {
            return respondIfAllRepliesReceived();
        } else {
            replyTo.tell(new RespondInteractionHandshaker(
                    requestName,
                    type,
                    HandshakeResult.FAIL,
                    waitingToStartInteraction
            ));
            return Behaviors.stopped();
        }
    }

    private Behavior<Command> respondIfAllRepliesReceived() {
        getContext().getLog().info(String.valueOf(stillWaiting));
        if (this.stillWaiting.isEmpty()) {
            getContext().getLog().info("Sending out interaction Start!");
            replyTo.tell(new RespondInteractionHandshaker(
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
