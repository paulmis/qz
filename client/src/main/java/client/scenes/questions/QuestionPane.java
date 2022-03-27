package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.questions.MCQuestionDTO;
import commons.entities.questions.QuestionDTO;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;
import org.apache.commons.lang3.NotImplementedException;



/**
 * The class that encompasses the multiple
 * choice question type control.
 * The purpose of this class is to allow the
 * generation of the control inside code.
 */
@Generated
public class QuestionPane extends StackPane {

    private Node view;
    private MCQuestionCtrl controller;

    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param mainCtrl the main controller
     * @param gameCommunication the communication class
     * @throws IOException if the FXML file cannot be loaded
     */
    public QuestionPane(MainCtrl mainCtrl, GameCommunication gameCommunication, QuestionDTO question)
        throws IOException {
        // Assign the scene and controller
        FXMLLoader loader;
        if (question instanceof MCQuestionDTO) {
            // Multiple choice question
            MCQuestionDTO mcQuestion = (MCQuestionDTO) question;

            if (mcQuestion.isGuessConsumption()) {
                loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionCost.fxml"));
                loader.setControllerFactory(param ->
                    controller = new MCQuestionCostCtrl(mainCtrl, gameCommunication, mcQuestion));
            } else {
                loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionActivity.fxml"));
                loader.setControllerFactory(param ->
                    controller = new MCQuestionActivityCtrl(mainCtrl, gameCommunication, mcQuestion));
            }
        } else {
            throw new NotImplementedException(question.getClass().getName() + " questions type not implemented");
        }

        // Load and add the FXML scene
        view = loader.load();
        getChildren().add(view);
    }
}
