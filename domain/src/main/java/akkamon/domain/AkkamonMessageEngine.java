package akkamon.domain;

import akka.actor.typed.ActorRef;
import akkamon.domain.actors.AkkamonNexus;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AkkamonMessageEngine {
    // broadcasts position info to WebSocket Clients
    void broadCastHeartBeatToScene(String sceneId, Map<AkkamonNexus.TrainerID, AkkamonNexus.MovementQueueReading> trainerPositions);

    void broadCastInteractionRequestToSessionWithtrainerIDs(List<AkkamonNexus.TrainerID> trainerIDs, String type, AkkamonNexus.TrainerID trainerID, String requestName, ActorRef<InteractionHandshaker.Command> handshaker);

    void registerTrainerSessionToSceneAndtrainerIDMaps(AkkamonNexus.TrainerID trainerID, AkkamonSession session);

    void removeTrainerSessionFromScene(AkkamonNexus.TrainerID trainerID, AkkamonSession session);

    void trainerDisconnected(AkkamonSession session);

    void removeInteractionHandshaker(String requestName);

    void broadCastInteractionStart(String requestName, String interactionType, Set<AkkamonNexus.TrainerID> waitingToStartInteraction);

    void broadCastHandshakeFail(String requestName, Set<AkkamonNexus.TrainerID> waitingToStartInteraction);
}
