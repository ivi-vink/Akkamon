package akkamon.api.models;

import akkamon.domain.Direction;
import akkamon.domain.TilePos;

public class Event {
    public EventType type;
    public String trainerId;
    public Direction direction;
    public String sceneId;
    public TilePos tilePos;
}
