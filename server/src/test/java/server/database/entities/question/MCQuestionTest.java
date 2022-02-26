package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.QuestionDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MCQuestionTest {

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
        q = new MCQuestion();
        q.setActivities(components);
        ((MCQuestion) q).setAnswer(components.get(1));
    }

    @Test
    void testFromDTOConstructor() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setText("Question text");

        MCQuestion q = new MCQuestion(questionDTO);
        assertEquals("Question text", q.getText());
    }

    @Test
    void checkAnswerTest() {
        List<AnswerDTO> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            ActivityDTO a = getActivity(choice).getDTO();
            answerActivities.add(a);
            AnswerDTO ans = new AnswerDTO();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMultipleAnswers() {
        List<AnswerDTO> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDTO> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            Activity a = getActivity(choice);
            answerActivities.add(a.getDTO());
            if (idx == 2) {
                a = getActivity(12);
                answerActivities.add(a.getDTO());
            }
            AnswerDTO ans = new AnswerDTO();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

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
}