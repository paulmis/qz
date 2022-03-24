package client.scenes;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

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
     * @param disbandHandler the action that is to be performed on a disband.
     */
    public GameLeaveScreenPane(GameLeaveScreenCtrl.DisbandHandler disbandHandler) {
        setUpScreen(new GameLeaveScreenCtrl(disbandHandler));
    }

    public void makeTranslucent() {
        this.controller.makeTranslucent();
    }
}
