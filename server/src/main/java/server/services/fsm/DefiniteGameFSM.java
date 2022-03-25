package server.services.fsm;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.util.Date;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import server.database.entities.game.DefiniteGame;

/**
 * Runnable for definite game finite state machine.
 */
@Slf4j
public class DefiniteGameFSM extends GameFSM {

    /**
     * Create a new runnable for the finite state machine.
     *
     * @param game    The game instance.
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
        log.trace("[{}] FSM runnable called.", getGame().getId());
        // Mark the FSM as running.
        setRunning(true);

        if (!getGame().isAcceptingAnswers()) {
            // If the game is currently not accepting answers,
            // we start accepting answers (go forward to the next question).
            runQuestion();
        } else {
            // If the game is currently accepting answers,
            // we show the answer (and potentially the leaderboard).
            runAnswer();
        }
    }

    @SneakyThrows
    void runLeaderboard() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            return;
        }

        log.trace("[{}] FSM runLeaderboard called.", getGame().getId());

        // TODO: make this configurable?
        int delay = 5000; // Delay before progressing to the next stage.

        // Notify all players to show the leaderboard.
        getContext().getSseManager().send(getGame().getPlayerIds(),
            new SSEMessage(SSEMessageType.SHOW_LEADERBOARD, delay));

        // Calculate when to proceed to the next stage.
        Date executionTime = DateUtils.addMilliseconds(new Date(), delay);
        // Schedule the next stage.
        setFuture(
                new FSMFuture(
                        Optional.of(getContext().getTaskScheduler().schedule(this::runQuestion, executionTime)),
                        executionTime)
        );
    }

    @SneakyThrows
    void runAnswer() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            return;
        }

        log.trace("[{}] FSM runAnswer called.", getGame().getId());

        // Delay before progressing to the next stage.
        int delay = 5000; // TODO: make this configurable?

        // Stop accepting answers.
        getContext().getGameService().setAcceptingAnswers(getGame(),
                false,
                delay);
        log.trace("[{}] FSM runnable: accepting answers disabled.", getGame().getId());

        // Calculate when to run the next question.
        Date executionTime = DateUtils.addMilliseconds(new Date(), delay);
        // Show leaderboard on every 5th question, and show next question on other questions.
        setFuture(new FSMFuture(Optional.of(getContext().getTaskScheduler().schedule(
                (getGame().getCurrentQuestion() % 5 == 4)
                        ? this::runLeaderboard
                        : this::runQuestion, executionTime)),
                executionTime));
    }

    @SneakyThrows
    void runQuestion() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            return;
        }

        log.trace("[{}] FSM runQuestion called.", getGame().getId());

        // If the game is finished, run the cleanup function and return immediately.
        if (getGame().getCurrentQuestion()
                == ((DefiniteGame) getGame()).getQuestionsCount()) {
            runFinish();
            return;
        }

        // Move onto the next question.
        log.debug("[{}] FSM runnable: advancing onto the next question.", getGame().getId());
        getGame().setCurrentQuestion(getGame().getCurrentQuestion() + 1);

        // Start accepting answers.
        getContext().getGameService().setAcceptingAnswers(getGame(),
                true,
                getGame().getConfiguration().getAnswerTime());

        log.trace("[{}] FSM runnable: accepting answers enabled.", getGame().getId());

        // Schedule the "show answer" stage.
        Date executionTime = DateUtils.addMilliseconds(new Date(),
                getGame().getConfiguration().getAnswerTime());
        setFuture(new FSMFuture(Optional.of(getContext().getTaskScheduler().schedule(this::runAnswer,
                executionTime)), executionTime));
    }

    @SneakyThrows
    void runFinish() {
        log.debug("[{}] Game is finished.", getGame().getId());

        // Stop the FSM
        setRunning(false);

        // We are not accepting answers anymore.
        getContext().getGameService().setAcceptingAnswers(getGame(), false);

        // Mark game as finished.
        getGame().setStatus(GameStatus.FINISHED);
        // Notify all players.
        getContext().getSseManager().send(getGame().getPlayerIds(),
                new SSEMessage(SSEMessageType.GAME_END));
    }
}