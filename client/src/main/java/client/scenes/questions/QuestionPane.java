package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import commons.entities.AnswerDTO;
import commons.entities.questions.EstimateQuestionDTO;
import commons.entities.questions.MCQuestionDTO;
import commons.entities.questions.QuestionDTO;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;



/**
 * The class that encompasses the multiple
 * choice question type control.
 * The purpose of this class is to allow the
 * generation of the control inside code.
 */
@Slf4j
@Generated
public class QuestionPane extends StackPane {

    private Node view;
    private QuestionCtrl controller;

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

            switch (mcQuestion.getQuestionType()) {
                case GUESS_COST: {
                    loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionCost.fxml"));
                    loader.setControllerFactory(param ->
                            controller = new MCQuestionCostCtrl(mainCtrl, gameCommunication, mcQuestion));
                    break;
                }
                case GUESS_ACTIVITY:
                case INSTEAD_OF: {
                    loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/MCQuestionActivity.fxml"));
                    loader.setControllerFactory(param ->
                            controller = new MCQuestionActivityCtrl(mainCtrl, gameCommunication, mcQuestion));
                    break;
                }
                default:
                    throw new NotImplementedException(mcQuestion.getQuestionType() + " questions type not implemented");
            }
        } else if (question instanceof EstimateQuestionDTO) {
            // Estimate question
            EstimateQuestionDTO estQuestion = (EstimateQuestionDTO) question;

            loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/EstimateQuestion.fxml"));
            loader.setControllerFactory(param ->
                    controller = new EstimateQuestionCtrl(mainCtrl, gameCommunication, estQuestion));
        } else {
            throw new NotImplementedException(question.getClass().getName() + " questions type not implemented");
        }

        // Load and add the FXML scene
        try {
            view = loader.load();
        } catch (IOException e) {
            log.error("Could not load the question pane FXML file", e);
            Platform.exit();
            System.exit(0);
        }
        getChildren().add(view);
    }

    /**
     * Show the correct answer.
     *
     * @param answer the answer to show to the player.
     */
    public void showAnswer(AnswerDTO answer) {
        controller.showAnswer(answer);
    }

    /**
     * Remove the answer.
     *
     * @param answer the answer to be removed.
     */
    public void removeAnswer(AnswerDTO answer) {
        controller.removeAnswer(answer);
    }
}
