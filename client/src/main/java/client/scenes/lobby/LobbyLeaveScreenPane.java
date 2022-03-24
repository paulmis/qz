package client.scenes.lobby;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The class that encompasses the LobbyLeaveScreen.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */

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
     * @param leaveHandler the action that is to be performed on a leave.
     */
    public LobbyLeaveScreenPane(LobbyLeaveScreenCtrl.LeaveHandler leaveHandler) {
        setUpScreen(new LobbyLeaveScreenCtrl(leaveHandler));
    }

    public void makeTranslucent() {
        this.controller.makeTranslucent();
    }
}
