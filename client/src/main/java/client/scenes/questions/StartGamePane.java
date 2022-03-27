package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

@Generated
public class StartGamePane extends StackPane {
    private Node view;
    private StartGameElementCtrl controller;

    /** Creates the Node view of the control
     * and adds it to the children of the StackPane
     * with the given parameters.
     *
     * @param mainCtrl the main controller
     * @param gameCommunication the communication class
     * @throws IOException if the FXML file could not be loaded
     */
    public StartGamePane(MainCtrl mainCtrl, GameCommunication gameCommunication)
        throws IOException {
        // Assign the scene and controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/StartGameElement.fxml"));
        loader.setControllerFactory(param -> new StartGameElementCtrl(mainCtrl, gameCommunication));

        // Load and add the FXML scene
        view = loader.load();
        getChildren().add(view);
    }
}
