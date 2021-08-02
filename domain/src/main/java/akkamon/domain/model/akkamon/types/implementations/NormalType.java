package akkamon.domain.model.akkamon.types.implementations;

import akkamon.domain.model.akkamon.Mon;
import akkamon.domain.model.akkamon.types.AkkamonType;

public class NormalType extends Mon.Type {

    public NormalType() {
        this.name = AkkamonType.NORMAL;
    }
}
