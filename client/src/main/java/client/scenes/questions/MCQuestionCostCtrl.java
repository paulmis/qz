package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.utils.communication.ServerUtils;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * MCQuestion controller for questions with costs as answers.
 */
public class MCQuestionCostCtrl extends MCQuestionCtrl {

    @FXML
    private ImageView questionPic;

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

        // Set question image
        questionPic.setImage(new Image(ServerUtils.getImagePathFromId(question.getQuestionIconId())));

        // Resize the image view to the surrounding vbox.
        questionPic.fitHeightProperty().bind(Bindings.min(
                super.imageVBox.widthProperty(),
                imageVBox.heightProperty()).multiply(0.8));
        questionPic.fitWidthProperty().bind(questionPic.fitHeightProperty());
    }
}
