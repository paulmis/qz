package client.scenes.questions;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import javafx.application.Platform;
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
     */
    public StartGamePane(MainCtrl mainCtrl, GameCommunication gameCommunication) {
        // Assign the scene and controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/questions/StartGameElement.fxml"));
        loader.setControllerFactory(param -> new StartGameElementCtrl(mainCtrl, gameCommunication));

        // Load and add the FXML scene
        try {
            view = loader.load();
            getChildren().add(view);
        } catch (Exception e) {
            System.out.println("Error loading the FXML file");
            e.printStackTrace();
            Platform.exit();
            System.exit(0);
        }
    }
}
