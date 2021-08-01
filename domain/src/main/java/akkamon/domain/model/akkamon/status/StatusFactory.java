package akkamon.domain.model.akkamon.status;

import akkamon.domain.model.Factory;
import akkamon.domain.model.akkamon.Mon;

public class StatusFactory implements Factory<Mon.Status, AkkamonStatus> {

    @Override
    public Mon.Status fromName(AkkamonStatus name) {
        switch (name) {
            case NONE:
                return new NoStatus();
            default:
                return null;
        }
    }
}
