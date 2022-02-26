package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EstimateQuestionTest {

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
        answerActivities.add(getActivity(estimate).getDTO());
        ans.setUserChoice(answerActivities);
        return ans;
    }

    @Test
    void testFromDTOConstructor() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setText("Question text");

        EstimateQuestion q = new EstimateQuestion(questionDTO);
        assertEquals("Question text", q.getText());
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
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
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

    @Test
    void allArgsConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        UUID anId = UUID.randomUUID();
        Question estimateNoArgs = new EstimateQuestion();
        estimateNoArgs.setId(anId);
        estimateNoArgs.setActivities(List.copyOf(activities));
        estimateNoArgs.setText(questionText);
        Question estimateAllArgs = new EstimateQuestion(anId, activities, questionText);

        // Constructor comparison
        assertEquals(estimateNoArgs.getId(), estimateAllArgs.getId());
        assertEquals(estimateNoArgs.getActivities(), estimateAllArgs.getActivities());
        assertEquals(estimateNoArgs.getText(), estimateAllArgs.getText());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        UUID anId = UUID.randomUUID();
        Question estimateAllArgs = new EstimateQuestion(anId, activities, questionText);
        Question estimateCopy = new EstimateQuestion(estimateAllArgs);

        // Constructor comparison
        assertEquals(estimateAllArgs.getId(), estimateCopy.getId());
        assertEquals(estimateAllArgs.getActivities(), estimateCopy.getActivities());
        assertEquals(estimateAllArgs.getText(), estimateCopy.getText());
    }
}