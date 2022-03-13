package server.api;

import commons.entities.ActivityDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;

/**
 * Controller for adding activities.
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Batch create/update activities.
     *
     * @param activities List of activities to add.
     */
    @PostMapping("/batch")
    ResponseEntity<HttpStatus> addActivity(@RequestBody List<ActivityDTO> activities) {
        // Convert DTOs to entities
        List<Activity> activityList = activities.stream().map(Activity::new).collect(Collectors.toList());
        // Save the entities
        activityRepository.saveAll(activityList);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
