package akkamon.api;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akkamon.api.models.Event;
import akkamon.api.models.HeartBeatEvent;
import akkamon.domain.AkkamonMessageEngine;
import akkamon.domain.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagingEngine implements AkkamonMessageEngine {

    private Map<String, AkkamonSession> trainerIdToAkkamonSessions = new HashMap<>();
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
        if (!trainerIdToAkkamonSessions.isEmpty()) {
            for (Map.Entry<String, AkkamonSession> entry : trainerIdToAkkamonSessions.entrySet()) {
                // System.out.println("heartbeat to " + entry.getKey());
                entry.getValue().send(gson.toJson(new HeartBeatEvent()));
            }
        }
    }

    @Override
    public void broadCastGridPosition() {
    }

    @Override
    public void registerTrainerSession(String trainerId, AkkamonSession session) {
        trainerIdToAkkamonSessions.put(trainerId, session);
    }

    @Override
    public void removeTrainerSession(String trainerId, AkkamonSession session) {

    }

    void incoming(AkkamonSession session, String message) {
        Event event = gson.fromJson(message, Event.class);
        String trainerId = String.valueOf(trainerIdToAkkamonSessions.size());
        String sceneId = "akkamonStartScene";

        switch (event.type) {
            case START_MOVING:
                system.tell(new AkkamonNexus.RequestStartMoving(
                        UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                        "0",
                        event.sceneId,
                        event.direction
                ));
                break;
            case TRAINER_REGISTRATION:

                system.tell(new AkkamonNexus.RequestTrainerRegistration(
                        trainerId,
                        sceneId,
                        session,
                        system
                ));
                break;
            case HEART_BEAT:
                System.out.println("My heart goes boom skip!");
                break;
        }

    }

    private void updatePositions() {

    }

}
