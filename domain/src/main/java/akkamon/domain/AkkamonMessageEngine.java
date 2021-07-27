package akkamon.domain;

import java.util.List;
import java.util.Map;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastHeartBeatToScene(String sceneId, Map<String, AkkamonNexus.MovementQueueReading> trainerPositions);

    void broadCastInteractionRequestToSessionWithTrainerIds(List<String> trainerIds, String type, String trainerId, long requestId);

    void registerTrainerSessionToSceneAndTrainerIdMaps(String sceneId, AkkamonSession session);

    void removeTrainerSessionFromScene(String sceneId, AkkamonSession session);

    void trainerDisconnected(AkkamonSession session);

    void broadCastMessageToSessionsWithTrainerIds(List<String> needConfirmation, String type, String trainerId, long requestid);
}
