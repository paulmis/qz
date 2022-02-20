package client.scenes.questions;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/** Estimate Question type controller.
 *
 */
public class EstimateQuestionCtrl implements Initializable {

    /** The interface for the answer handler.
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

    @FXML
    private Label questionLabel;

    @FXML
    private TextField guessField;

    public EstimateQuestionCtrl(String questionText, AnswerHandler answerHandler) {
        this.questionText = questionText;
        this.answerHandler = answerHandler;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.guessField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.guessField.setText(newValue.replaceAll("[^\\d]", ""));
            } else {
                answerHandler.handle(newValue);
            }
        });

        this.questionLabel.setText(questionText);
    }
}
