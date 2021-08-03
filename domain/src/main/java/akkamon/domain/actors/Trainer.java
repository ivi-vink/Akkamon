package akkamon.domain.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkamon.domain.actors.tasks.heartbeat.Direction;
import akkamon.domain.actors.tasks.heartbeat.TilePos;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

import static akkamon.domain.actors.AkkamonNexus.*;

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
        final public long requestId;
        final public TrainerID trainerID;
        final public Queue<Direction> value;

        public RespondMovementQueue(
                long requestId,
                TrainerID trainerID,
                Queue<Direction> value
        ) {
            this.requestId = requestId;
            this.trainerID = trainerID;
            this.value = value;
        }
    }

    public static Behavior<Command> create(TrainerID trainerID) {
        return Behaviors.setup(context -> new Trainer(context, trainerID));
    }

    private TrainerID trainerID;

    private Queue<Direction> movementQueue = new LinkedList<>();

    private Direction movementDirection = Direction.NONE;

    private Optional<TilePos> lastValidTilePos = Optional.empty();

    private ActorRef<AkkamonBattle.Command> battleRef;

    public Trainer(ActorContext<Command> context, TrainerID trainerID) {
        super(context);
        this.trainerID = trainerID;
    }

    @Override
    public Receive<Command> createReceive() {
        return moving();
    }

    private Receive<Command> moving() {
        return newReceiveBuilder()
                .onMessage(
                        ReadMovementQueue.class,
                        this::onReadMovementQueue
                )
                .onMessage(
                        RequestTrainerOffline.class,
                        this::onTrainerOffline
                )
                .onMessage(
                        RequestStartMoving.class,
                        this::onStartMoving
                )
                .onMessage(
                        RequestStopMoving.class,
                        this::onStopMoving)
                .onMessage(
                        RequestNewTilePos.class,
                        this::onNewTilePos
                )
                .onMessage(
                        AkkamonNexus.BattleStart.class, this::onBattleStart
                )
                .onMessage(
                        AkkamonBattle.RequestAction.class, this::onRequestBattleAction
                )
            .build();
    }

    private Behavior<Command> battling() {
        return Behaviors.receive(Command.class)
                .onMessage(ReadMovementQueue.class, this::onReadMovementQueue)
                .onMessage(
                        AkkamonBattle.RequestAction.class, this::onRequestBattleAction
                )
                .build();
    }

    private Trainer onRequestBattleAction(AkkamonBattle.RequestAction requestAction) {
        if (battleRef != null) {
            battleRef.tell(requestAction);
        } else {
            getContext().getLog().info("trainer is not in a battle!");
        }
        return this;
    }


    private Behavior<Command> onBattleStart(AkkamonNexus.BattleStart battle) {
        getContext().getLog().info("Trainer {} now has a battle ref and has battle behavior", trainerID);
        this.battleRef = battle.ref;
        battle.ref.tell(
                battle
        );
        return battling();
    }


    private Behavior<Command> onTrainerOffline(RequestTrainerOffline trainerOfflineRequest) {
        getContext().getLog().info("Trainer {} went offline, the actor has stopped! My supervisor should handle closing my connection!");
        return Behaviors.stopped();
    }

    private Trainer onReadMovementQueue(ReadMovementQueue readTrainerPositionRequest) {
        readTrainerPositionRequest.replyTo.tell(new RespondMovementQueue(
                readTrainerPositionRequest.requestId,
                trainerID,
                new LinkedList<>(movementQueue)
        ));
        this.movementQueue.clear();
        return this;
    }

    private Trainer onNewTilePos(RequestNewTilePos newTilePosRequest) {
        // getContext().getLog().info("Trainer {} has new {}.", trainerID, newTilePosRequest.tilePos);
        if (isMoving()) {
            this.movementQueue.add(this.movementDirection);
        }
        return this;
    }

    private Trainer onStopMoving(RequestStopMoving stopMovingRequest) {
        // getContext().getLog().info("Trainer {} stops to move {}.", trainerID, stopMovingRequest.direction);
        this.movementDirection = Direction.NONE;
        return this;
    }

    private Trainer onStartMoving(RequestStartMoving startMovingRequest) {
        // getContext().getLog().info("Trainer {} starts to move {}.", trainerID, startMovingRequest.direction);
        this.movementDirection = startMovingRequest.direction;
        return this;
    }

    private boolean isMoving() {
        return this.movementDirection != Direction.NONE;
    }
}
