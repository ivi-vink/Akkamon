package akkamon.domain.model.akkamon.types;

import akkamon.domain.model.Factory;
import akkamon.domain.model.akkamon.Mon;

public class TypeFactory implements Factory<Mon.Type, AkkamonTypes> {
    public Mon.Type fromName(AkkamonTypes type) {
        switch (type) {
            case NORMAL:
                return new NormalType();
            case DRAGON:
                return new DragonType();
            case PSYCHIC:
                return new PsychicType();
            default:
                return null;
        }
    }
}
