package akkamon.domain;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class Trainer extends AbstractBehavior<Trainer.Command> {

    public interface Command { }

    public static Behavior<Command> create(String sceneId, String trainerId) {
        return Behaviors.setup(context -> new Trainer(context, sceneId, trainerId));
    }

    private String sceneId;
    private String trainerId;

    private Optional<TilePos> lastValidTilePos = Optional.empty();

    public Trainer(ActorContext<Command> context, String sceneId, String trainerId) {
        super(context);
        this.sceneId = sceneId;
        this.trainerId = trainerId;
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(
                        AkkamonNexus.RequestStartMoving.class,
                        this::onStartMoving
                )
                .onMessage(
                        AkkamonNexus.RequestStopMoving.class,
                        this::onStopMoving)
                .onMessage(
                        AkkamonNexus.RequestNewTilePos.class,
                        this::onNewTilePos
                )
                .build();
    }

    private Trainer onNewTilePos(AkkamonNexus.RequestNewTilePos newTilePosRequest) {
        getContext().getLog().info("Trainer {} has new {}.", trainerId, newTilePosRequest.tilePos);
        lastValidTilePos = Optional.of(newTilePosRequest.tilePos);
        return this;
    }

    private Trainer onStopMoving(AkkamonNexus.RequestStopMoving stopMovingRequest) {
        getContext().getLog().info("Trainer {} stops to move {}.", trainerId, stopMovingRequest.direction);
        return this;
    }

    private Trainer onStartMoving(AkkamonNexus.RequestStartMoving startMovingRequest) {
        getContext().getLog().info("Trainer {} starts to move {}.", trainerId, startMovingRequest.direction);
        return this;
    }

}
