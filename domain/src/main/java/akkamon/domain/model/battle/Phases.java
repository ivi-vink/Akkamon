package akkamon.domain.model.battle;

public interface Phases {
    void fight();
    void getAttacked();
    void useItem();
    void switchOut();
    void switchIn();
}
