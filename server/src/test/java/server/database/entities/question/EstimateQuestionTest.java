package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.database.entities.Answer;

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

    private Answer getAnswer(int estimate) {
        Answer ans = new Answer();
        List<Activity> answerActivities = new ArrayList<>();
        answerActivities.add(getActivity(estimate));
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
    void getRightAnswerTest() {
        int costTest = 50;
        Question myQuestion = new EstimateQuestion(q);
        List<Activity> components = new ArrayList<>();
        Activity a = getActivity(costTest);
        components.add(a);
        myQuestion.setActivities(components);

        // Only one correct activity
        assertEquals(1, myQuestion.getRightAnswer().getUserChoice().size());

        // Correct activity has right cost
        assertEquals(costTest, myQuestion.getRightAnswer().getUserChoice().get(0).getCost());
    }

    @Test
    void checkAnswerTest() {
        List<Answer> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #4
                getAnswer(107), // #1
                getAnswer(0), // #5
                getAnswer(75))); // #3

        assertEquals(new ArrayList<>(Arrays.asList(0.75, 0.25, 1.0, 0.0, 0.5)), q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerSameRankTest() {
        List<Answer> userGuesses = new ArrayList<>(Arrays.asList(
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
        List<Answer> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #3
                getAnswer(107), // #1
                getAnswer(0))); // #4

        List<Activity> answerAct = List.of(
                getActivity(0),
                getActivity(1),
                getActivity(2),
                getActivity(3)
        );
        Answer a = new Answer();
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