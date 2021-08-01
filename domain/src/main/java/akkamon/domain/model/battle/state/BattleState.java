package akkamon.domain.model.battle.state;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.battle.MonTeam;

import java.util.HashMap;
import java.util.Map;

public class BattleState {

    public Map<String, MonTeam> teams = new HashMap<>();

    public BattleState(Map<AkkamonNexus.TrainerID, MonTeam> trainerIDtoTeam) {
        for (AkkamonNexus.TrainerID trainerID : trainerIDtoTeam.keySet()) {
            this.teams.put(trainerID.id, trainerIDtoTeam.get(trainerID));
        }
    }
}
