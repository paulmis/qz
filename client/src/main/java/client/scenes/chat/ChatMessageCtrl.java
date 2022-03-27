package client.scenes.chat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import lombok.Generated;

/**
 * Chat message controller.
 */
@Generated
public class ChatMessageCtrl implements Initializable {

    @FXML private Label messageUser;
    @FXML private Label messageDate;
    @FXML private TextArea messageContent;

    /**
     * Constructor for a single message item.
     */
    public ChatMessageCtrl() {
        // ToDo: this should require a DTO to be initialized
        messageUser.setText("aUser:");
        messageDate.setText("Jan 01 20:42");
        messageContent.setText("aMessage.");
        // ToDO: set messageUser color according to GamePlayer's UUID
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ToDo
    }
}
