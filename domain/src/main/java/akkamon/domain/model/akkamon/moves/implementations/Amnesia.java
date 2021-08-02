package akkamon.domain.model.akkamon.moves.implementations;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.Stat;
import akkamon.domain.model.akkamon.moves.MoveCategory;

public class Amnesia extends Mon.Move {


    public Amnesia(String name, Mon.Type type, MoveCategory category, Stat PP, int power, int accuracy) {
        super(name, type, category, PP, power, accuracy);
    }

    @Override
    public void startTurn() {

    }

    @Override
    public void fight() {

    }

    @Override
    public void getAttacked() {

    }

    @Override
    public void useItem() {

    }

    @Override
    public void switchOut() {

    }

    @Override
    public void endTurn() {

    }
}
