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

class MatchQuestionTest {

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
        q = new MatchQuestion();
        q.setActivities(components);
    }

    @Test
    void checkAnswerTest() {
        // first user has all correct
        List<ActivityDto> answerAct = List.of(
                this.mapper.map(getActivity(0), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(3), ActivityDto.class)
        );
        AnswerDto a = new AnswerDto();
        a.setUserChoice(answerAct);
        List<AnswerDto> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has all wrong
        answerAct = List.of(
                this.mapper.map(getActivity(3), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(0), ActivityDto.class)
        );
        a = new AnswerDto();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has two switched (2/4 of points)
        answerAct = List.of(
                this.mapper.map(getActivity(0), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(3), ActivityDto.class)
        );
        a = new AnswerDto();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertEquals(new ArrayList<>(Arrays.asList(1.0, 0.0, 2.0 / 4)), q.checkAnswer(userAnswers));
    }

    @Test
    void checkAnswerMismatchingSize() {
        // first user has 4 activities
        List<ActivityDto> answerAct = List.of(
                this.mapper.map(getActivity(0), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(3), ActivityDto.class)
        );
        AnswerDto a = new AnswerDto();
        a.setUserChoice(answerAct);
        List<AnswerDto> userAnswers = new ArrayList<>();
        userAnswers.add(a);

        // second user has 5 activities
        answerAct = List.of(
                this.mapper.map(getActivity(3), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(0), ActivityDto.class),
                this.mapper.map(getActivity(12), ActivityDto.class)
        );
        a = new AnswerDto();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        // third user has 4 activities
        answerAct = List.of(
                this.mapper.map(getActivity(0), ActivityDto.class),
                this.mapper.map(getActivity(2), ActivityDto.class),
                this.mapper.map(getActivity(1), ActivityDto.class),
                this.mapper.map(getActivity(3), ActivityDto.class)
        );
        a = new AnswerDto();
        a.setUserChoice(answerAct);
        userAnswers.add(a);

        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }

    @Test
    void checkAnswerNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            q.checkAnswer(null);
        });
    }
}