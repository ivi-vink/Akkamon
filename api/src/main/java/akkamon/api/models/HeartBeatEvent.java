package akkamon.api.models;

import akkamon.domain.AkkamonNexus;

import java.util.Map;

public class HeartBeatEvent extends Event {
    public Map<String, AkkamonNexus.TrainerPositionReading> remoteTrainerPositions;

    public HeartBeatEvent(Map<String, AkkamonNexus.TrainerPositionReading> remoteTrainerPositions) {
        this.type = EventType.HEART_BEAT;
        this.remoteTrainerPositions = remoteTrainerPositions;
    }
}
