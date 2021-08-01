package akkamon.domain.model.akkamon;

import akkamon.domain.model.akkamon.abilities.AbilityFactory;
import akkamon.domain.model.akkamon.abilities.AkkamonAbilities;
import akkamon.domain.model.akkamon.moves.MovesFactory;
import akkamon.domain.model.akkamon.status.AkkamonStatus;
import akkamon.domain.model.akkamon.status.StatusFactory;
import akkamon.domain.model.akkamon.types.AkkamonTypes;
import akkamon.domain.model.akkamon.types.TypeFactory;
import akkamon.domain.model.battle.Phases;

import java.util.Map;
import java.util.Objects;

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
    }

    public static abstract class Ability extends TypeEquality implements Phases {

    }

    public static abstract class Status extends TypeEquality implements Phases {

    }

    public static abstract class Move implements Phases {
    }

    private String name;
    private MonStats stats;
    private Type type;
    private Ability ability;
    private Status status;
    private Map<String, Move> moves;

    public Mon(String name, MonStats stats, AkkamonTypes typeName, AkkamonAbilities abilityName, AkkamonStatus statusName, String[] moveNames) {
        this.name = name;
        this.stats = stats;
        this.type = new TypeFactory().fromName(typeName);
        this.ability = new AbilityFactory().fromName(abilityName);
        this.status = new StatusFactory().fromName(statusName);
        this.moves = new MovesFactory().fromNames(moveNames);
    }


    public String getName() { return name; }
    public Type getType() {
        return type;
    }

    public Ability getAbility() {
        return ability;
    }

    public Status getStatus() {
        return status;
    }

}
