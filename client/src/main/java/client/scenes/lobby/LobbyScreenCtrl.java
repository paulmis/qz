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
import java.util.UUID;
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
    private UUID hostId = null;
    private UUID userId = null;

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

    /** This function checks if the player is the host of the lobby.
     *
     */
    public void checkHost() {
        this.server.getMyInfo(userDTO -> runLater(() -> {
                //Fetching user data success
                this.server.getLobbyInfo(gameDTO -> runLater(() -> {
                    //Fetching lobby data success
                    if (userDTO.getId() == gameDTO.getHost()) {
                        System.out.println("Player is host");
                        disbandButton.setVisible(true);
                    } else {
                        System.out.println("Player is not host");
                        disbandButton.setVisible(false);
                    }
                }), () -> runLater(() -> {
                    //Fetching lobby data failed
                    disbandButton.setVisible(false);
                    mainCtrl.showErrorSnackBar("Something went wrong while fetching lobby information");
                }));
            }), () -> runLater(() -> {
                //Fetching user data failed
                disbandButton.setVisible(false);
                mainCtrl.showErrorSnackBar("Something went wrong while fetching user information");
            }));
    }


    /**
     * This function resets the lobby screen ctrl.
     * It handles all the required set-up that needs to be done for a lobby to be displayed.
     */
    public void reset() {
        // this starts the sse connection
        sseHandler = new SSEHandler(this);
        server.subscribeToSSE(sseHandler);
        checkHost();
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
                                break;
                            case 404:
                                System.out.println("User/Game not found");
                                break;
                            default:
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
