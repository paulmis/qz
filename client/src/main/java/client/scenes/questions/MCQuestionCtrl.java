package client.scenes.questions;

import client.communication.game.AnswerHandler;
import com.jfoenix.controls.JFXButton;
import commons.entities.AnswerDTO;
import commons.entities.questions.MCQuestionDTO;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * MCQuestion controller.
 */
@Generated
public abstract class MCQuestionCtrl implements Initializable {

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

    protected final MCQuestionDTO question;
    protected final AnswerHandler answerHandler;

    /**
     * Constructor for MCQuestionCtrl.
     *
     * @param question the question this controller manages
     * @param answerHandler handler for when an answer is chosen
     */
    public MCQuestionCtrl(MCQuestionDTO question, AnswerHandler answerHandler) {
        this.question = question;
        this.answerHandler = answerHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Sets the question text
        this.questionLabel.setText(question.getText());

        // Looping over they answer controls
        for (int i = 0; i < getLabels().size(); i++) {
            // Assign the answer handler to the button
            int finalI = i;
            getButtons()
                .get(i)
                .setOnAction((actionEvent) ->
                    answerHandler.handle(
                        new AnswerDTO(
                            List.of(question.getActivities().get(finalI).getCost()),
                            question.getId())));
        }
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
