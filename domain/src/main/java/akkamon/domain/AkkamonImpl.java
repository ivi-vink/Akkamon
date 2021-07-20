package akkamon.domain;

import java.util.HashMap;

public class AkkamonImpl implements Akkamon {

    private static AkkamonImpl instance;

    private static HashMap<String, Trainer> dummyTrainersCollection = new HashMap<>();

    public static AkkamonImpl getInstance() {
        if (instance == null) {
            instance = new AkkamonImpl();
        }
        return instance;
    }

    @Override
    public void newPlayerConnected(String name, String password) {
        // switch (dummyTrainersCollection.size()) {
        //     case 0:
        //         dummyTrainersCollection.put("Ash", new Trainer("Ash"));
        //         break;
        //     case 1:
        //         dummyTrainersCollection.put("Misty", new Trainer("Misty"));
        //         break;
        // }
    }

    public void updateTrainerPosition(String name, float x, float y) {
        // Trainer trainer = dummyTrainersCollection.get(name);
        // trainer.newPosition(x, y);
    }

    public HashMap<String, Trainer> getDummyTrainersCollection() {
        return dummyTrainersCollection;
    }



}
