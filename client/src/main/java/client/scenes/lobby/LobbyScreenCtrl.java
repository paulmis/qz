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
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
     * Reacts to a player joining the lobby.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_JOINED)
    public void playerJoined() {
        updateView();
    }

    /**
     * Reacts to a player leaving the lobby.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_LEFT)
    public void playerLeft() {
        updateView();
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
                                        mainCtrl.showErrorSnackBar("Unable to quit the lobby: "
                                                + "user or lobby doesn't exist");
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
