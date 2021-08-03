package akkamon.domain.model.akkamon.status;

import akkamon.domain.model.akkamon.Mon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusFactory {

    public Map<StatusCategory, List<Mon.Status>> fromName(AkkamonStatus[] names) {
        Map<StatusCategory, List<Mon.Status>> statuses = new HashMap<>();
        
        statuses.put(StatusCategory.NON_VOLATILE, new ArrayList<>());
        statuses.put(StatusCategory.VOLATILE, new ArrayList<>());
        statuses.put(StatusCategory.VOLATILE_IN_BATTLE, new ArrayList<>());
        
        for (var i = 0 ; i < names.length; i++) {
            AkkamonStatus name = names[i];
            switch (name) {
                case NONE:
                    return statuses;
                default:
                    return null;
            }
        }
        return null;
    }

}
