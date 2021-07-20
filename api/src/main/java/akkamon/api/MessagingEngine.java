package akkamon.api;

import akkamon.api.models.GameState;
import com.google.gson.Gson;

import java.util.HashMap;

public class MessagingEngine implements AkkamonMessageEngine {

    private HashMap<String, AkkamonSession> akkamonSessions = new HashMap<>();
    private static MessagingEngine instance;
    private Gson gson = new Gson();

    public MessagingEngine() {
    }

    @Override
    public void broadCastGridPosition() {
    }

    void incoming(AkkamonSession session, String message) {

    }

    private void updatePositions(GameState gameState) {

    }

}
