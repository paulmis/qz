package server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.entities.game.GameStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.entities.game.GamePlayer;
import server.database.entities.game.NormalGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.question.MCQuestion;
import server.database.entities.question.Question;
import server.database.repositories.question.QuestionRepository;
import server.services.GameService;

/**
 * Tests for GameService class.
 */
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private GameService gameService;

    NormalGame game;
    GamePlayer joe;
    MCQuestion questionA;
    MCQuestion questionB;
    MCQuestion questionC;
    MCQuestion questionD;
    List<UUID> usedQuestionIds;

    @BeforeEach
    void init() {
        joe = new GamePlayer();
        questionA = new MCQuestion();
        questionA.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        questionB = new MCQuestion();
        questionB.setId(UUID.fromString("11111111-2222-2222-2222-222222222222"));
        questionC = new MCQuestion();
        questionC.setId(UUID.fromString("11111111-3333-3333-3333-333333333333"));
        questionD = new MCQuestion();
        questionD.setId(UUID.fromString("11111111-4444-4444-4444-444444444444"));
        usedQuestionIds = Arrays.asList(questionB.getId(), questionA.getId());

        game = new NormalGame();
        game.setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        game.setConfiguration(new NormalGameConfiguration(3, 13, 6));
        game.add(joe);

        // Mock the repository
        when(questionRepository.count()).thenReturn(4L);
    }

    @Test
    void provideQuestionsOk() {
        // Mock the repository
        when(questionRepository.findByIdNotIn(usedQuestionIds))
                .thenReturn(Arrays.asList(questionD, questionC));

        // Provide the questions
        List<Question> questions = gameService.provideQuestions(2, Arrays.asList(questionB, questionA));
        assertEquals(2, questions.size());
        assertThat(questions).hasSameElementsAs(Arrays.asList(questionC, questionD));

        // Verify interactions
        verify(questionRepository).count();
        verify(questionRepository).findByIdNotIn(usedQuestionIds);
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void provideQuestionsNotEnough() {
        // Expect a throw
        assertThrows(IllegalStateException.class,
                () -> gameService.provideQuestions(3, Arrays.asList(questionB, questionA)));

        // Verify interactions
        verify(questionRepository).count();
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void startGameNormal() {
        // Mock the repository
        when(questionRepository.findByIdNotIn(new ArrayList<>()))
                .thenReturn(Arrays.asList(questionA, questionC, questionB, questionD));

        // Start the game
        gameService.startGame(game);

        // Check that the questions have been generated and the status was changed
        assertEquals(3, game.getQuestions().size());
        assertEquals(GameStatus.ONGOING, game.getStatus());

        // Verify interactions
        verify(questionRepository).count();
        verify(questionRepository).findByIdNotIn(new ArrayList<>());
        verifyNoMoreInteractions(questionRepository);
    }
}
