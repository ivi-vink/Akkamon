package akkamon.api.models;

public class HeartBeatEvent extends Event {
    public HeartBeatEvent() {
        this.type = EventType.HEART_BEAT;
    }
}
