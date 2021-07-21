package akkamon.domain;

public interface AkkamonSession {
    void send(String event);

    void setTrainerId(String trainerId);

    String getTrainerId();
}
