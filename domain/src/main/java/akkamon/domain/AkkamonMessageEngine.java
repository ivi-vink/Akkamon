package akkamon.domain;

import java.util.Map;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastHeartBeatToScene(String sceneId, Map<String, AkkamonNexus.MovementQueueReading> trainerPositions);

    void registerTrainerSessionToScene(String sceneId, AkkamonSession session);

    void removeTrainerSessionFromScene(String sceneId, AkkamonSession session);

    void trainerDisconnected(AkkamonSession session);
}
