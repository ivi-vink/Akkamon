package akkamon.api;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.api.models.Event;
import akkamon.domain.AkkamonMessageEngine;
import akkamon.domain.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    public void broadCastToScene(String sceneId, String message) {
        Set<AkkamonSession> sessionsInScene = sceneIdToAkkamonSessions.get(sceneId);
        for (AkkamonSession session : sessionsInScene) {
            session.send(message);
        }
    }

    @Override
    public void registerTrainerSessionToScene(String sceneId, AkkamonSession session) {
        sceneIdToAkkamonSessions.get(sceneId).add(session);
        session.setTrainerId(sceneId);
        heartBeat();
    }

    @Override
    public void removeTrainerSessionFromScene(String sceneId, AkkamonSession session) {

    }

    void incoming(AkkamonSession session, String message) {
        Event event = gson.fromJson(message, Event.class);
        // TODO use session trainerId
        String sceneId = "akkamonStartScene";

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
            case TRAINER_REGISTRATION:
                String trainerId = String.valueOf(sceneIdToAkkamonSessions.size());
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
