package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;

import java.util.ArrayList;

public class BattleInitEvent extends Event {

    public ArrayList<AkkamonNexus.TrainerID> participants;

    public BattleInitEvent(ArrayList<AkkamonNexus.TrainerID> participants) {
        this.type = EventType.BATTLE_INIT;
        this.participants = participants;
    }
}
