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

class OrderQuestionTest {
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
        expectedRightAnswer.setUserChoice(expectedRightChoice);
        assertEquals(expectedRightAnswer, q.getRightAnswer());
    }

    @Test
    void checkAnswerTest() {
        // first user is right
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

        // second user is decreasing
        answerAct = List.of(
                getActivity(3).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two inverted (2/3 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertEquals(new ArrayList<>(Arrays.asList(1.0, 0.0, 2.0 / 3)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerDecreasing() {
        // first user is increasing
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

        // second user is decreasing
        answerAct = List.of(
                getActivity(30).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(0).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two inverted (1/3 of points)
        answerAct = List.of(
                getActivity(0).getDTO(),
                getActivity(2).getDTO(),
                getActivity(1).getDTO(),
                getActivity(3).getDTO()
        );
        a = new AnswerDTO();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        ((OrderQuestion) q).setIncreasing(false);
        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 1.0 / 3)), q.checkAnswer(userAnswers));
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

        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> q.checkAnswer(null));
    }

    @Test
    void allArgsConstructorTest() {
        // Test setup
        Question orderNoArgs = new OrderQuestion();
        UUID anId = UUID.randomUUID();
        orderNoArgs.setId(anId);
        List<Activity> activities = new ArrayList<>(List.of(
                getActivity(0), getActivity(1), getActivity(2), getActivity(3)));
        orderNoArgs.setActivities(List.copyOf(activities));
        String questionText = "aQuestion";
        orderNoArgs.setText(questionText);
        boolean order = true;
        ((OrderQuestion) orderNoArgs).setIncreasing(order);
        Question orderAllArgs = new OrderQuestion(anId, activities, questionText, order);

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
        UUID anId = UUID.randomUUID();
        boolean order = true;
        Question orderAllArgs = new OrderQuestion(anId, activities, questionText, order);
        Question orderCopy = new OrderQuestion(orderAllArgs, order);

        // Constructor comparison
        assertEquals(orderAllArgs.getId(), orderCopy.getId());
        assertEquals(orderAllArgs.getActivities(), orderCopy.getActivities());
        assertEquals(orderAllArgs.getText(), orderCopy.getText());
        assertEquals(((OrderQuestion) orderAllArgs).isIncreasing(), ((OrderQuestion) orderCopy).isIncreasing());
    }
}