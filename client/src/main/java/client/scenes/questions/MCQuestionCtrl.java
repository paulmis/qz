package client.scenes.questions;

import client.communication.game.AnswerHandler;
import com.jfoenix.controls.JFXButton;
import commons.entities.AnswerDTO;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * Multiple Choice Question Controller.
 */
@Generated
public class MCQuestionCtrl implements Initializable {

    @FXML private Label questionLabel;
    @FXML private ImageView imageOptionA;
    @FXML private ImageView imageOptionB;
    @FXML private ImageView imageOptionC;
    @FXML private ImageView imageOptionD;
    @FXML private Label labelOptionA;
    @FXML private Label labelOptionB;
    @FXML private Label labelOptionC;
    @FXML private Label labelOptionD;
    @FXML private JFXButton buttonOptionA;
    @FXML private JFXButton buttonOptionB;
    @FXML private JFXButton buttonOptionC;
    @FXML private JFXButton buttonOptionD;
    @FXML private VBox imageVBox;

    private final MCQuestionDTO question;
    private final AnswerHandler answerHandler;

    public MCQuestionCtrl(MCQuestionDTO question, AnswerHandler answerHandler) {
        this.question = question;
        this.answerHandler = answerHandler;
    }

    /**
     * This function runs after every control has
     * been created and initialized already.
     *
     * @param location These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Sets the question text
        this.questionLabel.setText(question.getText());

        // Three arrays for the controls of the answers.
        // They are created for easier referencing.
        var imageOptionArray = Arrays.asList(imageOptionA, imageOptionB, imageOptionC, imageOptionD);
        var labelOptionArray = Arrays.asList(labelOptionA, labelOptionB, labelOptionC, labelOptionD);
        var buttonOptionArray = Arrays.asList(buttonOptionA, buttonOptionB, buttonOptionC, buttonOptionD);

        // Looping over they answer controls
        for (int i = 0; i < imageOptionArray.size(); i++) {
            var imageOption = imageOptionArray.get(i);

            // Sets the image of the imageView to the url specified
            // in answerImages
            imageOption.setImage(null);

            // This code resizes the image view to the surrounding vbox.
            // It uses a bind on the minimum of the vbox width and height and multiplies that by 0.8.
            // In this way the image resizes properly to a lot of screen sizes.
            imageOption.fitHeightProperty().bind(Bindings.min(
                    imageVBox.widthProperty(),
                    imageVBox.heightProperty()).multiply(0.8));

            // This just binds the width of the image to the height
            // This is assuming that the images will be feed in as a squares.
            imageOption.fitWidthProperty().bind(imageOption.fitHeightProperty());

            var labelOption = labelOptionArray.get(i);
            long cost = question.getActivities().get(i).getCost();
            labelOption.setText(Long.toString(cost));

            // Gets the button and assign the action to it from the
            // list of actions
            var buttonOption = buttonOptionArray.get(i);
            // TODO: set after rebase
            buttonOption.setOnAction((actionEvent) -> answerHandler.handle(new AnswerDTO()));
        }
    }
}
