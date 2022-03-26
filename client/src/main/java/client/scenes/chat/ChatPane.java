package client.scenes.chat;

import client.utils.SSEHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * Widget representation of the chat.
 */
public class ChatPane extends StackPane {

    private Node view;
    private ChatCtrl controller;

    /**
     * Constructor of a chat widget.
     *
     * @param handler SSE handler in use by the current game/lobby
     */
    public ChatPane(SSEHandler handler) {
        // Create the loader for the fxml
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/chat/ChatWidget.fxml"));

        // Set the controller of the fxml
        fxmlLoader.setControllerFactory(param ->
                controller = new ChatCtrl(handler));

        // Load the fxml
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }

        // Add fxml to the view of this control
        getChildren().add(view);

        // Fit size to view
        AnchorPane top = (AnchorPane) view;
        this.prefWidthProperty().bind(top.prefWidthProperty());
        this.prefHeightProperty().bind(top.prefHeightProperty());
        this.minWidthProperty().bind(top.minWidthProperty());
        this.minHeightProperty().bind(top.minHeightProperty());
        this.maxWidthProperty().bind(top.maxWidthProperty());
        this.maxHeightProperty().bind(top.maxHeightProperty());
    }
}
