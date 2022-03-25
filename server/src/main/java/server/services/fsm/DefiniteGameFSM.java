package server.services.fsm;

import commons.entities.game.GameStatus;
import commons.entities.messages.SSEMessage;
import commons.entities.messages.SSEMessageType;
import java.io.IOException;
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
        log.trace("[{}] FSM runnable called.", getGame().getId());
        if (!getGame().isAcceptingAnswers()) {
            runQuestion();
        } else {
            runAnswer();
        }
    }

    void runAnswer() throws IOException {
        int delay = getGame().getCurrentQuestion() % 5 == 4 ? 10000 : 5000;

        getContext().getGameService().setAcceptingAnswers(getGame(),
                false,
                delay);

        log.trace("[{}] FSM runnable: accepting answers disabled.", getGame().getId());

        // Calculate when to run the next question.
        // Show leaderboard on every 5th question.
        Date executionTime = DateUtils.addMilliseconds(new Date(), delay);
        setFuture(new FSMFuture(Optional.of(getContext().getTaskScheduler().schedule(() -> {
            try {
                runQuestion();
            } catch (IOException e) {
                log.error("[{}] FSM runnable: error in question stage.", getGame().getId(), e);
                e.printStackTrace();
            }
        }, executionTime)),
                executionTime));
    }

    void runQuestion() throws IOException {
        log.trace("[{}] FSM runQuestion called.", getGame().getId());

        if (getGame().getCurrentQuestion()
                == ((DefiniteGame) getGame()).getQuestionsCount()) {
            finishGame();
            return;
        }

        // Move onto the next question.
        log.debug("[{}] FSM runnable: advancing onto the next question.", getGame().getId());
        getGame().setCurrentQuestion(getGame().getCurrentQuestion() + 1);

        // Enable accepting answers.
        getContext().getGameService().setAcceptingAnswers(getGame(),
                true,
                getGame().getConfiguration().getAnswerTime());

        log.trace("[{}] FSM runnable: accepting answers enabled.", getGame().getId());

        Date executionTime = DateUtils.addMilliseconds(new Date(),
                getGame().getConfiguration().getAnswerTime());
        setFuture(new FSMFuture(Optional.of(getContext().getTaskScheduler().schedule(() -> {
            try {
                runAnswer();
            } catch (IOException e) {
                log.error("[{}] FSM runnable: error in answer stage.", getGame().getId(), e);
                e.printStackTrace();
            }
        }, executionTime)),
                executionTime));
    }

    void finishGame() throws IOException {
        log.debug("[{}] Game is finished.", getGame().getId());

        // We are not accepting answers anymore.
        getContext().getGameService().setAcceptingAnswers(getGame(), false);

        // The game is finished.
        getGame().setStatus(GameStatus.FINISHED);
        getContext().getSseManager().send(getGame().getPlayerIds(),
                new SSEMessage(SSEMessageType.GAME_END));
    }
}