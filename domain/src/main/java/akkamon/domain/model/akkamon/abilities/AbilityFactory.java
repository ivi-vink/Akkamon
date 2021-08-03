package akkamon.domain.model.akkamon.abilities;

import akkamon.domain.model.akkamon.Factory;
import akkamon.domain.model.akkamon.Mon;

public class AbilityFactory implements Factory<Mon.Ability, AkkamonAbilities> {
    @Override
    public Mon.Ability fromName(AkkamonAbilities name) {
        switch (name) {
            case IMMUNITY:
                return new Immunity();
            default:
                return null;
        }
    }
}
