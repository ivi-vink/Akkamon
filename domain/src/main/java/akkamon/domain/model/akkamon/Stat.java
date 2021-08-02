package akkamon.domain.model.akkamon;

public class Stat {
    public final int base;
    public int effective;

    public Stat(int base) {
        this.base = base;
        this.effective = base;
    }
}
