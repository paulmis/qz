package server.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import server.database.entities.question.Question;
import server.database.repositories.question.ActivityRepository;
import server.database.repositories.question.QuestionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class QuestionControllerTest {
    private final MockMvc mockMvc;

    @MockBean
    private ActivityRepository activityRepository;

    @MockBean
    private QuestionRepository questionRepository;

    @Autowired
    public QuestionControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void init() {
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            activities.add(new Activity("Activity " + i));
        }
        lenient().when(activityRepository.findAll()).thenReturn(activities);

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
    void testMcCreate() throws Exception {
        this.mockMvc.perform(
                put("/api/question/mc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(questionRepository, times(1)).save(any(Question.class));
    }
}