package akkamon.api.models;

import org.eclipse.jetty.server.Authentication;

public class Event {
    public String type;
    public GameState gameState;
    public User user;

    public Event(String type, GameState gameState) {
        this.type = type;
        this.gameState = gameState;
    }
}
