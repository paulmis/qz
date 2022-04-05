package client.scenes.questions;

import static javafx.application.Platform.runLater;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.AnswerDTO;
import commons.entities.questions.EstimateQuestionDTO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;


/**
 * Estimate Question controller.
 */
@Generated
@Slf4j
public class EstimateQuestionCtrl extends QuestionCtrl {
    private final EstimateQuestionDTO question;

    @FXML private Label questionLabel;
    @FXML private TextField guessField;
    @FXML private VBox imageVBox;
    @FXML private ImageView questionIcon;

    /**
     * Constructor for the estimate question control.
     *
     * @param mainCtrl          the main controller
     * @param gameCommunication the communication class
     * @param question          the question to show
     */
    public EstimateQuestionCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication, EstimateQuestionDTO question) {
        super(mainCtrl, gameCommunication);
        this.question = question;
    }

    /**
     * This function runs after every control has
     * been created and initialized already.
     *
     * @param location  The location parameter.
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
                AnswerDTO myAnswer = new AnswerDTO(question.getId(), List.of(question.getActivities().get(0)));
                myAnswer.getResponse().get(0).setCost(Long.parseLong(this.guessField.getText()));

                // Send the answer
                GameCommunication.putAnswer(
                        ClientState.game.getId(),
                        myAnswer,
                        // Success
                        () -> {},
                        // Failure
                        () -> runLater(() ->
                                mainCtrl.showErrorSnackBar("Unable to send the answer")
                        )
                );
            }
        });

        // This line sets the question text to the received argument.
        this.questionLabel.setText(question.getText());

        // Set question image
        questionIcon.setImage(new Image(ServerUtils.getImagePathFromId(question.getQuestionIconId()), true));
    }

    @Override
    protected void showAnswer(AnswerDTO answer) {
        if (!this.question.getId().equals(answer.getQuestionId())) {
            log.error("Received answer for a different question: expected {} but got {}",
                    this.question.getId(), answer.getQuestionId());
            mainCtrl.showErrorSnackBar("Received answer for the wrong question.");
            return;
        }

        log.debug("Showing answer: {}", answer);

        // Evaluate the correctness of the answer
        boolean correctAnswer;
        try {
            long oldGuess = Long.parseLong(guessField.getText());
            correctAnswer = oldGuess == answer.getResponse().get(0).getCost();
        } catch (NumberFormatException numEx) {
            // No answer was given, automatically wrong
            correctAnswer = false;
        }

        // Display the correct answer
        guessField.setText(answer.getResponse().get(0).getCost().toString());

        // Apply the style to indicate that the answer is being shown
        guessField.setEditable(false);
        guessField.getStyleClass().add("show-answer");
        guessField.getStyleClass().add(correctAnswer
                ? "correct-answer"
                : "incorrect-answer");
    }
}
