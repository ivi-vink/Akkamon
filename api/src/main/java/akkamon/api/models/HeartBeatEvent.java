package akkamon.api.models;

import akkamon.domain.AkkamonNexus;

import java.util.Map;

public class HeartBeatEvent extends Event {
    public Map<String, AkkamonNexus.MovementQueueReading> remoteMovementQueues;

    public HeartBeatEvent(Map<String, AkkamonNexus.MovementQueueReading> remoteMovementQueues) {
        this.type = EventType.HEART_BEAT;
        this.remoteMovementQueues = remoteMovementQueues;
    }
}
