package akkamon.api;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.api.models.*;
import akkamon.domain.AkkamonMessageEngine;
import akkamon.domain.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import akkamon.domain.InteractionHandshaker;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagingEngine implements AkkamonMessageEngine {

    private Map<String, Set<AkkamonSession>> sceneIdToAkkamonSessions = new HashMap<>();
    private Map<String, AkkamonSession> trainerIdToAkkamonSessions = new HashMap<>();
    private Map<String, ActorRef<InteractionHandshaker.Command>> pendingInteractioRequestToHandshaker = new HashMap<>();
    private Gson gson = new Gson();

    private ActorRef<AkkamonNexus.Command> system;

    public MessagingEngine() {
        this.system = ActorSystem.create(AkkamonNexus.create(this), "akkamon-system");

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                heartBeat();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

    }

    private void heartBeat() {
        system.tell(new AkkamonNexus.RequestHeartBeat(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                system
                ));
    }

    @Override
    public void broadCastHeartBeatToScene(String sceneId,
                                          Map<String, AkkamonNexus.MovementQueueReading> trainerPositions) {
        Set<AkkamonSession> sceneSessions = sceneIdToAkkamonSessions.get(sceneId);
        // System.out.println(sceneSessions);
        // System.out.println(sceneIdToAkkamonSessions.keySet());
        if (sceneSessions != null) {
            for (AkkamonSession session : sceneSessions) {
                Map<String, AkkamonNexus.MovementQueueReading> withoutSelf = new HashMap<>(trainerPositions);
                withoutSelf.remove(session.getTrainerId());
                HeartBeatEvent heartBeat = new HeartBeatEvent(
                        withoutSelf
                );
                String heartBeatMessage = gson.toJson(heartBeat);
                // System.out.println("Sending to " + session.getTrainerId());
                // System.out.println(heartBeatMessage);
                session.send(
                        heartBeatMessage
                );
            }
        }
    }

    @Override
    public void broadCastInteractionRequestToSessionWithTrainerIds(List<String> trainerIds, String type, String trainerId, String requestName, ActorRef<InteractionHandshaker.Command> handshaker) {
        System.out.println("Sending interaction request " + requestName);
        this.pendingInteractioRequestToHandshaker.put(requestName, handshaker);
        trainerIds.add(trainerId);
        for (String id : trainerIds) {
            AkkamonSession session = trainerIdToAkkamonSessions.get(id);
            if (session != null) {
                session.send(gson.toJson(new InteractionRequest(
                        type,
                        id,
                        requestName
                )));
            }
        }

    }

    @Override
    public void registerTrainerSessionToSceneAndTrainerIdMaps(String sceneId, AkkamonSession session) {
        System.out.println("Registering session to scene " + sceneId);
        Set<AkkamonSession> sceneIdMapping = sceneIdToAkkamonSessions.get(sceneId);
        AkkamonSession trainerIdMapping = trainerIdToAkkamonSessions.get(session.getTrainerId());
        if (sceneIdMapping != null) {
            sceneIdMapping.add(session);
        } else {
            sceneIdMapping = new HashSet<>();
            sceneIdMapping.add(session);
            sceneIdToAkkamonSessions.put(sceneId,
                    sceneIdMapping
            );
            System.out.println(sceneIdToAkkamonSessions.keySet());
        }

        trainerIdToAkkamonSessions.put(session.getTrainerId(), session);
        System.out.println(trainerIdToAkkamonSessions);

        System.out.println("Sending trainerId: " + session.getTrainerId());
        // TODO what if registration goes wrong ...
        session.send(
                gson.toJson(new TrainerRegistrationReplyEvent(session.getTrainerId()))
        );
    }

    @Override
    public void removeTrainerSessionFromScene(String sceneId, AkkamonSession session) {
        this.sceneIdToAkkamonSessions.get(sceneId).remove(session);
    }

    @Override
    public void trainerDisconnected(AkkamonSession session) {
        String sceneId = null;
        for (Map.Entry<String, Set<AkkamonSession>> entry : this.sceneIdToAkkamonSessions.entrySet()) {
            if (entry.getValue().contains(session)) sceneId = entry.getKey();
        }

        system.tell(new AkkamonNexus.RequestTrainerOffline(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                session.getTrainerId(),
                sceneId,
                session,
                system
                ));
    }

    void incoming(AkkamonSession session, String message) {
        Event event = gson.fromJson(message, Event.class);
        // TODO use session trainerId
        String sceneId = "DemoScene";

        switch (event.type) {
            case INTERACTION_REQUEST:
                System.out.println("received interaction request");
                System.out.println(event.interaction);
                system.tell(new AkkamonNexus.RequestInteraction(
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        event.interaction.type,
                        event.sceneId,
                        event.interaction.requestingTrainerId,
                        event.interaction.receivingTrainerIds,
                        system
                ));
                break;
            case START_MOVING:
                system.tell(new AkkamonNexus.RequestStartMoving(
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        session.getTrainerId(),
                        event.sceneId,
                        event.direction,
                        system
                ));
                break;
            case NEW_TILE_POS:
                system.tell(
                        new AkkamonNexus.RequestNewTilePos(
                                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                session.getTrainerId(),
                                event.sceneId,
                                event.tilePos,
                                system
                        )
                );
                break;
            case STOP_MOVING:
                system.tell(
                        new AkkamonNexus.RequestStopMoving(
                            UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                            session.getTrainerId(),
                            event.sceneId,
                            event.direction,
                            system
                        )
                );
                break;
            case TRAINER_REGISTRATION_REQUEST:
                String trainerId = String.valueOf(sceneIdToAkkamonSessions.get(sceneId) == null ? 1 : sceneIdToAkkamonSessions.get(sceneId).size() + 1);
                system.tell(new AkkamonNexus.RequestTrainerRegistration(
                        trainerId,
                        sceneId,
                        session,
                        system
                ));
                break;
            case HEART_BEAT:
                //System.out.println("My <3 beats!");
                break;
        }

    }

    private void updatePositions() {

    }
}
