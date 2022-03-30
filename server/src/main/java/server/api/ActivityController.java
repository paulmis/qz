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
                activityDTO.setIcon(imageId.toString());

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
     * Get all activities.
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
            return new ResponseEntity<>(activityDTOList, HttpStatus.CREATED);
        } catch (Exception e) {
            // This can occur due to malformed data (for example, URL longer than 255 characters)
            log.warn("Failed to save activities: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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

        return activityRepository.findById(activityId).map(Activity::getIcon).map(icon -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(storageService.getURI(UUID.fromString(icon)));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found"));
    }
}
