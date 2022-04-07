package client.scenes.lobby;

import client.utils.SoundEffect;
import client.utils.SoundManager;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.Generated;

/**
 * The controller for the LobbyDisbandScreen.
 * It handles all the actions of the LobbyDisbandScreen.
 */
@Generated
public class LobbyDisbandScreenCtrl implements Initializable {

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
     * The disband handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the disband button.
     */
    public interface DisbandHandler {
        void handle();
    }

    @FXML private JFXButton cancelDisbandLobbyButton;
    @FXML private JFXButton disbandLobbyButton;
    @FXML private AnchorPane rootPane;
    private CancelHandler cancelHandler;
    private DisbandHandler disbandHandler;

    /**
     * The constructor for the LobbyDisbandScreen controller.
     *
     * @param disbandHandler the action that is to be performed when the host disbands the lobby.
     * @param cancelHandler the action that is to be performed when the host cancels disbanding the lobby.
     */
    public LobbyDisbandScreenCtrl(DisbandHandler disbandHandler, CancelHandler cancelHandler) {
        this.disbandHandler = disbandHandler;
        this.cancelHandler = cancelHandler;
    }

    /**
     * This function handles the cancel button click.
     */
    @FXML
    private void cancelDisbandLobby() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        cancelHandler.handle();
    }

    /**
     * This function handles the disband button click.
     */
    @FXML
    private void disbandLobby() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        disbandHandler.handle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
