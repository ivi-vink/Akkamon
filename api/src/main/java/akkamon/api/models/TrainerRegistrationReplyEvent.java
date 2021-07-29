package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;

public class TrainerRegistrationReplyEvent extends Event {

    public TrainerRegistrationReplyEvent(AkkamonNexus.TrainerID sessiontrainerID) {
        this.type = EventType.TRAINER_REGISTRATION_REPLY;

        this.trainerID = sessiontrainerID;
    }
}
