package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.SSEEventHandler;
import client.utils.SSEHandler;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import javafx.fxml.FXML;
import lombok.Generated;
import lombok.Getter;


/**
 * Lobby controller.
 */
@Getter
@Generated
public class LobbyScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private SSEHandler sseHandler;

    private String name = "Ligma's Lobby";

    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton copyLinkButton;
    @FXML private JFXButton startButton;
    @FXML private JFXButton lobbySettingsButton;
    @FXML private JFXButton disbandButton;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * This function resets the lobbu screen ctrl.
     * It handles all the required set-up that needs to be done for a lobby to be displayed.
     */
    public void reset() {
        // this starts the sse connection
        sseHandler = new SSEHandler(this);
        server.subscribeToSSE(sseHandler);
    }

    /**
     * Fired when the start button is clicked.
     */
    public void startButtonClick() {
        this.server.startLobby(response -> runLater(() -> {
            switch (response.getStatus()) {
                case 403:
                    mainCtrl.showErrorSnackBar("Starting the game failed! You are not the host.");
                    break;
                case 409:
                    mainCtrl.showErrorSnackBar("Something went wrong while starting the game.");
                    break;
                case 425:
                    mainCtrl.showInformationalSnackBar("Try again after a second.");
                    break;
                case 200:
                    mainCtrl.showInformationalSnackBar("Game started!");
                    break;
                default:
                    mainCtrl.showErrorSnackBar("Something went really bad. Try restarting the app.");
                    break;
            }
        }));
    }

    /**
     * Fired when the lobby settings button is clicked.
     */
    public void lobbySettingsButtonClick() {
        var config = new NormalGameConfigurationDTO(null, 60, 1, 20, 3, 2f, 100, 0, 75);
        mainCtrl.openLobbySettings(config, (conf) -> {
            System.out.println(conf);
            mainCtrl.closeLobbySettings();
        });
    }

    @SSEEventHandler(SSEMessageType.GAME_START)
    public void startGame() {
        mainCtrl.showGameScreen(this.sseHandler);
    }
}
