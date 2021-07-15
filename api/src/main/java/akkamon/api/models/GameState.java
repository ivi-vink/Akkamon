package akkamon.api.models;

import akkamon.domain.Trainer;

import java.util.HashMap;
import java.util.Map;

public class GameState {
    public Player currentPlayer;
    public HashMap<String, Player> remotePlayers = new HashMap<>();


    public void setCurrentPlayer(String name, HashMap<String, Trainer> trainers) {
        Trainer trainer = trainers.get(name);
        Position position = new Position(trainer.getX(), trainer.getY());
        currentPlayer = new Player(name, position);
    }

    public void setRemotePlayers(HashMap<String, Trainer> trainers) {
        for (Map.Entry<String, Trainer> trainer: trainers.entrySet()) {
            if (trainer.getValue().getName().equals(currentPlayer.name)) {
                continue;
            }

            String name = trainer.getKey();
            Trainer obj = trainer.getValue();
            remotePlayers.put(name, new Player(name, new Position(
                    obj.getX(),
                    obj.getY()
            )));
        }
    }
}
