package client.scenes.lobby;

import commons.entities.game.GamePlayerDTO;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;

/**
 * The player item pane.
 * It is used as a wrapper for a lobby player item.
 */
@Generated
@EqualsAndHashCode(callSuper = true)
@Data
public class LobbyPlayerPane extends StackPane {

    private Node view;
    private LobbyPlayerCtrl controller;

    /**
     * The constructor of the class. Initializes the view, adds it to the stack pane and creates a controller.
     */
    public LobbyPlayerPane(GamePlayerDTO playerDTO) {

        // We create the loader for the fxml of the lobby list item
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/Player.fxml"));

        // We set the controller of the fxml to our newly created controller and add the two required arguments.
        fxmlLoader.setControllerFactory(param ->
                controller = new LobbyPlayerCtrl(playerDTO));

        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);

        var top = (AnchorPane) view;
        this.prefWidthProperty().bind(top.prefWidthProperty());
        this.prefHeightProperty().bind(top.prefHeightProperty());
        this.minWidthProperty().bind(top.minWidthProperty());
        this.minHeightProperty().bind(top.minHeightProperty());
        this.maxWidthProperty().bind(top.maxWidthProperty());
        this.maxHeightProperty().bind(top.maxHeightProperty());
    }

    public void setPlayerLeading(boolean isLeading) {
        controller.setPlayerLeading(isLeading);
    }

    public void setPlayerHost(boolean isHost) {
        controller.setPlayerHost(isHost);
    }

    public void showRemovePlayerBtn(boolean doShow) {
        controller.showRemovePlayerBtn(doShow);
    }
}
