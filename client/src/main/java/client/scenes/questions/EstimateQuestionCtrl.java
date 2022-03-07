package client.scenes.questions;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 * Estimate Question type controller.
 */
public class EstimateQuestionCtrl implements Initializable {

    /**
     * The interface for the answer handler.
     * Handles the change text event.
     */
    public interface AnswerHandler {
        /**
         * Handle function of the interface.
         * Deals with the change of the text field.
         *
         * @param text The text contexts of the field.
         */
        void handle(String text);
    }

    private final String questionText;
    private final AnswerHandler answerHandler;

    @FXML private Label questionLabel;
    @FXML private TextField guessField;


    /**
     * Constructor for the estimate question control.
     *
     * @param questionText The text description of the question
     * @param answerHandler The action that is to be performed when the player answers.
     */
    public EstimateQuestionCtrl(String questionText, AnswerHandler answerHandler) {
        this.questionText = questionText;
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

        // The following listener handles the input for the
        // estimate question type text field.
        this.guessField.textProperty().addListener((observable, oldValue, newValue) -> {

            // We check if there exists a non digit character
            if (!newValue.matches("\\d*")) {

                // We remove the non digits characters if they are present.
                this.guessField.setText(newValue.replaceAll("[^\\d]", ""));
            } else {

                // If the input is valid we just call the handle method with
                // the new string value
                answerHandler.handle(newValue);
            }
        });

        // This line sets the question text to the received argument.
        this.questionLabel.setText(questionText);
    }
}
