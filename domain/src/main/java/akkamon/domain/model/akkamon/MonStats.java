package akkamon.domain.model.akkamon;

public class MonStats {
    public final int HP;
    public final int Attack;
    public final int Defence;
    public final int SpecialAttack;
    public final int SpecialDefence;
    public final int Speed;
    public final int accuracy = 1;
    public final int evasion = 1;

    public MonStats(int[] base, int[] evs, int[] ivs, double[] natureMultiplier, int level) {
        this.HP = setHP(base[0], evs[0], ivs[0], level);
        this.Attack = setStat(base[1], evs[1], ivs[1], natureMultiplier[1], level);
        this.Defence = setStat(base[2], evs[2], ivs[2], natureMultiplier[2], level);
        this.SpecialAttack = setStat(base[3], evs[3], ivs[3], natureMultiplier[3], level);
        this.SpecialDefence = setStat(base[4], evs[4], ivs[4], natureMultiplier[4], level);
        this.Speed = setStat(base[5], evs[5], ivs[5], natureMultiplier[5], level);
    }

    private int setStat(int base, int ev, int iv, double nature, int level) {
        double firstTerm = ( ( 2 * base + iv + (ev / 4.0) ) * level / 100 ) + 5;
        return  (int) (firstTerm * nature);
    }

    private int setHP(int base, int ev, int iv, int level) {
        double firstTerm = (2 * base + iv + (ev / 4.0)) * level / 100;
        return (int) (firstTerm + level + 10);
    }

}
