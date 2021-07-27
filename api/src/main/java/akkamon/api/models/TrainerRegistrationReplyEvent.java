package akkamon.api.models;

import akkamon.domain.AkkamonNexus;

import java.util.Map;

public class TrainerRegistrationReplyEvent extends Event {
    public TrainerRegistrationReplyEvent(String sessionTrainerId) {
        this.type = EventType.TRAINER_REGISTRATION_REPLY;

        this.trainerId = sessionTrainerId;
    }
}
