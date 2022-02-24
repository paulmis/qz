package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import commons.entities.ActivityDTO;
import org.junit.jupiter.api.Test;

class ActivityTest {
    @Test
    void testDTOConstructor() {
        Activity activity = new Activity(new ActivityDTO());
        assertNotNull(activity);
    }
}