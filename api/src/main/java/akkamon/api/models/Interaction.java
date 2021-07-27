package akkamon.api.models;

import java.util.List;

public class Interaction {
    public String type;
    public String requestingTrainerId;
    public List<String> receivingTrainerIds;

    public Interaction(String type, String requestingTrainerId, List<String> receivingTrainerIds) {
        this.type = type;
        this.requestingTrainerId = requestingTrainerId;
        this.receivingTrainerIds = receivingTrainerIds;
    }

    public String toString() {
        return "interaction={\n\ttype: " + this.type + ",\n" +
                "\trequestingTrainerId: " + this.requestingTrainerId + ",\n" +
                "\treceivingTrainerIds: " + this.receivingTrainerIds + "\n}";
    }
}
