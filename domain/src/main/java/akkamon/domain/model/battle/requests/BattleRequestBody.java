package akkamon.domain.model.battle.requests;

import akkamon.domain.model.akkamon.Mon;

public class BattleRequestBody {
    public RequestBattleAction requestAction;

    public Mon.Move move;

    @Override
    public String toString() {
        return "BattleRequestBody{" +
                "requestAction=" + requestAction +
                ", move=" + move +
                '}';
    }
}
