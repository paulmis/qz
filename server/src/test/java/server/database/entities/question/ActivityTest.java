package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.entities.ActivityDTO;
import org.junit.jupiter.api.Test;

class ActivityTest {
    @Test
    void testFromDTOConstructor() {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setDescription("A sample description");

        Activity activity = new Activity(activityDTO);
        assertEquals("A sample description", activity.getDescription());
    }
}