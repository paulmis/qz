package client.scenes.questions;

import client.communication.game.AnswerHandler;
import commons.entities.questions.MCQuestionDTO;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The class that encompasses the multiple
 * choice question type control.
 * The purpose of this class is to allow the
 * generation of the control inside code.
 */
@Generated
public class MCQuestionPane extends StackPane {

    private Node view;
    private MCQuestionCtrl controller;

    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param question the question to be displayed
     */
    public MCQuestionPane(MCQuestionDTO question, AnswerHandler answerHandler) {
        // Assign the scene and controller
        FXMLLoader loader;
        if (question.isGuessConsumption()) {
            loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionCost.fxml"));
            loader.setControllerFactory(param ->
                controller = new MCQuestionCostCtrl(question, answerHandler));
        } else {
            loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionActivity.fxml"));
            loader.setControllerFactory(param ->
                controller = new MCQuestionActivityCtrl(question, answerHandler));
        }

        // Load and add the FXML scene
        try {
            view = (Node) loader.load();
            getChildren().add(view);
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }
    }
}
