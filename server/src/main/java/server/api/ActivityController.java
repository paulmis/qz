package server.api;

import commons.entities.ActivityDTO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Batch create/update activities.
     *
     * @param activities List of activities to add.
     * @return DTOs of all added activities.
     */
    @PostMapping("/batch")
    ResponseEntity<List<ActivityDTO>> batchAddActivity(@RequestBody List<ActivityDTO> activities) {
        // TODO: proper access control on this endpoint
        // Convert DTOs to entities
        List<Activity> activityList = activities.stream().map(Activity::new).collect(Collectors.toList());
        // Save the entities
        try {
            List<ActivityDTO> activityDTOList = activityRepository.saveAll(activityList).stream()
                    .map(Activity::getDTO).collect(Collectors.toList());
            log.debug("Added {} activities", activityDTOList.size());
            return new ResponseEntity<>(activityDTOList, HttpStatus.OK);
        } catch (Exception e) {
            // This can occur due to malformed data (for example, URL longer than 255 characters)
            log.warn("Failed to save activities: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
