package server.api;

import com.google.common.io.Files;
import commons.entities.ActivityDTO;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;
import server.exceptions.NotFoundException;
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

    /**
     * Add a picture to an activity.
     *
     * @param id ID of the activity to add the picture to.
     * @param file Picture to add.
     * @return DTO of the modified activity.
     */
    @PutMapping("/{id}/image")
    ResponseEntity<ActivityDTO> addActivityImage(
            @PathVariable UUID id,
            @RequestParam MultipartFile file) {
        // Verify that the activity exists
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Activity not found"));

        String newFileName = String.format("%s.%s", id, Files.getFileExtension(
                Objects.requireNonNull(file.getOriginalFilename())));
        // Save the image to the storage service
        Path storedPath = storageService.store(file, newFileName);

        // Update the activity entity
        activity.setIcon(newFileName);
        activityRepository.save(activity);

        log.debug("Added activity '{}' image: '{}'", activity.getId(), storedPath);
        return new ResponseEntity<>(activity.getDTO(), HttpStatus.CREATED);
    }
}
