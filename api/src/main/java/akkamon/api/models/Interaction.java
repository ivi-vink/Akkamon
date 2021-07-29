package akkamon.api.models;

import akkamon.domain.actors.AkkamonNexus;

import java.util.List;

public class Interaction {
    public String type;
    public String requestingtrainerID;
    public List<AkkamonNexus.TrainerID> receivingtrainerIDs;

    public Interaction(String type, String requestingtrainerID, List<AkkamonNexus.TrainerID> receivingtrainerIDs) {
        this.type = type;
        this.receivingtrainerIDs = receivingtrainerIDs;
    }

    public String toString() {
        return "interaction={\n\ttype: " + this.type + ",\n" +
                "\trequestingtrainerID: " + this.requestingtrainerID + ",\n" +
                "\treceivingtrainerIDs: " + this.receivingtrainerIDs + "\n}";
    }
}
