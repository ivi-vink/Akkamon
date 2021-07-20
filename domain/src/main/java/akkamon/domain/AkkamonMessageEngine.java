package akkamon.domain;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastGridPosition();

    void registerTrainerSession(String trainerId, AkkamonSession session);

    void removeTrainerSession(String trainerId, AkkamonSession session);
}
