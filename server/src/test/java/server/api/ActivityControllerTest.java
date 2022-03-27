package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.ActivityDTO;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
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

    @BeforeEach
    void init() {
        User user = new User("John", "test@example.com", "password");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    @Test
    void getActivityImage() throws Exception {
        Activity activity = new Activity();
        activity.setIcon(getUUID(2).toString());
        when(activityRepository.findById(getUUID(1))).thenReturn(Optional.of(activity));

        // Verify that we get the UUID of the resource as a response
        this.mockMvc.perform(get("/api/activity/{id}/image", getUUID(1)))
                .andExpect(status().isOk())
                .andExpect(content().string(getUUID(2).toString()));
    }

    @Test
    void getActivityImageNotFound() throws Exception {
        when(activityRepository.findById(getUUID(1))).thenReturn(Optional.empty());
        this.mockMvc.perform(
                get("/api/activity/{id}/image", getUUID(1))
        ).andExpect(status().isNotFound());
    }

    @Test
    void addActivitiesBatch() throws Exception {
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
        // Mock the storage service getURI response
        final String IMAGE_URI = "https://example.com/";
        when(storageService.getURI(any())).thenReturn(URI.create(IMAGE_URI));

        MockMultipartFile activitiesMP = new MockMultipartFile(
                "activities",
                "blob",
                "application/json",
                objectMapper.writeValueAsString(activitiesDTO).getBytes());

        MockMultipartFile image2 = new MockMultipartFile(
                "images",
                "activity2.png",
                "image/png",
                "image2".getBytes());

        this.mockMvc.perform(
                multipart("/api/activity/batch")
                        .file(activitiesMP)
                        .file(image2))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(activitiesDTO)));

        // Verify that the activities were saved
        verify(activityRepository, times(1)).saveAll(any());
        assertEquals(2, activityCaptor.getValue().size());

        // For the second activity, the URI being saved will change.
        // This is because the URI will be updated to match the one returned by the storage service.
        // Verify that this happens.
        assertEquals(IMAGE_URI, activityCaptor.getValue().get(1).getIcon());

        // Verify that the images were saved
        verify(storageService, times(1)).store(any());
    }
}