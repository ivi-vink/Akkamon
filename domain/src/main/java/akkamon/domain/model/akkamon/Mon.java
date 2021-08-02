package akkamon.domain.model.akkamon;

import akkamon.domain.model.akkamon.abilities.AbilityFactory;
import akkamon.domain.model.akkamon.abilities.AkkamonAbilities;
import akkamon.domain.model.akkamon.moves.MoveCategory;
import akkamon.domain.model.akkamon.moves.MoveSlot;
import akkamon.domain.model.akkamon.moves.MovesFactory;
import akkamon.domain.model.akkamon.status.AkkamonStatus;
import akkamon.domain.model.akkamon.status.StatusCategory;
import akkamon.domain.model.akkamon.status.StatusFactory;
import akkamon.domain.model.akkamon.types.AkkamonType;
import akkamon.domain.model.akkamon.types.TypeFactory;
import akkamon.domain.model.battle.Phases;

import java.util.List;
import java.util.Map;

public class Mon {

    public static abstract class TypeEquality {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            else return true;
        }
    }

    public static abstract class Type extends TypeEquality {
        public AkkamonType name;

    }

    public static abstract class Ability extends TypeEquality implements Phases {

    }

    public static abstract class Status extends TypeEquality implements Phases {

    }

    public static abstract class Move implements Phases {
        public String name;
        public Mon.Type type;
        public MoveCategory category;
        public Stat PP;
        public int power;
        public double accuracy;

        public Move(String name, Mon.Type type,
                        MoveCategory category,
                        Stat PP,
                        int power,
                        double accuracy) {
            this.name = name;
            this.type = type;
            this.category = category;
            this.PP = PP;
            this.power = power;
            this.accuracy = accuracy;
        }
    }

    private String name;
    private MonStats stats;
    private Type type;
    private Ability ability;
    private Map<StatusCategory, List<Status>> status;
    private Map<MoveSlot, Move> moves;

    public Mon(String name, MonStats stats, AkkamonType typeName, AkkamonAbilities abilityName, AkkamonStatus[] statusNames, String[] moveNames) {
        this.name = name;
        this.stats = stats;
        this.type = new TypeFactory().fromName(typeName);
        this.ability = new AbilityFactory().fromName(abilityName);
        this.status = new StatusFactory().fromName(statusNames);
        this.moves = new MovesFactory().fromNames(moveNames);
    }


    public String getName() { return name; }
    public Type getType() {
        return type;
    }

    public Ability getAbility() {
        return ability;
    }

    public Map<StatusCategory, List<Status>> getStatus() {
        return status;
    }

}
