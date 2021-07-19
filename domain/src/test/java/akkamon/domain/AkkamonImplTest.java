package akkamon.domain;

import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;

class AkkamonImplTest {

    class getInstance_behaviour {
        @Test
        void given_there_is_no_instance_yet_when_getInstance_is_called_then_give_class_property_instance() {
            assertNotNull(AkkamonImpl.getInstance());
        }
    }

}