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

        // We create the loader for the fxml of the question
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/questions/MultipleChoiceQuestion.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the question text and the answer handler
        fxmlLoader.setControllerFactory(param ->
                controller = new MCQuestionCtrl(question, answerHandler));

        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
    }
}
