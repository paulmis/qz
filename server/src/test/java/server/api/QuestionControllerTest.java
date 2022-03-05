package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import commons.entities.AnswerDTO;
import commons.entities.UserDTO;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import server.database.entities.User;
import server.database.entities.question.Activity;
import server.database.entities.question.Question;
import server.database.repositories.UserRepository;
import server.database.repositories.question.QuestionRepository;
import static org.hamcrest.Matchers.equalToObject;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWebMvc
class QuestionControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private class MockQuestion extends Question {
        @Override
        public List<Double> checkAnswer(List<AnswerDTO> userAnswers) throws IllegalArgumentException {
            return null;
        }
    }

    @MockBean
    private QuestionRepository questionRepository;

    @MockBean
    private UserRepository userRepository;

    private UUID getUUID(int id) {
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + (id % 10));
    }

    private Question mockQuestion;
    User joe;
    UserDTO joeDTO;

    @Autowired
    public QuestionControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    }

    @BeforeEach
    private void init() {
        // Setup mock question
        List<Activity> activities = new ArrayList<>();

        mockQuestion = new MockQuestion();
        mockQuestion.setId(getUUID(0));
        mockQuestion.setText("Test Question");
        mockQuestion.setActivities(activities);
        when(questionRepository.findById(mockQuestion.getId())).thenReturn(Optional.of(mockQuestion));

        //Set up random test user
        joe = new User("joe", "joe@doe.com", "stinkywinky");
        joe.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        joeDTO = joe.getDTO();

        // Set the context user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        joe.getEmail(),
                        joe.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
    }

    @Test
    public void questionFoundTest() throws Exception {
        // Request question object -> expect a ok status and mock question object
        this.mockMvc
                .perform(get("/api/questions/" + mockQuestion.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(equalToObject(
                        objectMapper.writeValueAsString(mockQuestion.getDTO())
                )));
    }

    @Test
    public void questionNotFoundTest() throws Exception {
        // Request question object -> expect a conflict status
        this.mockMvc
                .perform(get("/api/questions/" + getUUID(1)))
                .andExpect(status().isConflict());
    }
}