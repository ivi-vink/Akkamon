package akkamon.domain.model.akkamon.types;

import akkamon.domain.model.akkamon.Factory;
import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.types.implementations.*;

public class TypeFactory implements Factory<Mon.Type, AkkamonType> {
    public Mon.Type fromName(AkkamonType type) {
        switch (type) {
            case NORMAL:
                return new NormalType();
            case DRAGON:
                return new DragonType();
            case PSYCHIC:
                return new PsychicType();
            case GROUND:
                return new GroundType();
            case ICE:
                return new IceType();
            case ELECTRIC:
                return new ElectricType();
            default:
                return null;
        }
    }
}
