package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Optional;

public class Trainer extends AbstractBehavior<Trainer.Command> {

    public interface Command { }

    public static class ReadTrainerPosition implements Command {
        final long requestId;
        final ActorRef<RespondTrainerPosition> replyTo;

        public ReadTrainerPosition(long requestId, ActorRef<RespondTrainerPosition> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    public static final class RespondTrainerPosition {
        final long requestId;
        final String trainerId;
        final Optional<TilePos> value;

        public RespondTrainerPosition(
                long requestId,
                String trainerId,
                Optional<TilePos> value
        ) {
            this.requestId = requestId;
            this.trainerId = trainerId;
            this.value = value;
        }
    }

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
                        ReadTrainerPosition.class,
                        this::onReadTrainerPosition
                )
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

    private Trainer onReadTrainerPosition(ReadTrainerPosition readTrainerPositionRequest) {
        readTrainerPositionRequest.replyTo.tell(new RespondTrainerPosition(
                readTrainerPositionRequest.requestId,
                trainerId,
                lastValidTilePos
        ));
        return this;
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
