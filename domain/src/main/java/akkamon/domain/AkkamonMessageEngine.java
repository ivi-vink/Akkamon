package akkamon.domain;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastToScene(String sceneId, String message);

    void registerTrainerSessionToScene(String sceneId, AkkamonSession session);

    void removeTrainerSessionFromScene(String sceneId, AkkamonSession session);
}
