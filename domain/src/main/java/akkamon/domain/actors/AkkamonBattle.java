package akkamon.domain.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akkamon.domain.model.battle.BattleEngine;
import akkamon.domain.model.battle.BattleMessage;
import akkamon.domain.model.battle.requests.BattleRequestBody;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AkkamonBattle extends AbstractBehavior<AkkamonBattle.Command> {

    private BattleEngine engine;

    public interface Command { }

    public static class RequestAction 
            implements AkkamonNexus.Command, SceneTrainerGroup.Command, Trainer.Command, Command {

        public AkkamonNexus.TrainerID trainerID;
        public BattleRequestBody body;

        public RequestAction(AkkamonNexus.TrainerID trainerID, BattleRequestBody battleRequestBody) {
            this.trainerID = trainerID;
            this.body = battleRequestBody;
        }

        @Override
        public String toString() {
            return "RequestAction{" +
                    "trainerID=" + trainerID +
                    ", body=" + body +
                    '}';
        }
    }
    
    public static class BattleCreatedResponse implements AkkamonNexus.Command {
        public Set<AkkamonNexus.TrainerID> participants;
        public Map<AkkamonNexus.TrainerID, BattleMessage> initState;

        public BattleCreatedResponse(Set<AkkamonNexus.TrainerID> participants,
                                     Map<AkkamonNexus.TrainerID, BattleMessage> initialisedState) {
            this.participants = participants;
            this.initState = initialisedState;
        }

    }

    public static Behavior<Command> create(
            Set<AkkamonNexus.TrainerID> participants,
            ActorRef<AkkamonNexus.Command> replyTo
    ) {
        return Behaviors.setup(
                context -> new AkkamonBattle(context, participants, replyTo)
        );
    }

    private Set<AkkamonNexus.TrainerID> participants;

    private Set<AkkamonNexus.TrainerID> needLink;

    private ActorRef<AkkamonNexus.Command> replyTo;

    private Set<RequestAction> nextTurnActions = new HashSet<>();

    public AkkamonBattle(
            ActorContext<Command> context,
            Set<AkkamonNexus.TrainerID> participants,
            ActorRef<AkkamonNexus.Command> replyTo
    ) {
        super(context);

        this.participants = participants;

        this.needLink = new HashSet(participants);

        this.replyTo = replyTo;
    }


    @Override
    public Receive<Command> createReceive() {
        return initial();
    }

    private Receive<Command> initial() {
        return newReceiveBuilder()
                .onMessage(AkkamonNexus.BattleStart.class, this::onBattleStart)
                .build();
    }

    private Behavior<Command> onRequestBattleAction(RequestAction requestAction) {
        nextTurnActions.add(requestAction);
        if (nextTurnActions.size() == participants.size()) {
            engine.play(nextTurnActions);
            nextTurnActions.clear();
        } else {
            getContext().getLog().info("Received battle action request of {} but not playing until all received, {}", requestAction.trainerID, requestAction.body);
        }
        return inProgress();
    }

    private Behavior<Command> onBattleStart(AkkamonNexus.BattleStart start) {
        AkkamonNexus.TrainerID linkingTrainer = start.trainerID;
        getContext().getLog().info(String.valueOf(needLink));
        needLink.remove(linkingTrainer);
        getContext().getLog().info(String.valueOf(needLink));
        if (needLink.isEmpty()) {
            getContext().getLog().info("Sending Battle Created Response!");
            this.engine = new BattleEngine(participants);
            replyTo.tell(
                    new BattleCreatedResponse(
                            participants,
                            engine.initialise()
                            )
            );
            return inProgress();
        } else {
            getContext().getLog().info("Need to link trainers {}", needLink);
            return this;
        }
    }

    private Behavior<Command> inProgress() {
        return Behaviors.receive(Command.class)
                .onMessage(RequestAction.class, this::onRequestBattleAction)
                .build();
    }

}
