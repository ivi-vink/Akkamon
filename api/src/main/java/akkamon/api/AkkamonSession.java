package akkamon.api;

import akkamon.api.models.User;

public interface AkkamonSession {

    void receiveGameState(String gameState);

    void disconnect(int statusCode, String message);

    void setCurrentUser(User user);
}
