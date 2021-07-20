package akkamon.api;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastGridPosition();
}
