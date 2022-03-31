package server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @BeforeEach
    void init() {
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            activities.add(new Activity("Activity " + i));
        }
        lenient().when(activityRepository.findByAbandonedIsFalse()).thenReturn(activities);
    }

    @Test
    void testGetActivitiesInvalidCount() {
        assertThrows(IllegalArgumentException.class, () -> activityService.getActivities(-1));
    }

    @Test
    void testGetActivitiesZeroCount() {
        assertEquals(0, activityService.getActivities(0).size());
    }

    @Test
    void testGetActivities() {
        assertEquals(10, activityService.getActivities(10).size());
    }

    @Test
    void testGetActivitiesOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> activityService.getActivities(11));
    }
}