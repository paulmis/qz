package client.scenes.questions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/** The class that encompasses the estimate
 *  question type control.
 */
public class EstimateQuestionPane extends StackPane {

    private Node view;
    private EstimateQuestionCtrl controller;


    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param questionText The question in string format.
     * @param answerHandler The handler for when the user changes his answer.
     */
    public EstimateQuestionPane(String questionText, EstimateQuestionCtrl.AnswerHandler answerHandler) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/questions/EstimateQuestion.fxml"));

        fxmlLoader.setControllerFactory(param ->
                controller = new EstimateQuestionCtrl(questionText, answerHandler));
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }
        getChildren().add(view);
    }
}
