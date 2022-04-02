package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.AnswerDTO;
import javafx.fxml.Initializable;

public abstract class QuestionCtrl implements Initializable {
    protected final MainCtrl mainCtrl;
    protected final GameCommunication communication;

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
