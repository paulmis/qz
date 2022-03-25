package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.ActivityDTO;
import java.nio.file.Path;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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
    void addMultipleActivities() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(activitiesDTO)));
        // Assert that the correct number of activities has been saved
        assertEquals(10, activityCaptor.getValue().size());
    }

    @Test
    void addImage() throws Exception {
        Activity activity = new Activity("activity",
                10000,
                "image",
                "source");
        activity.setId(getUUID(1));
        when(activityRepository.findById(any())).thenReturn(Optional.of(activity));
        when(storageService.store(any(), any())).thenReturn(Path.of("/tmp/whatever.png"));

        // Create a mock image
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "image.png",
                "image/png",
                "image".getBytes());

        // Expected response values
        Activity activityDTO = new Activity("activity",
                10000,
                getUUID(1) + ".png",
                "source");
        activityDTO.setId(getUUID(1));

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/activity/{id}/image", activity.getId());
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        // Perform the mock request
        this.mockMvc.perform(builder.file(multipartFile))
                // Expect 201 response code
                .andExpect(status().isCreated())
                // Expect response JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Assert that the correct activity has been returned
                .andExpect(content().json(objectMapper.writeValueAsString(activityDTO.getDTO())));
    }
}