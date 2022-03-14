package server.services;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;

/**
 * Fetch activities for individual questions.
 */
@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Get a number of activities from the database.
     *
     * @param count The number of activities to return.
     * @return A list of activities.
     */
    public List<Activity> getActivities(int count) {
        List<Activity> activities = activityRepository.findAll();
        Collections.shuffle(activities);
        return activities.subList(0, count);
    }
}