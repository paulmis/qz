package client.scenes.questions;

import static javafx.application.Platform.runLater;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import com.jfoenix.controls.JFXButton;
import commons.entities.AnswerDTO;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * MCQuestion controller.
 */
@Generated
@Slf4j
public abstract class MCQuestionCtrl extends QuestionCtrl {
    @FXML protected Label questionLabel;
    @FXML protected Label labelOptionA;
    @FXML protected Label labelOptionB;
    @FXML protected Label labelOptionC;
    @FXML protected Label labelOptionD;
    @FXML protected JFXButton buttonOptionA;
    @FXML protected JFXButton buttonOptionB;
    @FXML protected JFXButton buttonOptionC;
    @FXML protected JFXButton buttonOptionD;
    @FXML protected VBox imageVBox;

    protected JFXButton chosenAnswer = null;

    protected final MCQuestionDTO question;

    /**
     * Constructor for MCQuestionCtrl.
     *
     * @param mainCtrl the main controller
     * @param gameCommunication the communication class
     * @param question the question to show
     */
    public MCQuestionCtrl(MainCtrl mainCtrl, GameCommunication gameCommunication, MCQuestionDTO question) {
        super(mainCtrl, gameCommunication);
        this.question = question;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sets the question text
        this.questionLabel.setText(question.getText());

        // Specify behaviour on answer click
        for (int i = 0; i < getLabels().size(); i++) {
            // Get the button
            int finalI = i;
            JFXButton button = getButtons().get(i);

            // Add the callback
            button
                .setOnAction((actionEvent) -> {
                    SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
                    if (button != chosenAnswer) {
                        // Send the answer
                        GameCommunication.putAnswer(
                            ClientState.game.getId(),
                            new AnswerDTO(question.getId(),
                                    List.of(question.getActivities().get(finalI))),
                            // Success
                            () -> runLater(() -> setCurrentAnswer(button)),
                            // Failure
                            () -> runLater(() ->
                                mainCtrl.showErrorSnackBar("Unable to send the answer")
                            )
                        );
                    }
                });
        }
    }

    protected void showAnswer(AnswerDTO answer) {
        if (!this.question.getId().equals(answer.getQuestionId())) {
            log.error("Received answer for a different question: expected {} but got {}",
                this.question.getId(), answer.getQuestionId());
            mainCtrl.showErrorSnackBar("Received answer for the wrong question.");
            return;
        }

        log.debug("Showing answer: {}", answer);

        // Evaluate each button to show the correct answer
        UUID answerActivity = answer.getResponse().get(0).getId();
        for (int i = 0; i < getButtons().size(); ++i) {
            JFXButton button = getButtons().get(i);
            button.setMouseTransparent(true);

            // ToDo: the game should consider correct all the options with the correct _displayed_ value
            if (question.getActivities().get(i).getId().equals(answerActivity)) {
                button.getStyleClass().add("correct-answer");
            } else if (this.chosenAnswer == button) {
                button.getStyleClass().add("incorrect-answer");
            }
        }
    }

    /**
     * Sets the current answer.
     *
     * @param button the button representing the current answer
     */
    protected void setCurrentAnswer(JFXButton button) {
        // Remove styles from the old answer
        if (this.chosenAnswer != null) {
            this.chosenAnswer.getStyleClass().remove("chosen-answer");
        }

        // Set the new answer
        button.getStyleClass().add("chosen-answer");
        chosenAnswer = button;
    }

    /**
     * Returns all answer label controls.
     *
     * @return all answer label controls
     */
    protected List<Label> getLabels() {
        return Arrays.asList(labelOptionA, labelOptionB, labelOptionC, labelOptionD);
    }

    /**
     * Returns all answer button controls.
     *
     * @return all answer button controls
     */
    protected List<JFXButton> getButtons() {
        return Arrays.asList(buttonOptionA, buttonOptionB, buttonOptionC, buttonOptionD);
    }
}
