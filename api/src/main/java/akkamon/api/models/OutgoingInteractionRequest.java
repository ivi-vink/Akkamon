package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;

public class OutgoingInteractionRequest extends Event {

    public String interactionType;

    public OutgoingInteractionRequest(String interactionType, AkkamonNexus.TrainerID trainerID, String requestName) {
        this.type = EventType.INTERACTION_REQUEST;
        this.interactionType = interactionType;
        this.trainerID = trainerID;
        this.requestName = requestName;
    }
}
