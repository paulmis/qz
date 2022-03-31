package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * MCQuestion controller for questions with costs as answers.
 */
public class MCQuestionCostCtrl extends MCQuestionCtrl {
    public MCQuestionCostCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication, MCQuestionDTO question) {
        super(mainCtrl, gameCommunication, question);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Initialize answers
        for (int i = 0; i < getLabels().size(); i++) {
            getLabels().get(i).setText(question.getActivities().get(i).getCostWithHighestUnit());
        }
    }
}
