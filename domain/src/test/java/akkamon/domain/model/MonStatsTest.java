package akkamon.domain.model;

import akkamon.domain.model.akkamon.MonStats;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonStatsTest {


    @Test
    public void a_mons_hp_can_be_constructed_from_base_ev_nature_and_iv_values() {

        int[] baseSnorlax = {160, 110, 65, 65, 110, 30};
        int[] evs = {252, 4, 0, 0, 252, 0};
        int[] ivs = {31,31,31,31,31,31};
        double[] natureMultiplier = {1, 1, 1, 1, 1.1, 0.9};
        int level = 100;

        MonStats stats = new MonStats(
                baseSnorlax,
                evs,
                ivs,
                natureMultiplier,
                level
        );

        assertAll(
                () -> {
                    assertEquals(524, stats.HP);
                    assertEquals(257, stats.Attack);
                    assertEquals(166, stats.Defence);
                    assertEquals(166, stats.SpecialAttack);
                    assertEquals(350, stats.SpecialDefence);
                    assertEquals(86, stats.Speed);
                }
        );
    }

}