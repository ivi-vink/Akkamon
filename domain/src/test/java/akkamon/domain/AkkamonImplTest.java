package akkamon.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AkkamonImplTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    class getInstance_behaviour {
        @Test
        void given_there_is_no_instance_yet_when_getInstance_is_called_then_give_class_property_instance() {
            assertNotNull(AkkamonImpl.getInstance());
        }
    }

}