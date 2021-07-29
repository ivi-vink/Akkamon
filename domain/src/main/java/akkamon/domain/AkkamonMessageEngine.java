package akkamon.domain;

import akka.actor.typed.ActorRef;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastHeartBeatToScene(String sceneId, Map<String, AkkamonNexus.MovementQueueReading> trainerPositions);

    void broadCastInteractionRequestToSessionWithTrainerIds(List<String> trainerIds, String type, String trainerId, String requestName, ActorRef<InteractionHandshaker.Command> handshaker);

    void registerTrainerSessionToSceneAndTrainerIdMaps(String sceneId, AkkamonSession session);

    void removeTrainerSessionFromScene(String sceneId, AkkamonSession session);

    void trainerDisconnected(AkkamonSession session);

    void removeInteractionHandshaker(String requestName);

    void broadCastInteractionStart(String requestName, String interactionType, Set<String> waitingToStartInteraction);

    void broadCastHandshakeFail(String requestName, Set<String> waitingToStartInteraction);
}
