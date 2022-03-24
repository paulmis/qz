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
    }

    /**
     * Run finite state machine.
     */
    @SneakyThrows
    @Override
    public void run() {
        log.trace("[{}] FSM runnable called.", getGame().getId());
        getContext().getGameService().setAcceptingAnswers(getGame(), !getGame().isAcceptingAnswers());
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
            getContext().getSseManager().send(getGame().getPlayers().keySet(), new SSEMessage(SSEMessageType.GAME_END));
        } else {
            run();
        }
    }
}