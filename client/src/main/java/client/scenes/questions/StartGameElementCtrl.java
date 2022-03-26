package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * MCQuestion controller for questions with costs as answers.
 */
public class StartGameElementCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final GameCommunication communication;

    @FXML
    protected Label startLabel;

    /**
     * Constructor for EmptyQuestionCtrl.
     *
     * @param mainCtrl the main controller
     * @param gameCommunication the communication class
     */
    public StartGameElementCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication) {
        this.mainCtrl = mainCtrl;
        this.communication = gameCommunication;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
