package client.scenes.chat;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * Widget representation of a chat message.
 */
@Generated
public class ChatMessagePane extends StackPane {

    private Node view;
    private ChatMessageCtrl controller;

    /**
     * Constructor of a chat message item.
     */
    public ChatMessagePane() {
        // Create the loader for the fxml
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/chat/ChatMessage.fxml"));

        // Set the controller of the fxml
        fxmlLoader.setControllerFactory(param ->
                controller = new ChatMessageCtrl());

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
