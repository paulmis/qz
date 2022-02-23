package server.database.entities.question;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import commons.entities.ActivityDto;
import commons.entities.AnswerDto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class MCQuestionTest {

    private ModelMapper mapper;

    @BeforeEach
    void setup() {
        this.mapper = new ModelMapper();
    }

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
    void checkAnswerTest() {
        List<AnswerDto> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDto> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            ActivityDto a = this.mapper.map(getActivity(choice), ActivityDto.class);
            answerActivities.add(a);
            AnswerDto ans = new AnswerDto();
            ans.setUserChoice(answerActivities);
            userAnswers.add(ans);
        }

        assertEquals(new ArrayList<>(Arrays.asList(0.0, 1.0, 0.0, 0.0, 0.0, 1.0)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMultipleAnswers() {
        List<AnswerDto> userAnswers = new ArrayList<>();
        for (int idx = 0; idx < 6; idx++) {
            List<ActivityDto> answerActivities = new ArrayList<>();
            int choice = idx % 4;
            Activity a = getActivity(choice);
            answerActivities.add(this.mapper.map(a, ActivityDto.class));
            if (idx == 2) {
                a = getActivity(12);
                answerActivities.add(this.mapper.map(a, ActivityDto.class));
            }
            AnswerDto ans = new AnswerDto();
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