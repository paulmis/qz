package server.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.ActivityDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.question.Activity;
import server.database.repositories.question.ActivityRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class ActivityControllerTest {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private ActivityRepository activityRepository;

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
            Activity activity = new Activity("activity" + i, "icon" + i);
            activities.add(activity);
            activitiesDTO.add(activity.getDTO());
        }

        // Capture the arguments of the call to saveAll
        when(activityRepository.saveAll(activityCaptor.capture())).thenReturn(activities);

        // Send the request
        this.mockMvc.perform(
                put("/api/activity/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activitiesDTO)))
                .andExpect(status().isOk());
        // Assert that the correct number of activities has been saved
        assertEquals(10, activityCaptor.getValue().size());
    }
}