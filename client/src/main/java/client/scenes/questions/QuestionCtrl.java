package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.AnswerDTO;
import javafx.fxml.Initializable;

/**
 * Generic controller for all question types.
 */
public abstract class QuestionCtrl implements Initializable {
    protected final MainCtrl mainCtrl;
    protected final GameCommunication communication;

    /**
     * Constructor of the generic question control.
     *
     * @param mainCtrl the main controller
     * @param gameCommunication the communication class
     */
    public QuestionCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication) {
        this.mainCtrl = mainCtrl;
        this.communication = gameCommunication;
    }

    /**
     * Shows the correct answer in the UI.
     *
     * @param answer the correct answer.
     */
    protected abstract void showAnswer(AnswerDTO answer);
}
