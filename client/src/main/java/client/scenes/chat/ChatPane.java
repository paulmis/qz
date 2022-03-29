package client.scenes.chat;

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
     */
    public ChatPane() {
        // Create the loader for the fxml
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/chat/ChatWidget.fxml"));

        // Set the controller of the fxml
        fxmlLoader.setControllerFactory(param ->
                controller = new ChatCtrl());

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

    /**
     * Expose the updateMessages method from the controller.
     */
    public void updateMessages() {
        controller.updateMessages();
    }
}
