package akkamon.api;

import akkamon.api.models.*;
import akkamon.domain.AkkamonImpl;
import akkamon.domain.Trainer;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessagingEngine {

    private HashMap<String, AkkamonSession> akkamonSessions = new HashMap<>();
    private static MessagingEngine instance;
    private Gson gson = new Gson();

    public static MessagingEngine getInstance() {
        if (instance == null) {
            instance = new MessagingEngine();
            return instance;
        }
        return instance;
    }

    public MessagingEngine() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                emitGameState();
            }
        }, 0, 200, TimeUnit.MILLISECONDS);

    }

    void emitGameState() {
        HashMap<String, Trainer> trainers = AkkamonImpl.getInstance().getDummyTrainersCollection();

        if (akkamonSessions.size() == 1) {
            AkkamonSession session = akkamonSessions.get("Ash");

            GameState gameState = new GameState();
            // dummy
            gameState.setCurrentPlayer("Ash", trainers);

            Event event = new Event("serverSidePosUpdate", gameState);

            session.receiveGameState(gson.toJson(event));

        } else if (akkamonSessions.size() == 2) {
            for (String name: akkamonSessions.keySet()) {
                AkkamonSession session = akkamonSessions.get(name);

                GameState gameState = new GameState();
                // dummy
                gameState.setCurrentPlayer(name, trainers);
                gameState.setRemotePlayers(trainers);

                Event event = new Event("serverSidePosUpdate", gameState);

                session.receiveGameState(gson.toJson(event));
            }
        }

        // for (Map.Entry<User, AkkamonSession> sess: akkamonSessions.entrySet()) {

        //     User user = sess.getKey();
        //     AkkamonSession session = sess.getValue();

        //     GameState gameState = new GameState();
        //     // dummy
        //     gameState.setCurrentPlayer(user.name, trainers);

        //     session.receiveGameState(gson.toJson(gameState));
        // }
    }

    void incoming(AkkamonSession session, String message) {
        Event event = gson.fromJson(message, Event.class);
        switch (event.type) {
            case "login":
                login(session, event.user);
                break;
            case  "clientSidePosUpdate":
                updatePositions(event.gameState);
                break;
        }
    }

    private void updatePositions(GameState gameState) {
        Player current = gameState.currentPlayer;
        if (gameState.currentPlayer != null) {
            AkkamonImpl.getInstance().updateTrainerPosition(current.name, current.position.x, current.position.y);
        }
    }

    private void login(AkkamonSession session, User user) {
        if (user == null) {
            session.disconnect(401, "Give username and password");
        }
        System.out.println("Currrent connections: " + akkamonSessions.size());
        if (akkamonSessions.size() == 0) {
            akkamonSessions.put("Ash", session);
            System.out.println("After adding ash!: " + akkamonSessions.size());
            session.setCurrentUser(new User("Ash", ""));
        } else if (akkamonSessions.size() == 1) {
            akkamonSessions.put("Misty", session);
            session.setCurrentUser(new User("Misty", ""));
        }
        AkkamonImpl.getInstance().newPlayerConnected(user.name, user.password);
        System.out.println("Emitting gameState!");
        emitGameState();
    }

    public void sessionOffline(AkkamonSession session) {
        akkamonSessions.remove(session.getUser().name);
    }
}
