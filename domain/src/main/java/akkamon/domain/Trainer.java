package akkamon.domain;

public class Trainer {
    private String name;
    private int x;
    private int y;

    public Trainer(String name) {
        this.name = name;
    }

    public void newPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}
