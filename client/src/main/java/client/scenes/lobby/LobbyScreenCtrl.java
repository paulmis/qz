package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import java.time.Duration;
import javafx.fxml.FXML;
import lombok.Generated;
import lombok.Getter;

/**
 * Lobby controller.
 */
@Getter
@Generated
public class LobbyScreenCtrl implements SSESource {
    private final LobbyCommunication communication;
    private final MainCtrl mainCtrl;

    private String name = "Ligma's Lobby";

    @FXML private JFXButton disbandButton;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton copyLinkButton;
    @FXML private JFXButton startButton;
    @FXML private JFXButton lobbySettingsButton;
    @FXML private JFXButton leaveButton;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param communication Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(LobbyCommunication communication, MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
    }

    public void bindHandler(SSEHandler handler) {
        handler.initialize(this);
    }

    @SSEEventHandler(SSEMessageType.GAME_START)
    public void startGame() {
        mainCtrl.showGameScreen(null);
    }




    /**
     * Example of a sse event handler.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_LEFT)
    public void playerLeft(String playerId) {

    }

    /**
     * Fired when the start button is clicked.
     */
    public void startButtonClick() {
        LobbyCommunication.startGame(
            ClientState.game.getId(),
            // Success
            (response) -> runLater(() -> {
                switch (response.getStatus()) {
                    case 403:
                        mainCtrl.showErrorSnackBar("Starting the game failed! You are not the host.");
                        break;
                    case 409:
                        mainCtrl.showErrorSnackBar("Something went wrong while starting the game.");
                        break;
                    case 425:
                        mainCtrl.showErrorSnackBar("Try again after a second.");
                        break;
                    case 200:
                        mainCtrl.showInformationalSnackBar("Game started!");
                        break;
                    default:
                        mainCtrl.showErrorSnackBar("Something went really bad. Try restarting the app.");
                        break;
                }
            }),
            // Failure
            () -> runLater(() -> mainCtrl.showErrorSnackBar("Failed to start game.")));
    }
    
    /**
     * Handles the click of the quit button.
     * This is handled by the server function call.
     */
    @FXML
    private void leaveButtonClick() {
        // Open the warning and wait for user action
        mainCtrl.openLobbyLeaveWarning(
            // If confirmed, exit the lobby
            () -> {
                mainCtrl.closeLobbyLeaveWarning();
                this.communication.leaveLobby(response -> runLater(() -> {
                        switch (response.getStatus()) {
                            case 404:
                                mainCtrl.showErrorSnackBar("Unable to leave the lobby.");
                                break;
                            case 409:
                                mainCtrl.showErrorSnackBar("Something went wrong while leaving the lobby");
                                break;
                            case 200:
                                mainCtrl.showInformationalSnackBar("Successfully left the lobby");
                                mainCtrl.showLobbyListScreen();
                                ClientState.game = null;
                                ServerUtils.sseHandler.kill();
                                break;
                            default:
                                mainCtrl.showErrorSnackBar("Unable to leave the lobby");
                        }
                    }), () -> runLater(() -> mainCtrl.showErrorSnackBar("Unable to leave the lobby")));
            },
            // Otherwise, simply close the warning
            mainCtrl::closeLobbyLeaveWarning
        );
    }

    /**
     * Fired when the disband button is clicked.
     */
    public void disbandButtonClick() {
        mainCtrl.openLobbyDisbandWarning(() -> {
            mainCtrl.closeLobbyDisbandWarning();
            this.communication.disbandLobby(response -> runLater(() -> {
                switch (response.getStatus()) {
                    case 401:
                        mainCtrl.showErrorSnackBar("Failed to disband lobby. " +
                                "You are not the host");
                        break;
                    case 404:
                        mainCtrl.showErrorSnackBar("Failed to disband lobby. " +
                                "Information couldn't be retrieved");
                        break;
                    case 200:
                        mainCtrl.showInformationalSnackBar("Successfully disbanded the lobby");
                        mainCtrl.showLobbyListScreen();
                        ClientState.game = null;
                        ServerUtils.sseHandler.kill();
                        break;
                    default:
                        mainCtrl.showErrorSnackBar("Failed to disband lobby");
                        break;
                }
            }), () -> runLater(() -> mainCtrl.showErrorSnackBar("Failed to disband lobby")));
        }, () -> runLater(mainCtrl::closeLobbyDisbandWarning));
    }

    /**
     * Fired when the lobby settings button is clicked.
     */
    public void lobbySettingsButtonClick() {
        var config = new NormalGameConfigurationDTO(null, Duration.ofMinutes(1), 1, 20, 3, 2f, 100, 0, 75);
        mainCtrl.openLobbySettings(config, (conf) -> {
            System.out.println(conf);
            mainCtrl.closeLobbySettings();
        });
    }
}
