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

class OrderQuestionTest {
    static Question q;

    private static Activity getActivity(int id) {
        Activity a = new Activity();
        a.setDescription("Activity" + (id + 1));
        a.setCost(2 + id * 4L);
        return a;
    }

    @BeforeAll
    static void defaultQuestion() {
        List<Activity> components = new ArrayList<>();
        Activity a;
        for (int idx = 0; idx < 4; idx++) {
            a = getActivity(idx);
            components.add(a);
        }
        q = new OrderQuestion();
        q.setActivities(components);
        ((OrderQuestion) q).setIncreasing(true);
    }

    @Test
    void testFromDTOConstructor() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setText("Question text");

        OrderQuestion q = new OrderQuestion(questionDTO);
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

        // first user is right
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(1), a);

        // second user is decreasing
        answerAct = List.of(
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(2), a);

        // third user has two inverted (2/3 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(3), a);

        Map<UUID, Double> expectedAnswers = new HashMap<>();
        expectedAnswers.put(getUUID(1), 0.0);
        expectedAnswers.put(getUUID(2), 1.0);
        expectedAnswers.put(getUUID(3), 2.0 / 3);

        AnswerCollection answerCollection = new AnswerCollection(userAnswers);
        assertEquals(expectedAnswers, q.checkAnswer(answerCollection));
    }

    @Test
    void checkAnswerDecreasing() {
        Map<UUID, AnswerDTO> userAnswers = new HashMap<>();

        // first user is increasing
        List<ActivityDTO> answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(1).getDTO(),
                getActivity(2).getDTO(),
                getActivity(3).getDTO()
        );
        AnswerDTO a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(1), a);

        // second user is decreasing
        answerAct = List.of(
                getActivity(30).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(2), a);

        // third user has two inverted (1/3 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setResponse(answerAct);
        userAnswers.put(getUUID(3), a);

        ((OrderQuestion) q).setIncreasing(false);

        Map<UUID, Double> expectedAnswers = new HashMap<>();
        expectedAnswers.put(getUUID(1), 0.0);
        expectedAnswers.put(getUUID(2), 1.0);
        expectedAnswers.put(getUUID(3), 1.0 / 3);

        AnswerCollection answerCollection = new AnswerCollection(userAnswers);
        assertEquals(expectedAnswers, q.checkAnswer(answerCollection));
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
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO(),
                getActivity(12).getDTO()
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

        Map<UUID, Double> expectedAnswers = new HashMap<>();
        expectedAnswers.put(getUUID(1), 0.0);
        expectedAnswers.put(getUUID(2), 0.0);
        expectedAnswers.put(getUUID(3), 1.0 / 3);

        AnswerCollection answerCollection = new AnswerCollection(userAnswers);
        assertEquals(expectedAnswers, q.checkAnswer(answerCollection));
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(null));
    }

    @Test
    void allArgsConstructorTest() {
        // Test setup
        Question orderNoArgs = new OrderQuestion();
        orderNoArgs.setId(getUUID(0));
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        orderNoArgs.setActivities(List.copyOf(activities));
        String questionText = "aQuestion";
        orderNoArgs.setText(questionText);
        boolean order = true;
        ((OrderQuestion) orderNoArgs).setIncreasing(order);
        Question orderAllArgs = new OrderQuestion(getUUID(0), activities, questionText, order);

        // Constructor comparison
        assertEquals(orderNoArgs.getId(), orderAllArgs.getId());
        assertEquals(orderNoArgs.getActivities(), orderAllArgs.getActivities());
        assertEquals(orderNoArgs.getText(), orderAllArgs.getText());
        assertEquals(((OrderQuestion) orderNoArgs).isIncreasing(), ((OrderQuestion) orderAllArgs).isIncreasing());
    }

    @Test
    void copyConstructorTest() {
        // Test setup
        String questionText = "aQuestion";
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        boolean order = true;
        Question orderAllArgs = new OrderQuestion(getUUID(0), activities, questionText, order);
        Question orderCopy = new OrderQuestion(orderAllArgs, order);

        // Constructor comparison
        assertEquals(orderAllArgs.getId(), orderCopy.getId());
        assertEquals(orderAllArgs.getActivities(), orderCopy.getActivities());
        assertEquals(orderAllArgs.getText(), orderCopy.getText());
        assertEquals(((OrderQuestion) orderAllArgs).isIncreasing(), ((OrderQuestion) orderCopy).isIncreasing());
    }
}