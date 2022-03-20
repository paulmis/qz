package client.scenes.lobby;

import commons.entities.game.GameDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The lobby list item pane.
 * It is used as a wrapper for a lobby list item.
 */
@Generated
public class LobbyListItemPane extends StackPane {

    private Node view;
    private LobbyListItemCtrl controller;

    /**
     * The constructor of the class. Initializes the view and adds it to the
     * stackpane and it creates a controller.
     */
    public LobbyListItemPane(GameDTO gameDTO, LobbyListItemCtrl.JoinHandler joinHandler) {

        // We create the loader for the fxml of the lobby list item
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/LobbyListItem.fxml"));

        // We set the controller of the fxml to our newly created controller and add the two required arguments.
        fxmlLoader.setControllerFactory(param ->
                controller = new LobbyListItemCtrl(gameDTO, joinHandler));

        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
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
}
