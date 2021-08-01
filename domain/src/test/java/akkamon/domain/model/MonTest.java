package akkamon.domain.model;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.MonStats;
import akkamon.domain.model.akkamon.abilities.AkkamonAbilities;
import akkamon.domain.model.akkamon.abilities.Immunity;
import akkamon.domain.model.akkamon.status.AkkamonStatus;
import akkamon.domain.model.akkamon.status.NoStatus;
import akkamon.domain.model.akkamon.types.AkkamonTypes;
import akkamon.domain.model.akkamon.types.NormalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MonTest {

    @Test
    public void a_mon_can_be_constructed() {

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

        Mon snorlax = new Mon(
                "Snorlax",
                stats,
                AkkamonTypes.NORMAL,
                AkkamonAbilities.IMMUNITY,
                AkkamonStatus.NONE,
                new String[] {
                        "Body Slam",
                        "Reflect",
                        "Rest",
                        "Ice Beam"
                }
        );

        assertEquals(new NormalType(), snorlax.getType());
        assertEquals(new Immunity(), snorlax.getAbility());
        assertEquals(new NoStatus(), snorlax.getStatus());
    }

    @Nested
    public class SnorlaxBehaviour {

        @Nested
        public class startTurn {

        }

    }
}