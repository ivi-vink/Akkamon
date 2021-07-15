package akkamon.domain;

public interface Akkamon {
    void newPlayerConnected(String name, String password);
    void updateTrainerPosition(String name, int x, int y);
}
