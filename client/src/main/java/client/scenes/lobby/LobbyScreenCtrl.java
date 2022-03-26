package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.SSEEventHandler;
import client.utils.SSEHandler;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
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
public class LobbyScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private SSEHandler sseHandler;

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
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }




    /**
     * This function resets the lobby screen ctrl.
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
     * Fired when the leave button is clicked.
     */
    public void leaveButtonClick() {
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
        }, () -> {
            mainCtrl.closeLobbyLeaveWarning();
        });
    }

    /**
     * Fired when the disband button is clicked.
     */
    public void disbandButtonClick() {
        mainCtrl.openLobbyDisbandWarning(() -> {
            mainCtrl.closeLobbyDisbandWarning();
            this.server.disbandLobby(new ServerUtils.DisbandLobbyHandler() {
                @Override
                public void handle(Response response) {
                    javafx.application.Platform.runLater(() -> {
                        switch (response.getStatus()) {
                            case 200:
                                System.out.println("Host successfully disbanded the lobby");
                                mainCtrl.showLobbyListScreen();
                                break;
                            case 401:
                                System.out.println("Player isn't host");
                                mainCtrl.showErrorSnackBar("Failed to disband");
                                break;
                            case 404:
                                System.out.println("User/Game not found");
                                mainCtrl.showErrorSnackBar("Failed to disband");
                                break;
                            default:
                                mainCtrl.showErrorSnackBar("Failed to disband");
                                break;
                        }
                    });
                }
            });
        }, () -> {
            mainCtrl.closeLobbyDisbandWarning();
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

    @SSEEventHandler(SSEMessageType.GAME_START)
    public void startGame() {
        mainCtrl.showGameScreen(this.sseHandler);
    }
}
