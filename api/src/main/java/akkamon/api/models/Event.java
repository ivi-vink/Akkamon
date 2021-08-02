package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;
import akkamon.domain.Direction;
import akkamon.domain.TilePos;

public class Event {
    public EventType type;
    public AkkamonNexus.TrainerID trainerID;
    public Direction direction;
    public TilePos tilePos;
    public Interaction interaction;
    public String requestName;
    public boolean value;
    public BattleRequestBody battleRequestBody;
}
