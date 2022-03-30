package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.ActivityDTO;
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

    private Activity activityA;
    private Activity activityB;
    private Activity activityC;

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

        // Create some default activities
        activityA = new Activity();
        activityA.setId(getUUID(1));
        activityA.setDescription("Eating an apple");
        activityA.setCost(7);
        when(activityRepository.findByIdAndAbandonedIsFalse(activityA.getId())).thenReturn(Optional.of(activityA));
        when(activityRepository.findByIdAndAbandonedIsTrue(activityA.getId())).thenReturn(Optional.empty());
        when(activityRepository.findById(activityA.getId())).thenReturn(Optional.of(activityA));
        when(activityRepository.save(activityA)).thenReturn(activityA);

        activityB = new Activity();
        activityB.setId(getUUID(2));
        activityB.setDescription("Running a marathon");
        activityB.setCost(1234);
        activityB.setAbandoned(true);
        when(activityRepository.findByIdAndAbandonedIsFalse(activityB.getId())).thenReturn(Optional.empty());
        when(activityRepository.findByIdAndAbandonedIsTrue(activityB.getId())).thenReturn(Optional.of(activityB));
        when(activityRepository.findById(activityB.getId())).thenReturn(Optional.of(activityB));
        when(activityRepository.save(activityB)).thenReturn(activityB);

        activityC = new Activity();
        activityC.setId(getUUID(3));
        activityC.setDescription("Gaming for 1h");
        activityC.setCost(765);
        when(activityRepository.findByIdAndAbandonedIsFalse(activityC.getId())).thenReturn(Optional.of(activityC));
        when(activityRepository.findByIdAndAbandonedIsTrue(activityC.getId())).thenReturn(Optional.empty());
        when(activityRepository.findById(activityC.getId())).thenReturn(Optional.of(activityC));
        when(activityRepository.save(activityC)).thenReturn(activityC);

        // Mock the repository responses
        when(activityRepository.findAll()).thenReturn(List.of(activityA, activityB, activityC));
        when(activityRepository.findByAbandonedIsFalse()).thenReturn(List.of(activityA, activityC));
        when(activityRepository.save(activityA)).thenReturn(activityA);
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
        this.mockMvc.perform(post("/api/activity/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activitiesDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(activitiesDTO)));
        // Assert that the correct number of activities has been saved
        assertEquals(10, activityCaptor.getValue().size());
    }

    @Test
    void getAllActivities() throws Exception {
        // Send the request
        this.mockMvc
                .perform(get("/api/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        List.of(activityA.getDTO(), activityC.getDTO()))));

        verify(activityRepository, times(1)).findByAbandonedIsFalse();
    }

    @Test
    void updateOk() throws Exception {
        // Update an activity
        activityA.setCost(42);

        // Send the request
        this.mockMvc.perform(
                post("/api/activity/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(activityA.getDTO())))
                .andExpect(status().isOk());

        verify(activityRepository, times(1)).findById(activityA.getId());
        verify(activityRepository, times(1)).save(activityA);
    }

    @Test
    void addNoUUIDOk() throws Exception {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setDescription("staying perfectly still for 24 hours");
        activityDTO.setCost(168L);
        Activity activityD = new Activity(activityDTO);
        activityD.setId(getUUID(4));
        when(activityRepository.save(any(Activity.class))).thenReturn(activityD);

        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(activityDTO)))
                .andExpect(status().isCreated());

        verify(activityRepository, times(0)).findById(any());
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void addInvalidUUIDOk() throws Exception {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setDescription("staying perfectly still for 24 hours");
        activityDTO.setCost(168L);
        activityDTO.setId(getUUID(5));
        Activity activityD = new Activity(activityDTO);
        activityD.setId(getUUID(4));
        when(activityRepository.findById(activityDTO.getId())).thenReturn(Optional.empty());
        when(activityRepository.save(any(Activity.class))).thenReturn(activityD);

        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(activityDTO)))
                .andExpect(status().isCreated());

        verify(activityRepository, times(1)).findById(activityDTO.getId());
        verify(activityRepository, times(1)).save(any());
    }

    @Test
    void updateAbandoned() throws Exception {
        // Update an abandoned activity
        activityB.setCost(42);

        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(activityB.getDTO())))
                .andExpect(status().isGone());

        verify(activityRepository, times(1)).findById(activityB.getId());
        verify(activityRepository, times(0)).save(activityB);
    }

    @Test
    void updateMalformed() throws Exception {
        // This test is kinda fake tbh
        // Update an activity
        String anURL = "a";
        for (int idx = 0; idx < 12; idx++) {
            anURL += anURL;
        }
        activityA.setSource(anURL);
        when(activityRepository.save(activityA)).thenThrow(IllegalArgumentException.class);

        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(activityA.getDTO())))
                .andExpect(status().isBadRequest());

        verify(activityRepository, times(1)).findById(activityA.getId());
        verify(activityRepository, times(1)).save(activityA);
    }

    @Test
    void deleteOk() throws Exception {
        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/" + activityA.getId() + "/delete"))
                .andExpect(status().isOk());

        verify(activityRepository, times(1)).findByIdAndAbandonedIsFalse(activityA.getId());
        verify(activityRepository, times(1)).save(activityA);
        assertTrue(activityA.isAbandoned());
    }

    @Test
    void deleteInactive() throws Exception {
        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/" + activityB.getId() + "/delete"))
                .andExpect(status().isNotFound());

        verify(activityRepository, times(1)).findByIdAndAbandonedIsFalse(activityB.getId());
        verify(activityRepository, times(0)).save(any(Activity.class));
    }

    @Test
    void deleteNonexistent() throws Exception {
        // Send the request
        this.mockMvc.perform(
                        post("/api/activity/" + getUUID(4) + "/delete"))
                .andExpect(status().isNotFound());

        verify(activityRepository, times(1)).findByIdAndAbandonedIsFalse(getUUID(4));
        verify(activityRepository, times(0)).save(any(Activity.class));
    }
}