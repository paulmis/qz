package client.scenes.questions;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * Multiple Choice Question Controller.
 */
@Generated
public class MultipleChoiceQuestionCtrl implements Initializable {


    /**
     * The interface for the answer handler.
     * Handles the click option event.
     */
    public interface AnswerHandler {
        /**
         * Handle function of the interface.
         * Deals with the click of one of the buttons.
         */
        void handle();
    }

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

    private final String questionText;
    private final List<URL> answersImages;
    private final List<String> answersText;
    private final List<AnswerHandler> actions;

    /**
     * Constructs the controller for multiple choice question type.
     *
     * @param questionText The question in string format.
     * @param answersImages A list of url images of the answers.
     * @param answersText A list of texts of the answers.
     * @param actions A list of actions that each option should perform.
     */
    public MultipleChoiceQuestionCtrl(String questionText,
                                      List<URL> answersImages,
                                      List<String> answersText,
                                      List<AnswerHandler> actions) {
        this.questionText = questionText;
        this.answersImages = answersImages;
        this.answersText = answersText;
        this.actions = actions;
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
        this.questionLabel.setText(questionText);

        // Three arrays for the controls of the answers.
        // They are created for easier referencing.
        var imageOptionArray = Arrays.asList(imageOptionA, imageOptionB, imageOptionC, imageOptionD);
        var labelOptionArray = Arrays.asList(labelOptionA, labelOptionB, labelOptionC, labelOptionD);
        var buttonOptionArray = Arrays.asList(buttonOptionA, buttonOptionB, buttonOptionC, buttonOptionD);

        // Looping over they answer controls
        for (int i = 0; i < imageOptionArray.size(); i++) {


            var imageOption = imageOptionArray.get(i);
            var imageUrl = answersImages.get(i);

            // Sets the image of the imageView to the url specified
            // in answerImages
            imageOption.setImage(new Image(imageUrl.toString()));

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
            var answerText = answersText.get(i);
            labelOption.setText(answerText);

            // Gets the button and assign the action to it from the
            // list of actions
            var buttonOption = buttonOptionArray.get(i);
            var action = actions.get(i);
            buttonOption.setOnAction((actionEvent) -> action.handle());
        }
    }
}
