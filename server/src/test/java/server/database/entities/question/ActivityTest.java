package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;

import commons.entities.ActivityDTO;
import org.junit.jupiter.api.Test;

class ActivityTest {
    @Test
    void testFromDTOConstructor() {
        String desc = "A sample description";
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setDescription(desc);

        Activity activity = new Activity(activityDTO);
        assertEquals(desc, activity.getDescription());
    }
}