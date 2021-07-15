package akkamon.domain;

public class Trainer {
    private String name;
    private float x;
    private float y;

    public Trainer(String name) {
        this.name = name;
    }

    public void newPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}
