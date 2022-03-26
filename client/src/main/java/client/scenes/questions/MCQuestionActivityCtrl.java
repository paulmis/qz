package client.scenes.questions;

import client.communication.game.AnswerHandler;
import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * MCQuestion controller for questions with activities as answers.
 */
public class MCQuestionActivityCtrl extends MCQuestionCtrl {
    @FXML private ImageView imageOptionA;
    @FXML private ImageView imageOptionB;
    @FXML private ImageView imageOptionC;
    @FXML private ImageView imageOptionD;

    public MCQuestionActivityCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication, MCQuestionDTO question) {
        super(mainCtrl, gameCommunication, question);
    }

    /**
     * Initializes the controls of this MCQuestionPane.
     *
     * @param location the location parameter.
     * @param resources the resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Initialize images
        var imageOptionArray = Arrays.asList(imageOptionA, imageOptionB, imageOptionC, imageOptionD);

        // Initialize answers
        for (int i = 0; i < getLabels().size(); i++) {
            getLabels().get(i).setText(question.getActivities().get(i).getDescription());
        }

        // Looping over they answer controls
        for (ImageView imageOption : imageOptionArray) {
            // Sets the image of the imageView to the url specified
            // in answerImages
            imageOption.setImage(null);

            // This code resizes the image view to the surrounding vbox.
            // It uses a bind on the minimum of the vbox width and height and multiplies that by 0.8.
            // In this way the image resizes properly to a lot of screen sizes.
            imageOption.fitHeightProperty().bind(Bindings.min(
                super.imageVBox.widthProperty(),
                imageVBox.heightProperty()).multiply(0.8));

            // This just binds the width of the image to the height
            // This is assuming that the images will be feed in as a squares.
            imageOption.fitWidthProperty().bind(imageOption.fitHeightProperty());
        }
    }
}
