package client.scenes.questions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/** The class that encompasses the close
 *  question type control.
 */
public class CloseQuestionPane extends StackPane {

    private Node view;
    private CloseQuestionCtrl controller;


    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param questionText The question in string format.
     * @param answerHandler The handler for when the user changes his answer.
     */
    public CloseQuestionPane(String questionText, CloseQuestionCtrl.AnswerHandler answerHandler) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/questions/CloseQuestion.fxml"));

        fxmlLoader.setControllerFactory(param ->
                controller = new CloseQuestionCtrl(questionText, answerHandler));
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }
        getChildren().add(view);
    }
}
