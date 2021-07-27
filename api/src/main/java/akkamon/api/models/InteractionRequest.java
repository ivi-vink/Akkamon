package akkamon.api.models;

public class InteractionRequest extends Event {

    public String type;
    public String trainerId;
    public long requestId;

    public InteractionRequest(String type, String trainerId, long requestId) {
        this.type = type;
        this.trainerId = trainerId;
        this.requestId = requestId;
    }
}
