package presentatie;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;

public class Bart extends AbstractBehavior<Bart.Bericht> {

    public interface Bericht {}

    public static class reviewMijnCodeAlsjeblieft implements Bericht {
        public ActorRef<isKlaarMetCodeReview> replyTo;
        public reviewMijnCodeAlsjeblieft(ActorRef<isKlaarMetCodeReview> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class isKlaarMetCodeReview implements Bericht, Marco.Bericht {
        public String string;
        public isKlaarMetCodeReview(String string) {
            this.string = string;
        }
    }

    public static Behavior<Bart.Bericht> create() {
        return Behaviors.setup(context -> new Bart(context));
    }

    public Bart(ActorContext<Bart.Bericht> context) {
        super(context);
    }

    @Override
    public Receive<Bart.Bericht> createReceive() {
        return newReceiveBuilder()
                .onMessage(Bart.reviewMijnCodeAlsjeblieft.class, this::reviewMarcosCode)
                .build();
    }

    private Behavior<Bericht> bezigMetCodeReview() {
        return newReceiveBuilder()
                .onMessage(Bart.reviewMijnCodeAlsjeblieft.class, this::benAlBezig)
                .build();
    }

    private Behavior<Bericht> reviewMarcosCode(reviewMijnCodeAlsjeblieft codeReviewVraag) {
        getContext().getLog().info("Bart ontvangt vraag van Marco en begint met code review, en stuurt een reactie als hij klaar is.");
        Duration seconds = Duration.ofSeconds(7);
        getContext().getSystem().scheduler().scheduleOnce(seconds, new Runnable() {
            @Override
            public void run() {
                codeReviewVraag.replyTo.tell(new isKlaarMetCodeReview("Ben klaar met de code review!"));
            }
        },  getContext().getExecutionContext());
        return bezigMetCodeReview();
    }

    private Behavior<Bericht> benAlBezig(reviewMijnCodeAlsjeblieft codeReviewVraag) {
        getContext().getLog().info("Bart ontvangt tweede keer de vraag van Marco en reageert nu gelijk.");
        codeReviewVraag.replyTo.tell(new isKlaarMetCodeReview("Ben al bezig met de code review, Life is bad."));
        return this;
    }


}
