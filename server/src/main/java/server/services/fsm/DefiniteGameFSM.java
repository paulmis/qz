package server.services.fsm;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.exceptions.GameFinishedException;

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
    public DefiniteGameFSM(DefiniteGame<?> game, FSMContext context) {
        super(game, context);
        if (game.getStatus() != GameStatus.ONGOING) {
            log.warn("[{}] Attempt to construct a FSM on a " + game.getStatus() + " game", game.getId());
            throw new IllegalStateException("Attempt to construct a FSM on a " + game.getStatus() + " game");
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

        // At the beginning of the game, give users a few seconds to prepare.
        if (getState() == FSMState.IDLE) {
            log.debug("[{}] FSM is in PREPARING state.", getGame().getId());
            setState(FSMState.PREPARING);
            scheduleTask(this::run,
                    Duration.ofMillis(getContext().getQuizConfiguration().getTiming().getPreparationTime()));
            return;
        }

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

    /**
     * Question stage. In this stage, the question is shown for some time (amount provided by the Game entity).
     * Players are allowed to submit answers.
     * The FSM then transitions into the answer stage, or the finished stage, if the game reached its conclusion.
     */
    @SneakyThrows
    void runQuestion() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            log.debug("[{}] Stop signal received, FSM exiting.", getGame().getId());
            return;
        }

        log.trace("[{}] FSM runQuestion called.", getGame().getId());
        setState(FSMState.QUESTION);

        // If the game is finished, run the cleanup function and return immediately.
        try {
            // Move onto the next question.
            log.debug("[{}] FSM runnable: advancing onto question {}.",
                getGame().getId(), getGame().getCurrentQuestionNumber());
            getContext().getGameService()
                .nextQuestion(
                    getGame(),
                    getGame().getConfiguration().getAnswerTime().toMillis());

            // Schedule the "show answer" stage.
            scheduleTask(this::runAnswer, getGame().getConfiguration().getAnswerTime());
        } catch (GameFinishedException e) {
            runFinish();
        }
    }

    /**
     * Answer stage. In this stage, the players are shown the current question and are allowed to answer.
     * The FSM then transitions into either the leaderboard stage (every n questions), or question stage.
     */
    @SneakyThrows
    void runAnswer() {
        // If we received a stop signal, return immediately
        if (!isRunning()) {
            log.debug("[{}] Stop signal received, FSM exiting.", getGame().getId());
            return;
        }

        log.trace("[{}] FSM runAnswer called.", getGame().getId());
        setState(FSMState.ANSWER);

        // Update the scores
        getContext().getGameService().updateScores(getGame());

        // Delay before progressing to the next stage
        long delay = getContext().getQuizConfiguration().getTiming().getAnswerTime();
        // Show the leaderboard every <leaderboardInterval> questions
        int leaderboardInterval = getContext().getQuizConfiguration().getLeaderboardInterval();

        // Stop accepting answers
        getContext().getGameService().showAnswer(getGame(), delay);

        // Show leaderboard on every 5th question, and show next question on other questions
        scheduleTask(
                (getGame().getCurrentQuestionNumber() + 1) % leaderboardInterval == 0
                        ? this::runLeaderboard
                        : this::runQuestion,
                Duration.ofMillis(delay));
    }

    /**
     * Leaderboard stage. In this stage, the leaderboard is shown for a fixed amount of time.
     * The FSM then transitions into the question stage.
     */
    @SneakyThrows
    void runLeaderboard() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            log.debug("[{}] Stop signal received, FSM exiting.", getGame().getId());
            return;
        }

        log.trace("[{}] FSM runLeaderboard called.", getGame().getId());
        setState(FSMState.LEADERBOARD);

        // Delay before progressing to the next stage.
        int delay = getContext().getQuizConfiguration().getTiming().getLeaderboardTime();

        // Notify all players to show the leaderboard.
        getContext().getSseManager().send(getGame().getPlayerIds(),
            new SSEMessage(SSEMessageType.SHOW_LEADERBOARD, delay));
        log.trace("[{}] Leaderboard shown.", getGame().getId());

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
    void runFinish() {
        // We are not accepting answers anymore.
        getContext().getGameService().finish(getGame());

        // Stop the FSM
        setState(FSMState.FINISHED);
        setRunning(false);
    }
}