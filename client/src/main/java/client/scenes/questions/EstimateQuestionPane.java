package client.scenes.questions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * The class that encompasses the estimate
 * question type control.
 * The purpose of this class is to allow the
 * generation of the control inside code.
 */
public class EstimateQuestionPane extends StackPane {

    private Node view;
    private EstimateQuestionCtrl controller;


    /**
     * Creates the Node view of the Estimate Question control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param questionText The question in string format.
     * @param answerHandler The handler for when the user changes his answer.
     */
    public EstimateQuestionPane(String questionText, EstimateQuestionCtrl.AnswerHandler answerHandler) {

        // We create the loader for the fxml of the question
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/questions/EstimateQuestion.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the question text and the answer handler
        fxmlLoader.setControllerFactory(param ->
                controller = new EstimateQuestionCtrl(questionText, answerHandler));

        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
    }
}
