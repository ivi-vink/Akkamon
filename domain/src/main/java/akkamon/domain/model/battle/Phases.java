package akkamon.domain.model.battle;

public interface Phases {
    void startTurn();
    void fight();
    void getAttacked();
    void useItem();
    void switchOut();
    void endTurn();
}
