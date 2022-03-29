package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static server.utils.TestHelpers.getUUID;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.questions.QuestionDTO;
import java.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.services.answer.AnswerCollection;

class MatchQuestionTest {
    static Question q;

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4);
        return a;
    }

    @BeforeAll
    static void defaultQuestion() {
        List<Activity> components = new ArrayList<>();
        for (int idx = 0; idx < 4; idx++) {
            components.add(getActivity(idx));
        }
        q = new MatchQuestion();
        q.setActivities(components);
    }

    @Test
    void testFromDTOConstructor() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setText("Question text");

        MatchQuestion q = new MatchQuestion(questionDTO);
        assertEquals("Question text", q.getText());
    }

    @Test
    void getRightAnswerTest() {
        List<ActivityDTO> expectedRightChoice = new ArrayList<>();
        for (int idx = 0; idx < q.getActivities().size(); idx++) {
            expectedRightChoice.add(getActivity(idx).getDTO());
        }
        AnswerDTO expectedRightAnswer = new AnswerDTO();
        expectedRightAnswer.setResponse(expectedRightChoice);
        assertEquals(expectedRightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        Map<UUID, AnswerDTO> userAnswers = new HashMap<>();

        // first user has all correct
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(1), a);

        // second user has all wrong
        answerAct = List.of(
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(2), a);

        // third user has two switched (2/4 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(3), a);

        Map<UUID, Double> expectedScores = new HashMap<>();
        expectedScores.put(getUUID(1), 1.0);
        expectedScores.put(getUUID(2), 0.0);
        expectedScores.put(getUUID(3), 0.5);

        AnswerCollection answerCollection = new AnswerCollection(userAnswers);
        assertEquals(expectedScores, q.checkAnswer(answerCollection));
    }

    @Test
    void checkAnswerMismatchingSize() {
        Map<UUID, AnswerDTO> userAnswers = new HashMap<>();

        // first user has 4 activities
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(1), a);

        // second user has 5 activities
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO(),
                getActivity(4).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(2), a);

        // third user has 4 activities
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(3), a);

        Map<UUID, Double> expectedScores = new HashMap<>();
        expectedScores.put(getUUID(1), 1.0);
        expectedScores.put(getUUID(2), 0.0);
        expectedScores.put(getUUID(3), 0.5);

        AnswerCollection answerCollection = new AnswerCollection(userAnswers);
        assertEquals(expectedScores, q.checkAnswer(answerCollection));
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
        Question matchNoArgs = new MatchQuestion();
        matchNoArgs.setId(getUUID(0));
        matchNoArgs.setActivities(List.copyOf(activities));
        matchNoArgs.setText(questionText);
        Question matchAllArgs = new MatchQuestion(getUUID(0), activities, questionText);

        // Constructor comparison
        assertEquals(matchNoArgs.getId(), matchAllArgs.getId());
        assertEquals(matchNoArgs.getActivities(), matchAllArgs.getActivities());
        assertEquals(matchNoArgs.getText(), matchAllArgs.getText());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        Question matchAllArgs = new MatchQuestion(getUUID(0), activities, questionText);
        Question matchCopy = new MatchQuestion(matchAllArgs);

        // Constructor comparison
        assertEquals(matchAllArgs.getId(), matchCopy.getId());
        assertEquals(matchAllArgs.getActivities(), matchCopy.getActivities());
        assertEquals(matchAllArgs.getText(), matchCopy.getText());
    }
}