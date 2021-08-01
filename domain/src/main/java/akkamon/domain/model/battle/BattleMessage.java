package akkamon.domain.model.battle;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.battle.events.BattleEvent;
import akkamon.domain.model.battle.state.BattleState;

import java.util.Queue;

public class BattleMessage {

    public Queue<BattleEvent> eventsToPlay;
    public BattleState state;

    public BattleMessage(
            AkkamonNexus.TrainerID trainerID,
            BattleEvents events,
            BattleState state
    ) {
        this.eventsToPlay = events.trainerIDEventMap.get(trainerID);
        this.state = state;
    }

}
