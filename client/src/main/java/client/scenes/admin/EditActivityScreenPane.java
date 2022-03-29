package client.scenes.admin;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.scenes.lobby.LobbyListItemCtrl;
import client.scenes.questions.MCQuestionActivityCtrl;
import client.scenes.questions.MCQuestionCostCtrl;
import client.scenes.questions.MCQuestionCtrl;
import commons.entities.ActivityDTO;
import commons.entities.questions.MCQuestionDTO;
import commons.entities.questions.QuestionDTO;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;
import org.apache.commons.lang3.NotImplementedException;

import java.io.IOException;

public class EditActivityScreenPane extends StackPane {

    private Node view;
    private EditActivityScreenCtrl controller;

    public EditActivityScreenPane(ActivityView activity, EditActivityScreenCtrl.SaveHandler saveHandler) {

        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/admin/EditActivityScreen.fxml"));

        fxmlLoader.setControllerFactory(param ->
                controller = new EditActivityScreenCtrl(activity, saveHandler));

        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getChildren().add(view);
    }
}
