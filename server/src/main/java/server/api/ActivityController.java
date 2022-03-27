package server.api;

import static com.google.common.base.MoreObjects.firstNonNull;

import com.google.common.collect.Maps;
import commons.entities.ActivityDTO;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Batch create/update activities.
     *
     * @param activities List of activities to add.
     * @param images List of pictures to add.
     * @return DTOs of all added activities.
     */
    @PostMapping("/batch")
    ResponseEntity batchAddActivity(@RequestPart List<ActivityDTO> activities,
                                    @RequestPart MultipartFile[] images) {
        // Map filenames to images
        Map<String, MultipartFile> imageMap = Maps.uniqueIndex(
                firstNonNull(Arrays.asList(images), new ArrayList<>(0)),
                MultipartFile::getOriginalFilename);

        List<Activity> activitiesToAdd = activities.stream().map(activityDTO -> {
            MultipartFile image;
            // If the image is not specified or is not in the map, just save the activity
            if (activityDTO.getIcon() == null || (image = imageMap.get(activityDTO.getIcon())) == null) {
                return new Activity(activityDTO);
            }

            try {
                // Save the image to the storage
                UUID imageId = storageService.store(image.getInputStream());
                // Get the URL under which the image is accessible
                URI imageUri = storageService.getURI(imageId);
                log.trace("Stored image '{}' for activity '{}'", imageId, activityDTO.getId());

                // Set the image URL and return the activity
                activityDTO.setIcon(imageUri.toString());
            } catch (IOException e) {
                log.error("Failed to store image for activity '{}'", activityDTO.getId(), e);
            }
            return new Activity(activityDTO);
        }).collect(Collectors.toList());

        // Save the activities
        List<Activity> savedActivities = activityRepository.saveAll(activitiesToAdd);
        log.debug("Saved {} activities", savedActivities.size());

        // Return the DTOs of the saved activities
        return new ResponseEntity<>(savedActivities.stream().map(Activity::getDTO), HttpStatus.CREATED);
    }

    /**
     * Get the URL of the image for the specified activity.
     *
     * @param id ID of the activity to get.
     * @return URL of the image associated with the activity.
     */
    @GetMapping("/{id}/image")
    ResponseEntity getActivityImage(@PathVariable UUID id) {
        log.trace("Getting image for activity '{}'", id);
        return activityRepository.findById(id)
                .map(activity -> ResponseEntity.ok(activity.getIcon()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activity not found"));
    }
}
