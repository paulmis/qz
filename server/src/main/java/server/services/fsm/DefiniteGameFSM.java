package server.services.fsm;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.services.SSEManager;

/**
 * Runnable for definite game finite state machine.
 */
@Slf4j
public class DefiniteGameFSM extends GameFSM {

    public DefiniteGameFSM(DefiniteGame game, FSMContext context) {
        super(game, context);
        if (game.getCurrentQuestion() == game.getQuestionsCount()) {
            throw new IllegalStateException("Game is already finished.");
        }
        if (game.isAcceptingAnswers()) {
            throw new IllegalStateException("Game is already accepting answers.");
        }
        if (game.getCurrentQuestion() != 0) {
            throw new IllegalStateException("Game is already in progress.");
        }
    }

    /**
     * Run finite state machine.
     */
    @SneakyThrows
    @Override
    public void run() {
        while (getGame().getCurrentQuestion()
                != ((NormalGameConfiguration) getGame().getConfiguration()).getNumQuestions()) {
            log.trace("[{}] FSM runnable called.", getGame().getId());

            if (getGame().isAcceptingAnswers()) {
                // Disable accepting answers.
                getContext().getGameService().setAcceptingAnswers(getGame(),
                        false,
                        5000L);

                log.trace("[{}] FSM runnable: accepting answers disabled.", getGame().getId());

                // If we are accepting answers, get duration of the question.
                Thread.sleep(5000L);

                log.trace("[{}] FSM runnable: advancing onto the next question.", getGame().getId());
                // Move onto the next question.
                getGame().setCurrentQuestion(getGame().getCurrentQuestion() + 1);
            } else {
                // Enable accepting answers.
                getContext().getGameService().setAcceptingAnswers(getGame(),
                        true,
                        getGame().getConfiguration().getAnswerTime() * 1000L);

                log.trace("[{}] FSM runnable: accepting answers enabled.", getGame().getId());

                // If we are not accepting answers, sleep for the duration of leaderboard.
                Thread.sleep(getGame().getConfiguration().getAnswerTime() * 1000L);
            }

            // Otherwise, continue running the runnable
            log.trace("[{}] FSM runnable continuing.", getGame().getId());
        }

        log.debug("[{}] Game is finished.", getGame().getId());

        // We are not accepting answers anymore.
        getContext().getGameService().setAcceptingAnswers(getGame(), false);

        // The game is finished.
        getGame().setStatus(GameStatus.FINISHED);
        getContext().getSseManager().send(getGame().getPlayerIds(),
                new SSEMessage(SSEMessageType.GAME_END));
    }
}