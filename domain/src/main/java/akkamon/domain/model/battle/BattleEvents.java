package akkamon.domain.model.battle;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.battle.events.BattleEvent;

import java.util.*;

public class BattleEvents {

    public final Map<AkkamonNexus.TrainerID, Queue<BattleEvent>> trainerIDEventMap = new HashMap<>();

    public BattleEvents(Set<AkkamonNexus.TrainerID> participants) {
        for (AkkamonNexus.TrainerID trainerID : participants) {
            trainerIDEventMap.put(trainerID, new LinkedList<>());
        }
    }
}
