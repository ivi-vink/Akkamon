package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;

import java.util.Map;

public class HeartBeatEvent extends Event {
    public Map<AkkamonNexus.TrainerID, AkkamonNexus.MovementQueueReading> remoteMovementQueues;

    public HeartBeatEvent(Map<AkkamonNexus.TrainerID, AkkamonNexus.MovementQueueReading> remoteMovementQueues) {
        this.type = EventType.HEART_BEAT;
        this.remoteMovementQueues = remoteMovementQueues;
    }
}
