package akkamon.api;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.AkkamonSession;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;


public class EventSocket extends WebSocketAdapter implements AkkamonSession {
    private final CountDownLatch closureLatch = new CountDownLatch(1);
    private AkkamonNexus.TrainerID trainerID;

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
        App.messagingEngine.incoming(this, message);

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode, reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        closureLatch.countDown();
        App.messagingEngine.trainerDisconnected(this);
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
    public void send(String event) {
        try {
            getRemote().sendString(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void settrainerID(AkkamonNexus.TrainerID trainerID) {
        this.trainerID = trainerID;
    }

    @Override
    public AkkamonNexus.TrainerID gettrainerID() {
        return this.trainerID;
    }
}
