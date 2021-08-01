package akkamon.domain.model.battle;

import akkamon.domain.model.akkamon.Mon;

import java.util.List;

public class MonTeam {

    public Mon activeMon;
    public List<Mon> team;

    public MonTeam(List<Mon> team) {
        this.activeMon = team.get(0);
        this.team = team;
    }

}
