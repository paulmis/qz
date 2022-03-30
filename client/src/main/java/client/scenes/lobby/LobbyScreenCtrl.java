package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.scenes.UserInfoPane;
import client.utils.ClientState;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.messages.SSEMessageType;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Lobby controller.
 */
@Getter
@Generated
@Slf4j
public class LobbyScreenCtrl implements SSESource {
    private final LobbyCommunication communication;
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    
    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private Label gameName;
    @FXML
    private Label gameId;
    @FXML
    private Label gameType;
    @FXML
    private Label gameCapacity;
    @FXML
    private VBox playerList;
    @FXML
    private JFXButton disbandButton;
    @FXML
    private JFXButton settingsButton;
    @FXML
    private JFXButton userButton;
    @FXML
    private JFXButton copyLinkButton;
    @FXML
    private JFXButton startButton;
    @FXML
    private JFXButton lobbySettingsButton;
    @FXML
    private JFXButton leaveButton;
    
    private UserInfoPane userInfo = null;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param communication Reference to communication utilities object.
     * @param mainCtrl      Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(LobbyCommunication communication, MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
        this.server = server;
    }

    public void bindHandler(SSEHandler handler) {
        handler.initialize(this);
    }

    /**
     * Reacts to the lobby being modified.
     */
    @SSEEventHandler(SSEMessageType.LOBBY_MODIFIED)
    public void lobbyModified() {
        log.info("Lobby modified");
        // Ask for the new lobby
        communication.getLobbyInfo(
            ClientState.game.getId(),
            (game) -> runLater(() -> {
                log.info("Lobby info with {} players received", game.getPlayers().size());
                ClientState.game = game;
                updateView();
            }),
            (error) -> runLater(() -> {
                mainCtrl.showErrorSnackBar(
                    error == null
                        ? "Unable to update the lobby"
                        : error.getDescription());
            })
        );
    }

    /**
     * Reacts to the lobby being disbanded.
     */
    @SSEEventHandler(SSEMessageType.LOBBY_DELETED)
    public void lobbyDisbanded() {
        mainCtrl.showErrorSnackBar("Lobby has been disbanded");
        mainCtrl.showLobbyListScreen();
    }

    /**
     * Reacts to the game being started by another player.
     */
    @SSEEventHandler(SSEMessageType.GAME_START)
    public void gameStarted() {
        mainCtrl.showGameScreen(ClientState.game.getCurrentQuestion());
    }

    /**
     * This function resets the lobby screen ctrl.
     * It handles all the required set-up that needs to be done for a lobby to be displayed.
     */
    public void reset() {
        if (userInfo != null) {
            userInfo.setVisible(false);
        }
        updateView();
    }

    /**
     * Fired when the start button is clicked.
     */
    @FXML
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
                        mainCtrl.showErrorSnackBar("Failed to disband lobby. You are not the host");
                        break;
                    case 404:
                        mainCtrl.showErrorSnackBar("Failed to disband lobby. Information couldn't be retrieved");
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
     * Fired when the user button is pressed.
     */
    public void showUserInfo() {
        if (userInfo == null) {
            // Create userInfo
            userInfo = new UserInfoPane(server, mainCtrl);
            mainAnchor.getChildren().add(userInfo);
            userInfo.setVisible(true);
            runLater(() -> userInfo.setupPosition(userButton, mainAnchor));
        } else {
            // Toggle visibility
            userInfo.setVisible(!userInfo.isVisible());
        }
    }

    /**
     * Fired when the lobby settings button is clicked.
     */
    @FXML
    public void lobbySettingsButtonClick() {
        mainCtrl.openLobbySettings(ClientState.game.getConfiguration(), (conf) -> {
            // Close pop-up
            mainCtrl.closeLobbySettings();

            // Call endpoint to change config
            communication.saveConfig(
                    ClientState.game.getId(),
                    conf,
                    // Success
                    () -> runLater(() -> {
                        updateView();
                    }),
                    // Failure
                    () -> runLater(() -> mainCtrl.showErrorSnackBar("Failed to update the configuration.")));
        });
    }

    /**
     * Set up the screen elements according to the stored GameDTO.
     */
    public void updateView() {
        GameDTO gameDTO = ClientState.game;

        // ToDo: have a game name in gameDTO
        // Set game's name as "host's game"
        String hostNickname = gameDTO.getPlayers().stream()
                .filter(player -> player.getId().equals(gameDTO.getHost()))
                .findFirst()
                .map(GamePlayerDTO::getNickname)
                .orElse("N.A.");
        gameName.setText(hostNickname + "'s game");

        // Set game id
        gameId.setText(gameDTO.getGameId());

        // Set game class
        gameType.setText(gameDTO.getClass().getName()
                .replaceAll(".*\\.", "")
                .replaceAll("GameDTO", ""));

        // Show game capacity and current occupancy
        gameCapacity.setText(gameDTO.getPlayers().size() + "/" + gameDTO.getConfiguration().getCapacity());

        // Check if logged in player is the host
        boolean isHost = gameDTO.getPlayers().stream()
                .filter(dto -> ClientState.user.getId().equals(dto.getUserId()))
                .anyMatch(dto -> gameDTO.getHost().equals(dto.getId()));
        lobbySettingsButton.setDisable(!isHost);
        startButton.setDisable(!isHost);

        // Show list of participants
        updatePlayerList(gameDTO, isHost);
    }

    /**
     * Populates the list of players given the game information.
     *
     * @param gameDTO structure containing the game information
     */
    private void updatePlayerList(GameDTO gameDTO, boolean isHost) {
        playerList.getChildren().clear();

        List<LobbyPlayerPane> playerElements = gameDTO.getPlayers().stream()
                .sorted((p1, p2) ->
                        // Sort by join date, host always first
                        gameDTO.getHost().equals(p1.getId()) == gameDTO.getHost().equals(p2.getId())
                                ? p1.getJoinDate().compareTo(p2.getJoinDate())
                                : (gameDTO.getHost().equals(p1.getId()) ? -1 : 1))
                .map(dto -> {
                    LobbyPlayerPane elem = new LobbyPlayerPane(dto);
                    // Show kick-out buttons only to host
                    elem.showRemovePlayerBtn(isHost);
                    // Indicate if the player is the host
                    elem.setPlayerHost(gameDTO.getHost().equals(dto.getId()));
                    return elem;
                })
                .collect(Collectors.toList());

        playerList.getChildren().addAll(playerElements);
    }
}
