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
 * The controller for the GameStartScreen.
 * It handles all the actions of the GameStartScreen.
 */
@Generated
public class GameStartScreenCtrl implements Initializable {

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
     * The start handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the start button.
     */
    public interface StartHandler {
        void handle();
    }

    @FXML private JFXButton cancelStartGameButton;
    @FXML private JFXButton startGameButton;
    @FXML private AnchorPane rootPane;
    private StartHandler startHandler;
    private CancelHandler cancelHandler;

    /**
     * The constructor for the GameStartScreen controller.
     *
     * @param startHandler the action that is to be performed when the user starts the game.
     * @param cancelHandler the action that is to be performed when the user cancels starting the game.
     */
    public GameStartScreenCtrl(StartHandler startHandler, CancelHandler cancelHandler) {
        this.startHandler = startHandler;
        this.cancelHandler = cancelHandler;
    }

    /**
     * This function handles the cancel button click.
     */
    @FXML
    private void cancelStartGame() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        cancelHandler.handle();
    }

    /**
     * This function handles the start button click.
     */
    @FXML
    private void startGame() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        startHandler.handle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
