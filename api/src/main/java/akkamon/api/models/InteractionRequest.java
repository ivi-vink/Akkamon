package akkamon.api.models;

public class InteractionRequest extends Event {

    public String interactionType;
    public String requestName;

    public InteractionRequest(String interactionType, String trainerId, String requestName) {
        this.type = EventType.INTERACTION_REQUEST;
        this.interactionType = interactionType;
        this.trainerId = trainerId;
        this.requestName = requestName;
    }
}
