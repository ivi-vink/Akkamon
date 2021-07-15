package akkamon.api.models;

import java.util.HashMap;

public class Player {
    public String name;
    public Position position;

    public Player(String name, Position position) {
        this.name = name;
        this.position = position;
    }
}
