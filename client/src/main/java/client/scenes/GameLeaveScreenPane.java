package client.scenes;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The class that encompasses the GameLeaveScreen.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */

@Generated
public class GameLeaveScreenPane extends StackPane {

    private Node view;
    private GameLeaveScreenCtrl controller;

    private void setUpScreen(GameLeaveScreenCtrl ctrl) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/GameLeaveScreen.fxml"));
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
     * This constructor sets up the game leave pane.
     *
     * @param leaveHandler the action that is to be performed on a leave.
     * @param cancelHandler the action that is to be performed on a cancel.
     */
    public GameLeaveScreenPane(GameLeaveScreenCtrl.LeaveHandler leaveHandler,
                               GameLeaveScreenCtrl.CancelHandler cancelHandler) {
        setUpScreen(new GameLeaveScreenCtrl(leaveHandler, cancelHandler));
    }
}
