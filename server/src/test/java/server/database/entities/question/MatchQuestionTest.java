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
        expectedRightAnswer.setUserChoice(expectedRightChoice);
        assertEquals(expectedRightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        // first user has all correct
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setUserChoice(answerAct);
        List<AnswerDTO> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has all wrong
        answerAct = List.of(
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two switched (2/4 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertEquals(new ArrayList<>(Arrays.asList(1.0, 0.0, 2.0 / 4)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMismatchingSize() {
        // first user has 4 activities
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setUserChoice(answerAct);
        List<AnswerDTO> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has 5 activities
        answerAct = List.of(
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO(),
                getActivity(12).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has 4 activities
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(userAnswers);
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
        Question matchNoArgs = new MatchQuestion();
        matchNoArgs.setId(anId);
        matchNoArgs.setActivities(List.copyOf(activities));
        matchNoArgs.setText(questionText);
        Question matchAllArgs = new MatchQuestion(anId, activities, questionText);

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
        UUID anId = UUID.randomUUID();
        Question matchAllArgs = new MatchQuestion(anId, activities, questionText);
        Question matchCopy = new MatchQuestion(matchAllArgs);

        // Constructor comparison
        assertEquals(matchAllArgs.getId(), matchCopy.getId());
        assertEquals(matchAllArgs.getActivities(), matchCopy.getActivities());
        assertEquals(matchAllArgs.getText(), matchCopy.getText());
    }
}