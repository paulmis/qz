package client.scenes.chat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * Chat controller.
 */
@Generated
public class ChatCtrl implements Initializable {

    @FXML private VBox messageList;
    @FXML private TextField userMessage;

    /**
     * Constructor of a chat widget.
     */
    public ChatCtrl() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ToDo: make send button square
    }

    /**
     * Send a chat message.
     */
    @FXML
    private void sendButtonClick() {
        if (userMessage.getText().isBlank()) {
            // Avoid sending blank messages
            return;
        }
        // ToDo: connect to endpoint to send a message
    }

    /**
     * Retrieve chat messages and update message list.
     */
    public void updateMessages() {
        // ToDo: update list of messages
        messageList.getChildren().clear();
        List<ChatMessagePane> messages = new ArrayList<>();
        // ToDo: connect to endpoint to get messages
        // ToDo: convert DTOs to instances of ChatMessagePane
        messageList.getChildren().addAll(messages);
    }
}
