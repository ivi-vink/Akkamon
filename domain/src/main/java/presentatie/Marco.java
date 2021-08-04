package presentatie;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Marco extends AbstractBehavior<Marco.Bericht> {

    public interface Bericht {}

    private static class MarcoBegrijptReactieZo implements Bericht {
        public String string;

        public MarcoBegrijptReactieZo(String s) {
            this.string = s;
        }
    }

    public static Behavior<Marco.Bericht> create() {
        return Behaviors.setup(context -> new Marco(context));
    }

    private boolean benalbezigOntvangen = false;

    public Marco(ActorContext<Bericht> context) {
        super(context);

        ActorRef<Bart.Bericht> bart = getContext().spawn(Bart.create(), "bartsNaam");

        final Duration timeout = Duration.ofSeconds(10);

        getContext().getLog().info("Hey Bart, ik heb net Aurorus gerefactord kan je mijn code reviewen?");
        context.ask(
                Bart.isKlaarMetCodeReview.class,
                bart,
                timeout,
                (ActorRef<Bart.isKlaarMetCodeReview> ref) -> new Bart.reviewMijnCodeAlsjeblieft(ref),
                (reactie, throwable) -> {
                    if (reactie != null) {
                        return new MarcoBegrijptReactieZo(reactie.string);
                    } else {
                        return new MarcoBegrijptReactieZo("Code Review failed?!");
                    }
                }
        );

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        context.ask(
                Bart.isKlaarMetCodeReview.class,
                bart,
                timeout,
                (ActorRef<Bart.isKlaarMetCodeReview> ref) -> new Bart.reviewMijnCodeAlsjeblieft(ref),
                (reactie, throwable) -> {
                    if (reactie != null) {
                        return new MarcoBegrijptReactieZo(reactie.string);
                    } else {
                        return new MarcoBegrijptReactieZo("Code Review failed?!");
                    }
                }
        );
    }

    @Override
    public Receive<Bericht> createReceive() {
        return newReceiveBuilder()
                .onMessage(MarcoBegrijptReactieZo.class, this::ontvangReactie)
                .build();
    }

    private Behavior<Bericht> ontvangReactie(MarcoBegrijptReactieZo reactie) {
        getContext().getLog().info("Marco ontvangt reactie van Bart: " + reactie.string);
        return stopAlsDemoVoorbijIs();
    }

    private Behavior<Bericht> stopAlsDemoVoorbijIs() {
        if (benalbezigOntvangen) {
            return Behaviors.stopped();
        } else {
            this.benalbezigOntvangen = true;
            return this;
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello all!");
        ActorRef<Bericht> system = ActorSystem.create(Marco.create(), "presentatie-voorbeeld");
    }

}
