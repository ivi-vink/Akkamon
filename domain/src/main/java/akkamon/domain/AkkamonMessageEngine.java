package akkamon.domain;

import java.util.List;
import java.util.Map;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastHeartBeatToScene(String sceneId, Map<String, AkkamonNexus.MovementQueueReading> trainerPositions);

    void broadCastInteractionRequestToSessionWithTrainerIds(List<String> trainerIds, String type, String trainerId, String requestName);

    void registerTrainerSessionToSceneAndTrainerIdMaps(String sceneId, AkkamonSession session);

    void removeTrainerSessionFromScene(String sceneId, AkkamonSession session);

    void trainerDisconnected(AkkamonSession session);

}
