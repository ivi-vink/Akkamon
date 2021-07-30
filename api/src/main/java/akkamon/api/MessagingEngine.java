package akkamon.api;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.api.models.*;
import akkamon.domain.AkkamonMessageEngine;
import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import akkamon.domain.InteractionHandshaker;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagingEngine implements AkkamonMessageEngine {

    // nexus: a connection or series of connections linking two or more things.
    private ActorRef<AkkamonNexus.Command> nexus;

    private Map<String, Set<AkkamonSession>> sceneIdToAkkamonSessions = new HashMap<>();
    private Map<AkkamonNexus.TrainerID, AkkamonSession> trainerIDToAkkamonSessions = new HashMap<>();
    private Map<String, ActorRef<InteractionHandshaker.Command>> pendingInteractioRequestToHandshaker = new HashMap<>();

    private Gson gson = new Gson();

    public MessagingEngine() {
        this.nexus = ActorSystem.create(AkkamonNexus.create(this), "akkamon-system");

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                heartBeat();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

    }

    private void heartBeat() {
        for (AkkamonSession session : trainerIDToAkkamonSessions.values()) {
            session.send(
                    gson.toJson(
                            new HeartBeatEvent(null)
                    )
            );
        }
        nexus.tell(new AkkamonNexus.RequestHeartBeat(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                nexus
                ));
    }

    @Override
    public void broadCastHeartBeatToScene(String sceneId, Map<AkkamonNexus.TrainerID, AkkamonNexus.MovementQueueReading> trainerPositions) {

        Set<AkkamonSession> sceneSessions = sceneIdToAkkamonSessions.get(sceneId);
        // System.out.println(sceneSessions);
        // System.out.println(sceneIdToAkkamonSessions.keySet());
        if (sceneSessions != null) {
            for (AkkamonSession session : sceneSessions) {
                Map<AkkamonNexus.TrainerID, AkkamonNexus.MovementQueueReading> withoutSelf = new HashMap<>(trainerPositions);
                withoutSelf.remove(session.gettrainerID());

                HeartBeatEvent heartBeat = new HeartBeatEvent(
                        withoutSelf
                );

                String heartBeatMessage = gson.toJson(heartBeat);
                // System.out.println("Sending to " + session.gettrainerID());
                // System.out.println(heartBeatMessage);
                session.send(
                        heartBeatMessage
                );
            }
        }
    }

    @Override
    public void broadCastInteractionRequestToSessionWithtrainerIDs(
            List<AkkamonNexus.TrainerID> trainerIDs,
            String type,
            AkkamonNexus.TrainerID trainerID,
            String requestName,
            ActorRef<InteractionHandshaker.Command> handshaker) {

        System.out.println("Sending interaction request " + requestName);
        this.pendingInteractioRequestToHandshaker.put(requestName, handshaker);
        trainerIDs.add(trainerID);

        for (AkkamonNexus.TrainerID id : trainerIDs) {
            AkkamonSession session = trainerIDToAkkamonSessions.get(id);
            if (session != null) {
                session.send(gson.toJson(new OutgoingInteractionRequest(
                        type,
                        id,
                        requestName
                )));
            }
        }

    }



    @Override
    public void registerTrainerSessionToSceneAndtrainerIDMaps(AkkamonNexus.TrainerID trainerID, AkkamonSession session) {
        System.out.println("Registering session to scene " + trainerID.scene);

        Set<AkkamonSession> sceneIdMapping = sceneIdToAkkamonSessions.get(trainerID.scene);
        // AkkamonSession trainerIDMapping = trainerIDToAkkamonSessions.get(session.gettrainerID());

        if (sceneIdMapping != null) {
            sceneIdMapping.add(session);
        } else {
            sceneIdMapping = new HashSet<>();
            sceneIdMapping.add(session);
            sceneIdToAkkamonSessions.put(trainerID.scene,
                    sceneIdMapping
            );
            System.out.println(sceneIdToAkkamonSessions.keySet());
        }

        trainerIDToAkkamonSessions.put(session.gettrainerID(), session);
        System.out.println(trainerIDToAkkamonSessions);

        System.out.println("Sending trainerID: " + session.gettrainerID());
        // TODO what if registration goes wrong ...
        session.send(
                gson.toJson(new TrainerRegistrationReplyEvent(session.gettrainerID()))
        );
    }

    @Override
    public void removeTrainerSessionFromScene(AkkamonNexus.TrainerID sceneId, AkkamonSession session) {
        this.sceneIdToAkkamonSessions.get(sceneId).remove(session);
    }

    @Override
    public void trainerDisconnected(AkkamonSession session) {
        String sceneId = null;
        for (Map.Entry<String, Set<AkkamonSession>> entry : this.sceneIdToAkkamonSessions.entrySet()) {
            if (entry.getValue().contains(session)) sceneId = entry.getKey();
        }

        nexus.tell(new AkkamonNexus.RequestTrainerOffline(
                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                session.gettrainerID(),
                sceneId,
                session,
                nexus
                ));
    }

    @Override
    public void removeInteractionHandshaker(String requestName) {
        this.pendingInteractioRequestToHandshaker.remove(requestName);
    }

    @Override
    public void broadCastInteractionStart(String requestName, String interactionType, Set<AkkamonNexus.TrainerID> waitingToStartInteraction) {
        for (AkkamonNexus.TrainerID trainerID : waitingToStartInteraction) {

            AkkamonSession session = trainerIDToAkkamonSessions.get(trainerID);
            session.send(gson.toJson(
                    new InteractionStartEvent(
                            requestName,
                            interactionType
                    )
            ));
        }

    }

    @Override
    public void broadCastHandshakeFail(String requestName, Set<AkkamonNexus.TrainerID> waitingToStartInteraction) {
        System.out.println("Handshake fail not implemented yet!");
    }

    @Override
    public void broadCastBattleStart(Set<AkkamonNexus.TrainerID> participants) {
        System.out.println("Sending battle start event!!");
        for (AkkamonNexus.TrainerID trainerID : participants) {
            AkkamonSession session = trainerIDToAkkamonSessions.get(trainerID);
            Set<AkkamonNexus.TrainerID> withoutself = new HashSet<>(participants);
            withoutself.remove(trainerID);
            if (session != null) {
                session.send(
                        gson.toJson(
                                new BattleInitEvent(new ArrayList<>(withoutself))
                        )
                );
            }
        }
    }

    void incoming(AkkamonSession session, String message) {
        System.out.println(message);
        Event event = gson.fromJson(message, Event.class);
        if (event == null) {
            System.out.println("Received non-supported message DTO.");
            return;
        }
        // TODO use session trainerID
        String sceneId = "DemoScene";


        switch (event.type) {
            case INTERACTION_REPLY:
                System.out.println("received interaction reply!");
                sendToHandshaker(event.requestName, event.trainerID, event.value);
                break;
            case INTERACTION_REQUEST:
                System.out.println("received interaction request");
                System.out.println(event.interaction);
                nexus.tell(new AkkamonNexus.RequestInteraction(
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        event.interaction.type,
                        event.trainerID,
                        event.interaction.receivingtrainerIDs,
                        nexus
                ));
                break;
            case START_MOVING:
                System.out.println(message);
                nexus.tell(new AkkamonNexus.RequestStartMoving(
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        event.trainerID,
                        event.direction,
                        nexus
                ));
                break;
            case NEW_TILE_POS:
                nexus.tell(
                        new AkkamonNexus.RequestNewTilePos(
                                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                event.trainerID,
                                event.tilePos,
                                nexus
                        )
                );
                break;
            case STOP_MOVING:
                nexus.tell(
                        new AkkamonNexus.RequestStopMoving(
                            UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                            event.trainerID,
                            event.direction,
                            nexus
                        )
                );
                break;
            case TRAINER_REGISTRATION_REQUEST:
                // Here we make the trainerID and the scene is hard coded!
                String trainerName = String.valueOf(sceneIdToAkkamonSessions.get(sceneId) == null ? 1 : sceneIdToAkkamonSessions.get(sceneId).size() + 1);
                nexus.tell(new AkkamonNexus.RequestTrainerRegistration(
                        trainerName,
                        sceneId,
                        session,
                        nexus
                ));
                break;
            case HEART_BEAT:
                //System.out.println("My <3 beats!");
                break;
        }

    }

    private void sendToHandshaker(String requestName, AkkamonNexus.TrainerID trainerID, boolean value) {
        ActorRef<InteractionHandshaker.Command> handshaker = pendingInteractioRequestToHandshaker.get(requestName);
        if (handshaker != null) {
            handshaker.tell(
                    new InteractionHandshaker.InteractionReply(requestName, trainerID, value)
            );
        }
    }

    private void updatePositions() {

    }
}
