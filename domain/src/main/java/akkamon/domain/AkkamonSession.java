package akkamon.domain;

public interface AkkamonSession {
    void send(String event);
}
