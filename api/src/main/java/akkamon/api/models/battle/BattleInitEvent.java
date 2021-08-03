package akkamon.api.models.battle;

import akkamon.api.models.Event;
import akkamon.api.models.EventType;
import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.battle.BattleMessage;
import akkamon.domain.model.battle.state.BattleState;

import java.util.ArrayList;
import java.util.Map;

public class BattleInitEvent extends Event {

    public ArrayList<AkkamonNexus.TrainerID> participants;
    public BattleMessage message;

    public BattleInitEvent(ArrayList<AkkamonNexus.TrainerID> participants, BattleMessage introductionMessage) {
        this.type = EventType.BATTLE_INIT;
        this.participants = participants;
        this.message = introductionMessage;
    }
}
