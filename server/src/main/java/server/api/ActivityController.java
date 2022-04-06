package server.api;

import com.google.common.collect.Maps;
import commons.entities.ActivityDTO;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;
import server.exceptions.ResourceNotFoundException;
import server.services.storage.StorageService;

/**
 * Controller for adding activities.
 */
@RestController
@RequestMapping("/api/activity")
@Slf4j
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private StorageService storageService;

    /**
     * Batch create/update activities with added images.
     *
     * @param activities List of activities to add.
     * @param images     List of pictures to add.
     * @return DTOs of all added activities.
     */
    @PostMapping("/batch/images")
    ResponseEntity batchAddActivityWithImages(@RequestPart List<ActivityDTO> activities,
                                              @RequestPart(required = false) MultipartFile[] images) {
        // Map filenames to images
        Map<String, MultipartFile> imageMap = images == null
                ? Collections.emptyMap()
                : Maps.uniqueIndex(Arrays.asList(images), MultipartFile::getOriginalFilename);

        // Convert DTOs to entities and save images
        List<Activity> activitiesToAdd = activities.stream().map(activityDTO -> {
            MultipartFile image;
            // If the image is not specified or is not in the map, just save the activity
            if (activityDTO.getIcon() == null || (image = imageMap.get(activityDTO.getIcon())) == null) {
                return new Activity(activityDTO);
            }

            try {
                // Save the image to the storage
                UUID imageId = storageService.store(image.getInputStream());
                log.trace("Stored image '{}' for activity '{}'", imageId, activityDTO.getId());

                // Set the image resource ID and return the activity
                activityDTO.setIconId(imageId);

            } catch (IOException e) {
                log.error("Failed to store image for activity '{}'", activityDTO.getId(), e);
            }
            return new Activity(activityDTO);
        }).collect(Collectors.toList());

        // Save the activity entities
        List<Activity> savedActivities = activityRepository.saveAll(activitiesToAdd);
        log.debug("Saved {} activities", savedActivities.size());

        // Return the DTOs of the saved activities
        return new ResponseEntity<>(savedActivities.stream().map(Activity::getDTO), HttpStatus.CREATED);
    }

    /**
     * Add a batch of activities.
     *
     * @param activities List of activities to add.
     * @return DTOs of all added activities.
     */
    @PostMapping("/batch")
    ResponseEntity<List<ActivityDTO>> batchAddActivity(@RequestBody List<ActivityDTO> activities) {
        // Convert DTOs to entities
        List<Activity> activityList = activities.stream().map(Activity::new).collect(Collectors.toList());

        // Save the entities
        try {
            List<ActivityDTO> activityDTOList = activityRepository.saveAll(activityList).stream()
                    .map(Activity::getDTO).collect(Collectors.toList());
            log.debug("Added {} activities", activityDTOList.size());
            return new ResponseEntity<>(activityDTOList, HttpStatus.CREATED);
        } catch (Exception e) {
            // This can occur due to malformed data (for example, URL longer than 255 characters)
            log.warn("Failed to save activities: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get the URL of the image for the specified activity.
     *
     * @param activityId ID of the activity to get.
     * @return URL of the image associated with the activity.
     */
    @GetMapping("/{activityId}/image")
    ResponseEntity getActivityImage(@PathVariable UUID activityId) {
        log.trace("Getting image for activity '{}'", activityId);

        return activityRepository.findById(activityId).map(Activity::getIconId).map(icon -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(storageService.getURI(icon));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }).orElseThrow(() -> new ResourceNotFoundException("Activity not found"));
    }
    
    /**
     * Lists all the activities currently in use.
     *
     * @return the list of activities currently in use
     */
    @GetMapping
    ResponseEntity<List<ActivityDTO>> getInUse() {
        log.trace("Returning all activities in use");
        List<ActivityDTO> activities = activityRepository.findByAbandonedIsFalse()
                .stream()
                .map(Activity::getDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activities);
    }

    /**
     * Creates or updates an activity and its image.
     *
     * @param activityDTO DTO of the activity to store
     * @param image       The activity image to upload
     * @return 400 if the activity was malformed,
     *         404 if the activity was marked as abandoned,
     *         200 if the activity was updated and
     *         201 if a new activity was created
     */
    @PostMapping("/save/image")
    ResponseEntity saveActivityImage(
            @RequestPart ActivityDTO activityDTO,
            @RequestPart(required = false) MultipartFile image) {

        boolean createdActivity = true;
        if (activityDTO.getId() != null) {
            // Retrieve previously existing activity, if present
            Activity previous = activityRepository.findById(activityDTO.getId()).orElse(null);
            if (previous != null) {
                if (previous.isAbandoned()) {
                    // Trying to update an abandoned activity
                    throw new ResourceNotFoundException("Updating a deleted activity.");
                } else {
                    createdActivity = false;
                }
            }
        }

        // Store the image, if present
        if (image != null) {
            try {
                // Save the image to the storage
                UUID imageId = storageService.store(image.getInputStream());
                log.trace("Stored image '{}' for activity '{}'", imageId, activityDTO.getId());

                // Set the image resource ID
                activityDTO.setIconId(imageId);

            } catch (IOException e) {
                log.error("Failed to store the image for activity '{}'", activityDTO.getId(), e);
            }
        }

        // Convert DTO to entity
        Activity toSave = new Activity(activityDTO);

        // Save the entity (if the UUID in the DTO is already existing, the entity will be updated instead)
        try {
            toSave = activityRepository.save(toSave);
            log.debug("Saved activity {}", toSave.toString());
            if (createdActivity) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.ok().build();
            }
        } catch (IllegalArgumentException e) {
            // This can occur due to malformed data (for example, URL longer than 255 characters)
            log.warn("Failed to save activity: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Creates or updates an activity.
     *
     * @param activityDTO DTO of the activity to store
     * @return 400 if the activity was malformed,
     *         404 if the activity was marked as abandoned,
     *         200 if the activity was updated and
     *         201 if a new activity was created
     */
    @PostMapping("/save")
    ResponseEntity saveActivity(@RequestBody ActivityDTO activityDTO) {
        return saveActivityImage(activityDTO, null);
    }

    /**
     * Marks an activity as abandoned (i.e. it will not be used to generate new questions).
     *
     * @param activityId the id of the activity
     * @return 404 if the activity was not found, 200 otherwise
     */
    @PostMapping("/{activityId}/delete")
    ResponseEntity deleteActivity(@PathVariable UUID activityId) {
        Optional<Activity> toDelete = activityRepository.findByIdAndAbandonedIsFalse(activityId);
        if (toDelete.isEmpty()) {
            // Activity not found
            throw new ResourceNotFoundException("Activity " + activityId + " not found.");
        }

        // Set activity as abandoned
        toDelete.get().setAbandoned(true);
        activityRepository.save(toDelete.get());
        return ResponseEntity.ok().build();
    }
}
