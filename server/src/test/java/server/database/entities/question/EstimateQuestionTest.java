package server.database.entities.question;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static server.utils.TestHelpers.getUUID;

import commons.entities.questions.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.database.entities.answer.Answer;

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

    private Answer getAnswer(long estimate) {
        Answer ans = new Answer();
        List<Long> answerActivities = new ArrayList<>();
        answerActivities.add(estimate);
        ans.setResponse(answerActivities);
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
        assertEquals(1, myQuestion.getRightAnswer().getResponse().size());

        // Correct activity has right cost
        assertEquals(costTest, myQuestion.getRightAnswer().getResponse().get(0));
    }

    @Test
    void checkAnswerTest() {
        List<Answer> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #4
                getAnswer(107), // #1
                getAnswer(0), // #5
                getAnswer(75))); // #3

        var desiredScores = Arrays.asList(0.92, 0.00, 0.96, 0.0, 0.53);
        var actualScores = q.checkAnswer(userGuesses);

        assertEquals(desiredScores.size(), actualScores.size());

        for (int i = 0; i < desiredScores.size(); i++) {
            assertThat(desiredScores.get(i), closeTo(actualScores.get(i), 1e-1));
        }
    }

    @Test
    void checkAnswerMismatchingSize() {
        List<Answer> userGuesses = new ArrayList<>(Arrays.asList(
                getAnswer(90), // #2
                getAnswer(50), // #3
                getAnswer(107), // #1
                getAnswer(0))); // #4

        List<Long> answerAct = List.of(
                getActivity(0).getCost(),
                getActivity(1).getCost(),
                getActivity(2).getCost(),
                getActivity(3).getCost()
        );
        Answer a = new Answer();
        a.setResponse(answerAct);
        userGuesses.add(a);

        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(userGuesses));
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(null));
    }

    @Test
    void allArgsConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
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
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        Question estimateAllArgs = new EstimateQuestion(getUUID(0), activities, questionText);
        Question estimateCopy = new EstimateQuestion(estimateAllArgs);

        // Constructor comparison
        assertEquals(estimateAllArgs.getId(), estimateCopy.getId());
        assertEquals(estimateAllArgs.getActivities(), estimateCopy.getActivities());
        assertEquals(estimateAllArgs.getText(), estimateCopy.getText());
    }
}