package akkamon.api.models;

import com.google.gson.JsonElement;

public class InteractionStartEvent extends Event {

    public String interactionType;

    public InteractionStartEvent(String requestName, String interactionType) {
        this.type = EventType.INTERACTION_START;
        this.requestName = requestName;
        this.interactionType = interactionType;
    }
}
