package akkamon.domain;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Trainer extends AbstractBehavior<Trainer.Command> {

    public interface Command { }

    public static class ReadMovementQueue implements Command {
        final long requestId;
        final ActorRef<RespondMovementQueue> replyTo;

        public ReadMovementQueue(long requestId, ActorRef<RespondMovementQueue> replyTo) {
            this.requestId = requestId;
            this.replyTo = replyTo;
        }
    }

    public static final class RespondMovementQueue {
        final long requestId;
        final String trainerId;
        final Queue<Direction> value;

        public RespondMovementQueue(
                long requestId,
                String trainerId,
                Queue<Direction> value
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

    private Queue<Direction> movementQueue = new LinkedList<>();

    private Direction movementDirection = Direction.NONE;

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
                        ReadMovementQueue.class,
                        this::onReadMovementQueue
                )
                .onMessage(
                        AkkamonNexus.RequestTrainerOffline.class,
                        this::onTrainerOffline
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

    private Behavior<Command> onTrainerOffline(AkkamonNexus.RequestTrainerOffline trainerOfflineRequest) {
        getContext().getLog().info("Trainer {} went offline, the actor has stopped! My supervisor should handle closing my connection!");
        return Behaviors.stopped();
    }

    private Trainer onReadMovementQueue(ReadMovementQueue readTrainerPositionRequest) {
        readTrainerPositionRequest.replyTo.tell(new RespondMovementQueue(
                readTrainerPositionRequest.requestId,
                trainerId,
                new LinkedList<>(movementQueue)
        ));
        this.movementQueue.clear();
        return this;
    }

    private Trainer onNewTilePos(AkkamonNexus.RequestNewTilePos newTilePosRequest) {
        getContext().getLog().info("Trainer {} has new {}.", trainerId, newTilePosRequest.tilePos);
        if (isMoving()) {
            this.movementQueue.add(this.movementDirection);
        }
        return this;
    }

    private Trainer onStopMoving(AkkamonNexus.RequestStopMoving stopMovingRequest) {
        getContext().getLog().info("Trainer {} stops to move {}.", trainerId, stopMovingRequest.direction);
        this.movementDirection = Direction.NONE;
        return this;
    }

    private Trainer onStartMoving(AkkamonNexus.RequestStartMoving startMovingRequest) {
        getContext().getLog().info("Trainer {} starts to move {}.", trainerId, startMovingRequest.direction);
        this.movementDirection = startMovingRequest.direction;
        return this;
    }

    private boolean isMoving() {
        return this.movementDirection != Direction.NONE;
    }
}
