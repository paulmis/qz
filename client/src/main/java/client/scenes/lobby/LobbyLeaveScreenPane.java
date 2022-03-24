package client.scenes.lobby;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

@Generated
public class LobbyLeaveScreenPane extends StackPane {

    private Node view;
    private LobbyLeaveScreenCtrl controller;

    private void setUpScreen(LobbyLeaveScreenCtrl ctrl) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/LobbyLeaveScreen.fxml"));
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
     * This constructor sets up the lobby leave pane.
     *
     * @param disbandHandler the action that is to be performed on a disband.
     */
    public LobbyLeaveScreenPane(LobbyLeaveScreenCtrl.DisbandHandler disbandHandler) {
        setUpScreen(new LobbyLeaveScreenCtrl(disbandHandler));
    }

    public void makeTranslucent() {
        this.controller.makeTranslucent();
    }
}
