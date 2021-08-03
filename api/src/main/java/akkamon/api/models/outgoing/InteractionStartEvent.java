package akkamon.api.models.outgoing;

import akkamon.api.models.Event;
import akkamon.api.models.EventType;
import com.google.gson.JsonElement;

public class InteractionStartEvent extends Event {

    public String interactionType;

    public InteractionStartEvent(String requestName, String interactionType) {
        this.type = EventType.INTERACTION_START;
        this.requestName = requestName;
        this.interactionType = interactionType;
    }
}
