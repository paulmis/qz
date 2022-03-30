package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.ActivityDTO;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;
import server.services.storage.StorageService;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class ActivityControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private StorageService storageService;

    @Captor
    private ArgumentCaptor<List<Activity>> activityCaptor;

    @Autowired
    public ActivityControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @Test
    void getActivityImage() throws Exception {
        Activity activity = new Activity();
        activity.setIcon(getUUID(2).toString());
        when(activityRepository.findById(getUUID(1))).thenReturn(Optional.of(activity));
        when(storageService.getURI(getUUID(2))).thenReturn(URI.create("https://example.com/image"));

        // Verify that we get the UUID of the resource as a response
        this.mockMvc.perform(get("/api/activity/{id}/image", getUUID(1)))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://example.com/image"));
    }

    @Test
    void getActivityImageNoImage() throws Exception {
        // Construct an activity without an associated image
        Activity activity = new Activity();
        when(activityRepository.findById(getUUID(1))).thenReturn(Optional.of(activity));

        // Verify that we get a 404 response
        this.mockMvc.perform(get("/api/activity/{id}/image", getUUID(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getActivityImageNotFound() throws Exception {
        when(activityRepository.findById(getUUID(1))).thenReturn(Optional.empty());
        this.mockMvc.perform(
                get("/api/activity/{id}/image", getUUID(1))
        ).andExpect(status().isNotFound());
    }

    @Test
    void addActivitiesBatchWithImages() throws Exception {
        // Create a list of activities and corresponding DTOs
        List<Activity> activities = new ArrayList<>();
        List<ActivityDTO> activitiesDTO = new ArrayList<>();

        Activity activity1 = new Activity("activity1", 100, "activity1.png", "https://example.com/");
        activity1.setId(getUUID(1));
        activities.add(activity1);
        activitiesDTO.add(activity1.getDTO());

        Activity activity2 = new Activity("activity2", 200, "activity2.png", "https://example.com/");
        activity2.setId(getUUID(2));
        activities.add(activity2);
        activitiesDTO.add(activity2.getDTO());

        // Capture the arguments of the call to saveAll
        when(activityRepository.saveAll(activityCaptor.capture())).thenReturn(activities);
        // Mock the response from the storage service
        when(storageService.store(any(InputStream.class))).thenReturn(getUUID(3));

        // Convert the list of DTOs to a MultipartFile (JSON)
        MockMultipartFile activitiesMP = new MockMultipartFile(
                "activities",
                "blob",
                "application/json",
                objectMapper.writeValueAsString(activitiesDTO).getBytes());

        // Create a dummy image file
        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "activity2.png",
                "image/png",
                "image2".getBytes());

        this.mockMvc.perform(
                multipart("/api/activity/batch/images")
                        .file(activitiesMP)
                        .file(image2))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verify that we received the correct list of activity DTOs
                .andExpect(content().json(objectMapper.writeValueAsString(activitiesDTO)));

        // Verify that the activities were saved
        verify(activityRepository, times(1)).saveAll(any());
        assertEquals(2, activityCaptor.getValue().size());

        // For the second activity, the icon identifier will change.
        // This is because the UUID will be updated to match the one returned by the storage service.
        // Verify that this happens.
        assertEquals(getUUID(3).toString(), activityCaptor.getValue().get(1).getIcon());

        // Verify that the images were saved
        verify(storageService, times(1)).store(any());
    }

    @Test
    void addActivitiesBatch() throws Exception {
        // Create a list of activities and corresponding DTOs
        List<Activity> activities = new ArrayList<>();
        List<ActivityDTO> activitiesDTO = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Activity activity = new Activity("activity" + i);
            activities.add(activity);
            activitiesDTO.add(activity.getDTO());
        }

        // Capture the arguments of the call to saveAll
        when(activityRepository.saveAll(activityCaptor.capture())).thenReturn(activities);

        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/batch")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(activitiesDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(activitiesDTO)));
        // Assert that the correct number of activities has been saved
        assertEquals(10, activityCaptor.getValue().size());
    }

}