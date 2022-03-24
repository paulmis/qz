package server.services.fsm;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.configuration.NormalGameConfiguration;

/**
 * Runnable for definite game finite state machine.
 */
@Slf4j
public class DefiniteGameFSM extends GameFSM {

    /**
     * Create a new runnable for the finite state machine.
     *
     * @param game The game instance.
     * @param context The execution context of the FSM.
     */
    public DefiniteGameFSM(DefiniteGame game, FSMContext context) {
        super(game, context);
        if (game.getCurrentQuestion() == game.getQuestionsCount()) {
            log.warn("[{}] Attempt to construct a FSM on a finished game.", game.getId());
            throw new IllegalStateException("Game is already finished.");
        }
        log.debug("[{}] DefiniteFSM created.", game.getId());
    }

    /**
     * Run finite state machine.
     */
    @SneakyThrows
    @Override
    public void run() {
        // TODO: utilize thread pools instead of this loop - susceptible to DoS (thread exhaustion)
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
                        getGame().getConfiguration().getAnswerTime());

                log.trace("[{}] FSM runnable: accepting answers enabled.", getGame().getId());

                // If we are not accepting answers, sleep for the duration of leaderboard.
                Thread.sleep(getGame().getConfiguration().getAnswerTime());
            }
            log.trace("[{}] FSM runnable loop end.", getGame().getId());
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