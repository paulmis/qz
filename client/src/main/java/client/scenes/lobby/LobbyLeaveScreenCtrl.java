package client.scenes.lobby;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.Generated;

/**
 * The controller for the LobbyLeaveScreen.
 * It handles all the actions of the LobbyLeaveScreen.
 */
@Generated
public class LobbyLeaveScreenCtrl implements Initializable {

    /**
     * The cancel handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the cancel button.
     */
    public interface CancelHandler {
        void handle();
    }

    /**
     * The leave handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the leave button.
     */
    public interface LeaveHandler {
        void handle();
    }

    @FXML private JFXButton cancelButton;
    @FXML private JFXButton leaveButton;
    @FXML private AnchorPane rootPane;
    private LeaveHandler leaveHandler;
    private CancelHandler cancelHandler;

    /**
     * The constructor for the LobbyLeaveScreen controller.
     *
     * @param leaveHandler the action that is to be performed when the user leaves the lobby.
     * @param cancelHandler the action that is to be performed when the user cancels leaving the game.
     */
    public LobbyLeaveScreenCtrl(LeaveHandler leaveHandler, CancelHandler cancelHandler) {
        this.leaveHandler = leaveHandler;
        this.cancelHandler = cancelHandler;
    }

    /**
     * This function handles the cancel button click.
     */
    @FXML
    private void cancel() {
        cancelHandler.handle();
    }

    /**
     * This function handles the leave button click.
     */
    @FXML
    private void leave() {
        leaveHandler.handle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
