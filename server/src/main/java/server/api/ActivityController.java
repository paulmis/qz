package server.api;

import commons.entities.ActivityDTO;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        // ToDo: Add images
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

    /**
     * Lists all the activities currently in use.
     *
     * @return the list of activities currently in use
     */
    @GetMapping("")
    ResponseEntity<List<ActivityDTO>> getInUse() {
        List<ActivityDTO> activities = activityRepository.findByAbandonedIsFalse()
                .stream()
                .map(Activity::getDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activities);
    }

    /**
     * Creates or updates an activity.
     *
     * @param activityDTO DTO of the activity to store
     * @return 400 if the activity is malformed,
     *         410 if the activity is marked as abandoned,
     *         200 if the activity is updated and
     *         201 if a new activity was created
     */
    @PostMapping("/save")
    ResponseEntity saveActivity(@RequestBody ActivityDTO activityDTO) {
        // ToDo: Add images

        // Convert DTO to entity
        Activity toSave = new Activity(activityDTO);

        boolean createdActivity = true;
        if (activityDTO.getId() != null) {
            // Retrieve previously existing activity, if present
            Optional<Activity> previous = activityRepository.findById(activityDTO.getId());
            if (previous.isPresent() && previous.get().isAbandoned()) {
                // Trying to update an abandoned activity
                return ResponseEntity.status(HttpStatus.GONE).build();
            } else if (previous.isPresent()) {
                createdActivity = false;
            }
        }

        // Save the entity (if the UUID in the DTO is already existing, the entity will be updated instead)
        try {
            toSave = activityRepository.save(toSave);
            log.debug("Saved activity {}", toSave.toString());
            if (createdActivity) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.ok().build();
            }
        } catch (Exception e) {
            // This can occur due to malformed data (for example, URL longer than 255 characters)
            log.warn("Failed to save activity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Marks an activity as abandoned (i.e. it will not be used to generate new questions).
     *
     * @param activityId the id of the activity
     * @return 404 if the activity was not found, 200 otherwise
     */
    @PostMapping("/{activityId}/delete")
    ResponseEntity deleteActivity(@PathVariable UUID activityId) {
        // ToDo: handle images

        Optional<Activity> toDelete = activityRepository.findByIdAndAbandonedIsFalse(activityId);
        if (toDelete.isEmpty()) {
            // Activity not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Set activity as abandoned
        toDelete.get().setAbandoned(true);
        activityRepository.save(toDelete.get());
        return ResponseEntity.ok().build();
    }
}
