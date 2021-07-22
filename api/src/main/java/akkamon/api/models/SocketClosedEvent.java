package akkamon.api.models;

public class SocketClosedEvent extends Event {

    public SocketClosedEvent() {
        this.type = EventType.SOCKET_CLOSED;
    }
}
