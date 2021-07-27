package akkamon.api;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.api.models.Event;
import akkamon.api.models.HeartBeatEvent;
import akkamon.api.models.TrainerRegistrationReplyEvent;
import akkamon.domain.AkkamonMessageEngine;
import akkamon.domain.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagingEngine implements AkkamonMessageEngine {

    private Map<String, Set<AkkamonSession>> sceneIdToAkkamonSessions = new HashMap<>();
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
        System.out.println(sceneSessions);
        System.out.println(sceneIdToAkkamonSessions.keySet());
        if (sceneSessions != null) {
            for (AkkamonSession session : sceneSessions) {
                Map<String, AkkamonNexus.MovementQueueReading> withoutSelf = new HashMap<>(trainerPositions);
                withoutSelf.remove(session.getTrainerId());
                HeartBeatEvent heartBeat = new HeartBeatEvent(
                        withoutSelf
                );
                String heartBeatMessage = gson.toJson(heartBeat);
                System.out.println("Sending to " + session.getTrainerId());
                System.out.println(heartBeatMessage);
                session.send(
                        heartBeatMessage
                );
            }
        }
    }

    @Override
    public void registerTrainerSessionToScene(String sceneId, AkkamonSession session) {
        System.out.println("Registering session to scene " + sceneId);
        Set<AkkamonSession> sessionsInScene = sceneIdToAkkamonSessions.get(sceneId);
        if (sessionsInScene != null) {
            sessionsInScene.add(session);
        } else {
            sessionsInScene = new HashSet<>();
            sessionsInScene.add(session);
            sceneIdToAkkamonSessions.put(sceneId,
                    sessionsInScene
            );
            System.out.println(sceneIdToAkkamonSessions.keySet());
        }

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
