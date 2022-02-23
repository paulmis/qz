package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class EstimateQuestionTest {

    private ModelMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ModelMapper();
    }

    private static Question q;

    private static Activity getActivity(int cost) {
        Activity a = new Activity();
        a.setDescription("Activity of cost " + cost);
        a.setCost(cost);
        return a;
    }

    @BeforeAll
    static void defaultQuestion() {
        List<Activity> components = new ArrayList<>();
        Activity a = getActivity(100);
        components.add(a);
        q = new EstimateQuestion();
        q.setActivities(components);
    }

    private AnswerDTO getAnswer(int estimate) {
        AnswerDTO ans = new AnswerDTO();
        List<ActivityDTO> answerActivities = new ArrayList<>();
        answerActivities.add(this.mapper.map(getActivity(estimate), ActivityDTO.class));
        ans.setUserChoice(answerActivities);
        return ans;
    }

    @Test
    void checkAnswerTest() {
        List<AnswerDTO> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #4
                getAnswer(107), // #1
                getAnswer(0), // #5
                getAnswer(75))); // #3

        assertEquals(new ArrayList<>(Arrays.asList(0.75, 0.25, 1.0, 0.0, 0.5)), q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerSameRankTest() {
        List<AnswerDTO> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #3
                getAnswer(107), // #1
                getAnswer(0), // #4
                getAnswer(150), // #3
                getAnswer(800))); // #5

        assertEquals(new ArrayList<>(Arrays.asList(0.75, 0.5, 1.0, 0.25, 0.5, 0.0)), q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerMismatchingSize() {
        List<AnswerDTO> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #3
                getAnswer(107), // #1
                getAnswer(0))); // #4

        List<ActivityDTO> answerAct = List.of(
                this.mapper.map(getActivity(0), ActivityDTO.class),
                this.mapper.map(getActivity(1), ActivityDTO.class),
                this.mapper.map(getActivity(2), ActivityDTO.class),
                this.mapper.map(getActivity(3), ActivityDTO.class)
        );
        AnswerDTO a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userGuesses.add(a);

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(userGuesses);
        });
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }
}