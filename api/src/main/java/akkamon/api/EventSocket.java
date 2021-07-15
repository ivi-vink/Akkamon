package akkamon.api;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import akkamon.api.models.User;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.Session;


public class EventSocket extends WebSocketAdapter implements AkkamonSession {
    private final CountDownLatch closureLatch = new CountDownLatch(1);

    public User user;

    @Override
    public void onWebSocketConnect(Session sess)
    {
        super.onWebSocketConnect(sess);
        System.out.println("Socket Connected: " + sess);
    }

    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
        System.out.println("Received TEXT message: " + message);
        MessagingEngine.getInstance().incoming(this, message);

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        closureLatch.countDown();
        MessagingEngine.getInstance().sessionOffline(this);
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
    }

    public void awaitClosure() throws InterruptedException
    {
        System.out.println("Awaiting closure from remote");
        closureLatch.await();
    }

    @Override
    public void receiveGameState(String gameState) {
        try {
            getRemote().sendString(gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect(int statusCode, String message) {
        getSession().close(statusCode, message);
    }

    @Override
    public void setCurrentUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }
}
