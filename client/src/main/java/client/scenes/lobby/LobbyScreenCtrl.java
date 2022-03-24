package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.GameCommunication;
import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import java.time.Duration;
import javafx.fxml.FXML;
import javax.ws.rs.core.Response;
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
    public LobbyScreenCtrl(LobbyCommunication communication, MainCtrl mainCtrl) {
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
                this.communication.leaveLobby(
                    (response) -> runLater(() -> {
                        switch (response.getStatus()) {
                            case 200:
                                System.out.println("User successfully removed from lobby");
                                mainCtrl.showLobbyListScreen();
                                ClientState.game = null;
                                ServerUtils.sseHandler.kill();
                                break;
                            case 404:
                                mainCtrl.showErrorSnackBar("Unable to quit the lobby: user or lobby doesn't exist");
                                break;
                            case 409:
                                mainCtrl.showErrorSnackBar("Unable to quit the lobby: "
                                    + "there was a conflict while removing the player");
                                break;
                            default:
                                mainCtrl.showErrorSnackBar("Unable to quit the lobby: server error");
                        }
                    }));
            },
            // Otherwise, simply close the warning
            mainCtrl::closeLobbyLeaveWarning
        );
    }

    /**
     * Fired when the disband button is clicked.
     */
    public void disbandButtonClick() {
        mainCtrl.openLobbyLeaveWarning(() -> {
            mainCtrl.closeLobbyLeaveWarning();
            this.server.leaveLobby(new ServerUtils.LeaveGameHandler() {
                @Override
                public void handle(Response response) {
                    javafx.application.Platform.runLater(() -> {
                        switch (response.getStatus()) {
                            case 200:
                                System.out.println("User successfully removed from lobby");
                                mainCtrl.showLobbyListScreen();
                                break;
                            case 404:
                                System.out.println("User/Game not found");
                                break;
                            case 409:
                                System.out.println("Couldn't remove player");
                                break;
                            default:
                                break;
                        }
                    });
                }
            });
        });
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
