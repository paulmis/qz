package client.scenes.questions;

import java.net.URL;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * The class that encompasses the multiple
 * choice question type control.
 * The purpose of this class is to allow the
 * generation of the control inside code.
 */
public class MultipleChoiceQuestionPane extends StackPane {

    private Node view;
    private MultipleChoiceQuestionCtrl controller;


    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param questionText The question in string format.
     * @param answersImages A list of url images of the answers.
     * @param answersText A list of texts of the answers.
     * @param actions A list of actions that each option should perform.
     */
    public MultipleChoiceQuestionPane(String questionText,
                                      List<String> answersText,
                                      List<URL> answersImages,
                                      List<MultipleChoiceQuestionCtrl.AnswerHandler> actions) {

        // We create the loader for the fxml of the question
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/questions/MultipleChoiceQuestion.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the question text and the answer handler
        fxmlLoader.setControllerFactory(param ->
                controller = new MultipleChoiceQuestionCtrl(questionText, answersImages, answersText, actions));

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
