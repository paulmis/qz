package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import lombok.Getter;

/**
 * Lobby controller.
 */
@Getter
public class LobbyCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    protected String name = "Ligma's Lobby";

    @FXML
    public JFXButton settingsButton;
    @FXML
    public JFXButton userButton;
    @FXML
    public JFXButton copyLinkButton;
    @FXML
    public JFXButton startButton;
    @FXML
    public JFXButton lobbySettingsButton;
    @FXML
    public JFXButton disbandButton;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Fired when the start button is clicked.
     */
    public void startButtonClick() {
        this.mainCtrl.showGameScreen();
    }
}
