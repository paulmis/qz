package server.database.entities.question;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static server.utils.TestHelpers.getUUID;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.questions.EstimateQuestionDTO;
import java.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.services.answer.AnswerCollection;

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

    private AnswerDTO getAnswer(long estimate) {
        AnswerDTO ans = new AnswerDTO();
        List<ActivityDTO> answerActivities = new ArrayList<>();

        ActivityDTO a = new ActivityDTO();
        a.setCost(estimate);

        answerActivities.add(a);
        ans.setResponse(answerActivities);
        return ans;
    }

    @Test
    void testFromDTOConstructor() {
        EstimateQuestionDTO questionDTO = new EstimateQuestionDTO();
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
        assertEquals(1, myQuestion.getRightAnswer().getResponse().size());

        // Correct activity has right cost
        assertEquals(costTest, myQuestion.getRightAnswer().getResponse().get(0).getCost());
    }

    @Test
    void checkAnswerTest() {
        Map<UUID, AnswerDTO> userGuesses = new HashMap<>();
        userGuesses.put(getUUID(1), getAnswer(90));
        userGuesses.put(getUUID(2), getAnswer(50)); // #4
        userGuesses.put(getUUID(3), getAnswer(107)); // #1
        userGuesses.put(getUUID(4), getAnswer(0)); // #5
        userGuesses.put(getUUID(5), getAnswer(75));

        Map<UUID, Double> desiredScores = new HashMap<>();
        desiredScores.put(getUUID(1), 0.92);
        desiredScores.put(getUUID(2), 0.0);
        desiredScores.put(getUUID(3), 0.96);
        desiredScores.put(getUUID(4), 0.0);
        desiredScores.put(getUUID(5), 0.53);

        AnswerCollection answers = new AnswerCollection(userGuesses);

        Map<UUID, Double> actualScores = q.checkAnswer(answers);

        assertEquals(desiredScores.size(), actualScores.size());
        for (UUID idx : desiredScores.keySet()) {
            assertThat(desiredScores.get(idx), closeTo(actualScores.get(idx), 1e-1));
        }
    }

    @Test
    void checkAnswerMismatchingSize() {
        Map<UUID, AnswerDTO> userGuesses = new HashMap<>();
        userGuesses.put(getUUID(1), getAnswer(90));
        userGuesses.put(getUUID(2), getAnswer(50)); // #4
        userGuesses.put(getUUID(3), getAnswer(107)); // #1
        userGuesses.put(getUUID(4), getAnswer(0)); // #5
        userGuesses.put(getUUID(5), getAnswer(75));

        // Add a mismatched activity
        userGuesses.get(getUUID(5)).getResponse().add(new ActivityDTO());

        Map<UUID, Double> desiredScores = new HashMap<>();
        desiredScores.put(getUUID(1), 0.92);
        desiredScores.put(getUUID(2), 0.0);
        desiredScores.put(getUUID(3), 0.96);
        desiredScores.put(getUUID(4), 0.0);
        desiredScores.put(getUUID(5), 0.0);


        AnswerCollection answers = new AnswerCollection(userGuesses);

        Map<UUID, Double> actualScores = q.checkAnswer(answers);

        // Verify that the mismatched activity results in score 0
        assertEquals(desiredScores.size(), actualScores.size());
        for (UUID idx : desiredScores.keySet()) {
            assertThat(desiredScores.get(idx), closeTo(actualScores.get(idx), 1e-1));
        }
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(null));
    }

    @Test
    void allArgsConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(getActivity(0)));
        Question estimateNoArgs = new EstimateQuestion();
        estimateNoArgs.setId(getUUID(0));
        estimateNoArgs.setActivities(List.copyOf(activities));
        estimateNoArgs.setText(questionText);
        Question estimateAllArgs = new EstimateQuestion(getUUID(0), activities, questionText);

        // Constructor comparison
        assertEquals(estimateNoArgs.getId(), estimateAllArgs.getId());
        assertEquals(estimateNoArgs.getActivities(), estimateAllArgs.getActivities());
        assertEquals(estimateNoArgs.getText(), estimateAllArgs.getText());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(getActivity(0)));
        Question estimateAllArgs = new EstimateQuestion(getUUID(0), activities, questionText);
        Question estimateCopy = new EstimateQuestion(estimateAllArgs);

        // Constructor comparison
        assertEquals(estimateAllArgs.getId(), estimateCopy.getId());
        assertEquals(estimateAllArgs.getActivities(), estimateCopy.getActivities());
        assertEquals(estimateAllArgs.getText(), estimateCopy.getText());
    }

    @Test
    void constructorTestMultipleActivities() {
        List<Activity> activities = new ArrayList<>(List.of(getActivity(0), getActivity(1)));
        assertThrows(IllegalArgumentException.class, () -> new EstimateQuestion(getUUID(0), activities, ""));
    }
}