package akkamon.api.models;

import akkamon.domain.model.battle.requests.BattleRequestBody;
import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.actors.tasks.heartbeat.Direction;
import akkamon.domain.actors.tasks.heartbeat.TilePos;

public class Event {
    public EventType type;
    public AkkamonNexus.TrainerID trainerID;
    public Direction direction;
    public TilePos tilePos;
    public Interaction interaction;
    public String requestName;
    public boolean value;
    public BattleRequestBody body;

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", trainerID=" + trainerID +
                ", direction=" + direction +
                ", tilePos=" + tilePos +
                ", interaction=" + interaction +
                ", requestName='" + requestName + '\'' +
                ", value=" + value +
                ", body=" + body +
                '}';
    }
}
