package server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.entities.questions.MCType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import server.configuration.QuestionGenerationConfiguration;
import server.database.entities.question.Activity;
import server.database.entities.question.EstimateQuestion;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.question.ActivityRepository;
import server.database.repositories.question.QuestionRepository;

/**
 * Tests for QuestionService class.
 */
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @Mock
    private QuestionGenerationConfiguration questionGenerationConfiguration;

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

    @BeforeEach
    void init() {
        lenient().when(questionRepository.saveAll(any(List.class)))
                .thenAnswer((Answer<List<Question>>) invocation -> invocation.getArgument(0));
        lenient().when(questionGenerationConfiguration.getNumberEnabledQuestionTypes()).thenReturn(4);
        lenient().when(questionGenerationConfiguration.getQuestionGenerationAttempts()).thenReturn(1000);
        lenient().when(questionGenerationConfiguration.getEnabledQuestionTypes())
                .thenReturn(new QuestionGenerationConfiguration().getEnabledQuestionTypes());
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
        verify(questionRepository).saveAll(any());
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
                () -> questionService.provideQuestions(1));

        // Verify interactions
        verify(activityRepository).findQuestionAcceptable();
        verifyNoMoreInteractions(activityRepository, questionRepository);
    }

    @Test
    void oneOfEachKind() {
        // Mock the service
        // Generate activities that can be grouped in questions
        List<Activity> available = new ArrayList<>();
        for (int orderIdx = 1; orderIdx < 5; orderIdx++) {
            for (int idx = 0; idx < 5; idx++) {
                available.add(getActivity(idx, orderIdx));
            }
        }
        when(activityRepository.findQuestionAcceptable()).thenReturn(available);

        // Provide the questions
        List<Question> questions = questionService.provideQuestions(4);
        assertEquals(4, questions.size());
        assertTrue(questions.stream().anyMatch(q -> q instanceof EstimateQuestion));
        List<MCQuestion> mcQuestions = questions.stream()
                .filter(q -> q instanceof MCQuestion)
                .map(q -> (MCQuestion) q)
                .collect(Collectors.toList());
        assertEquals(3, mcQuestions.size());
        assertTrue(mcQuestions.stream().anyMatch(q -> q.getQuestionType() == MCType.GUESS_COST));
        assertTrue(mcQuestions.stream().anyMatch(q -> q.getQuestionType() == MCType.GUESS_ACTIVITY));
        assertTrue(mcQuestions.stream().anyMatch(q -> q.getQuestionType() == MCType.INSTEAD_OF));

        // Verify interactions
        verify(activityRepository).findQuestionAcceptable();
        verify(questionRepository).saveAll(any());
        verifyNoMoreInteractions(activityRepository, questionRepository);
    }
}