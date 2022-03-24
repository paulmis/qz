package server.services.fsm;

import commons.entities.game.GameStatus;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import server.database.entities.game.DefiniteGame;
import server.database.entities.game.configuration.NormalGameConfiguration;

/**
 * Runnable for definite game finite state machine.
 */
@Slf4j
public class DefiniteGameFSM extends GameFSM {

    public DefiniteGameFSM(DefiniteGame game) {
        super(game);
    }

    /**
     * Run finite state machine.
     */
    @SneakyThrows
    @Override
    public void run() {
        log.trace("[{}] FSM runnable called.", getGame().getId());
        getGame().setAcceptingAnswers(!getGame().isAcceptingAnswers());
        log.trace("[{}] waiting....", getGame().getId());
        if (getGame().isAcceptingAnswers()) {
            Thread.sleep(getGame().getConfiguration().getAnswerTime());
        } else {
            Thread.sleep(5000L);
        }

        log.trace("[{}] FSM runnable finished.", getGame().getId());
        getGame().setCurrentQuestion(getGame().getCurrentQuestion() + 1);
        if (getGame().getCurrentQuestion()
                == ((NormalGameConfiguration) getGame().getConfiguration()).getNumQuestions()) {
            getGame().setStatus(GameStatus.FINISHED);
        } else {
            run();
        }
    }
}
