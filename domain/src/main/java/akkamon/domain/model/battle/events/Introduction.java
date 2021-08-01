package akkamon.domain.model.battle.events;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.model.battle.MonTeam;

import java.util.List;
import java.util.Map;

public class Introduction extends BattleEvent {

    public Introduction(
    ) {
        this.id = BattleEventIds.INTRODUCTION;
    }
}
