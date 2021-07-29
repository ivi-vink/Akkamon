package akkamon.domain;

import akkamon.domain.actors.AkkamonNexus;

public interface AkkamonSession {
    void send(String event);

    void settrainerID(AkkamonNexus.TrainerID trainerID);

    AkkamonNexus.TrainerID gettrainerID();
}
