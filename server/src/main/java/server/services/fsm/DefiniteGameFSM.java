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

        // At the beginning of the game, give users a few seconds to prepare.
        if (getState() == FSMState.IDLE) {
            setState(FSMState.PREPARING);
            scheduleTask(this::run, Duration.ofSeconds(5));
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
     * Leaderboard stage. In this stage, the leaderboard is shown for a fixed amount of time.
     * The FSM then transitions into the question stage.
     */
    @SneakyThrows
    void runLeaderboard() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            return;
        }

        log.trace("[{}] FSM runLeaderboard called.", getGame().getId());
        setState(FSMState.LEADERBOARD);

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

    /**
     * Answer stage. In this stage, the players are shown the current question and are allowed to answer.
     * The FSM then transitions into either the leaderboard stage (every n questions), or question stage.
     */
    @SneakyThrows
    void runAnswer() {
        // If we received a stop signal, return immediately.
        if (!isRunning()) {
            return;
        }

        log.trace("[{}] FSM runAnswer called.", getGame().getId());
        setState(FSMState.ANSWER);

        // Delay before progressing to the next stage.
        int delay = 5000; // TODO: make this configurable?
        // Show the leaderboard every <leaderboardInterval> questions.
        int leaderboardInterval = 5; // TODO: make this configurable?

        // Stop accepting answers.
        getContext().getGameService().setAcceptingAnswers(getGame(),
                false,
                (long) delay);
        log.trace("[{}] FSM runnable: accepting answers disabled.", getGame().getId());

        // Show leaderboard on every 5th question, and show next question on other questions.
        scheduleTask(
                (getGame().getCurrentQuestion() + 1) % leaderboardInterval == 0
                        ? this::runLeaderboard
                        : this::runQuestion,
                Duration.ofMillis(delay)
        );
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
            return;
        }

        log.trace("[{}] FSM runQuestion called.", getGame().getId());
        setState(FSMState.QUESTION);

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
                getGame().getConfiguration().getAnswerTime().toMillis());

        log.trace("[{}] FSM runnable: accepting answers enabled.", getGame().getId());

        // Schedule the "show answer" stage.
        scheduleTask(this::runAnswer, getGame().getConfiguration().getAnswerTime());
    }

    @SneakyThrows
    void runFinish() {
        log.debug("[{}] Game is finished.", getGame().getId());
        setState(FSMState.FINISHED);

        // Stop the FSM
        setRunning(false);

        // We are not accepting answers anymore.
        getContext().getGameService().setAcceptingAnswers(getGame(), false);

        // Mark game as finished and notify all players.
        getGame().setStatus(GameStatus.FINISHED);
        getContext().getSseManager().send(getGame().getPlayerIds(), new SSEMessage(SSEMessageType.GAME_END));
    }
}