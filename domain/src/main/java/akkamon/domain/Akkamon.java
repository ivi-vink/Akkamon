package akkamon.domain;

public interface Akkamon {
    void newPlayerConnected(String name, String password);
    void updateTrainerPosition(String name, float x, float y);
}
