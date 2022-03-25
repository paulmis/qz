package client.scenes.lobby;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The class that encompasses the LobbyDisbandScreen.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */

@Generated
public class LobbyDisbandScreenPane extends StackPane {

    private Node view;
    private LobbyDisbandScreenCtrl controller;

    private void setUpScreen(LobbyDisbandScreenCtrl ctrl) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/LobbyDisbandScreen.fxml"));
        fxmlLoader.setControllerFactory(param ->
                controller = ctrl);
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }
        getChildren().add(view);
    }

    /**
     * This constructor sets up the lobby disband pane.
     *
     * @param disbandHandler the action that is to be performed on a disband.
     */
    public LobbyDisbandScreenPane(LobbyDisbandScreenCtrl.DisbandHandler disbandHandler) {
        setUpScreen(new LobbyDisbandScreenCtrl(disbandHandler));
    }

    public void makeTranslucent() {
        this.controller.makeTranslucent();
    }
}
