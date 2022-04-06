package server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.question.Activity;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.Question;
import server.database.repositories.question.ActivityRepository;
import server.database.repositories.question.QuestionRepository;

/**
 * Tests for QuestionService class.
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private QuestionService questionService;

    private static Activity getActivity(int id, int order) {
        Activity a = new Activity();
        a.setDescription("Doing activity " + (id + 1));
        long mult = 1;
        while (order > 1) {
            mult *= 10;
            order--;
        }
        a.setCost(id * mult);
        return a;
    }

    @Test
    void provideQuestionsOk() {
        // Mock the service
        // Generate activities that can be grouped in questions
        List<Activity> available = new ArrayList<>();
        for (int orderIdx = 1; orderIdx < 3; orderIdx++) {
            for (int idx = 0; idx < 5; idx++) {
                available.add(getActivity(idx, orderIdx));
            }
        }
        when(activityRepository.findQuestionAcceptable()).thenReturn(available);

        // Provide the questions
        List<Question> questions = questionService.provideQuestions(2);
        assertEquals(2, questions.size());

        // Verify interactions
        verify(activityRepository).findQuestionAcceptable();
        verify(questionRepository, times(2)).save(any());
        verifyNoMoreInteractions(activityRepository, questionRepository);
    }

    @Test
    void provideQuestionsNotEnough() {
        // Mock the service
        // generate too few activities
        List<Activity> available = new ArrayList<>();
        for (int idx = 0; idx < 4; idx++) {
            available.add(getActivity(idx, idx));
        }
        when(activityRepository.findQuestionAcceptable()).thenReturn(available);

        // Expect a throw
        assertThrows(IllegalStateException.class,
                () -> questionService.provideQuestions(1));

        // Verify interactions
        verify(activityRepository).findQuestionAcceptable();
        verifyNoMoreInteractions(activityRepository, questionRepository);
    }

    @Test
    void onlyEstimateAvailable() {
        // Mock the service
        // Generate activities that cannot be grouped in MC questions
        List<Activity> available = new ArrayList<>();
        for (int orderIdx = 1; orderIdx < 5; orderIdx++) {
            for (int idx = 0; idx < 2; idx++) {
                available.add(getActivity(idx, orderIdx));
            }
        }
        when(activityRepository.findQuestionAcceptable()).thenReturn(available);

        // Expect a throw
        assertThrows(IllegalStateException.class,
                () -> questionService.provideQuestions(100));

        // Verify interactions
        verify(activityRepository).findQuestionAcceptable();
        verifyNoMoreInteractions(activityRepository, questionRepository);
    }
}