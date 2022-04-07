package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.LobbyCommunication;
import client.communication.user.UserCommunication;
import client.scenes.MainCtrl;
import client.scenes.UserInfoPane;
import client.utils.ClientState;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import commons.entities.game.GameDTO;
import commons.entities.messages.SSEMessageType;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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
public class LobbyScreenCtrl implements SSESource, Initializable {
    private final LobbyCommunication communication;
    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    
    @FXML private AnchorPane lobbyMainAnchor;
    @FXML private Label gameName;
    @FXML private Label labelGameId;
    @FXML private Label gameType;
    @FXML private Label gameCapacity;
    @FXML private VBox playerList;
    @FXML private JFXButton disbandButton;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton copyLinkButton;
    @FXML private JFXButton startButton;
    @FXML private JFXButton lobbySettingsButton;
    @FXML private JFXButton leaveButton;
    @FXML private FontAwesomeIconView lockButtonIconView;
    @FXML private AnchorPane settingsPanel;
    @FXML private JFXButton volumeButton;
    @FXML private JFXSlider volumeSlider;
    @FXML private JFXToggleButton muteEveryoneToggleButton;
    @FXML private FontAwesomeIconView volumeIconView;

    private List<FontAwesomeIcon> volumeIconList;
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
     *
     * @param preparationDuration Duration of preparation phase
     */
    @SSEEventHandler(SSEMessageType.GAME_START)
    public void gameStarted(Integer preparationDuration) {
        SoundManager.playMusic(SoundEffect.GAME_START, getClass());
        mainCtrl.showGameScreen(ClientState.game.getCurrentQuestion());
        mainCtrl.getGameScreenCtrl().startTimer(Duration.ofMillis(preparationDuration));
        ClientState.previousScore = Optional.of(0);
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
        setUpVolume();
    }

    @FXML
    private void leaderboardButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        mainCtrl.showGlobalLeaderboardScreen();
    }

    @FXML
    private void settingsButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (userInfo != null) {
            userInfo.setVisible(false);
        }
        settingsPanel.setVisible(!settingsPanel.isVisible());
    }

    @FXML
    private void volumeButtonClick(ActionEvent actionEvent) {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        SoundManager.volume.setValue(SoundManager.volume.getValue() == 0 ? 100 : 0);
    }

    private void setUpVolume() {

        // A list of icons so we can have a swift transition
        // between them when changing the volume
        volumeIconList = Arrays.asList(
                FontAwesomeIcon.VOLUME_OFF,
                FontAwesomeIcon.VOLUME_DOWN,
                FontAwesomeIcon.VOLUME_UP);

        // Bidirectional binding of the volume with the volume
        // property. This is to ensure we can report changes
        // instantly to the ui if the volume changes from
        // outside of our control.
        volumeSlider.valueProperty().bindBidirectional(SoundManager.volume);

        // a listener on the volume to change the icon
        // of the volume.
        SoundManager.volume.addListener((observable, oldValue, newValue) -> {

            // Sets the glyph name of the iconView directly
            volumeIconView.setGlyphName(volumeIconList.get(
                    Math.round(newValue.floatValue() / 100 * (volumeIconList.size() - 1))
            ).name());
        });
        SoundManager.everyoneMuted.bindBidirectional(muteEveryoneToggleButton.selectedProperty());
    }

    /**
     * Fired when the start button is clicked.
     */
    @FXML
    public void startButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (ClientState.game.getConfiguration().getCapacity() > ClientState.game.getPlayers().size()) {
            mainCtrl.showErrorSnackBar("You need to have "
                    + ClientState.game.getConfiguration().getCapacity()
                    + " players to start the game.");
            return;
        } else if (ClientState.game.getConfiguration().getCapacity() < ClientState.game.getPlayers().size()) {
            mainCtrl.showErrorSnackBar("The lobby exceeds the capacity. Kick some people out!");
            return;
        }

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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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
        settingsPanel.setVisible(false);
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (userInfo == null) {
            // Create userInfo
            userInfo = new UserInfoPane(server, new UserCommunication(), mainCtrl);
            lobbyMainAnchor.getChildren().add(userInfo);
            userInfo.setVisible(true);
            runLater(() -> userInfo.setupPosition(userButton, lobbyMainAnchor));
        } else {
            // Toggle visibility
            userInfo.setVisibility(!userInfo.isVisible());
        }
    }

    /**
     * Fired when the lobby settings button is clicked.
     */
    @FXML
    public void lobbySettingsButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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

        gameName.setText(gameDTO.getGameName());

        // Set game id
        labelGameId.setText(gameDTO.getGameId());

        // Set if lobby is locked or not.
        lockButtonIconView.setGlyphName((gameDTO.getIsPrivate() ? "LOCK" : "UNLOCK"));

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
        disbandButton.setVisible(isHost);

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
                    LobbyPlayerPane elem = new LobbyPlayerPane(dto, () -> communication.kickPlayer(dto.getUserId(),
                            () -> runLater(() -> mainCtrl.showInformationalSnackBar("Kicked user!")),
                            error -> runLater(() ->
                                    mainCtrl.showErrorSnackBar("Failed to kick user: " + error.getDescription()))));

                    // Show kick-out buttons only to host
                    elem.showRemovePlayerBtn(isHost && (!dto.getUserId().equals(ClientState.user.getId())));
                    // Indicate if the player is the host
                    elem.setPlayerHost(gameDTO.getHost().equals(dto.getId()));
                    return elem;
                })
                .collect(Collectors.toList());

        playerList.getChildren().addAll(playerElements);
    }

    @FXML
    private void copyLinkButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(labelGameId.getText());
        clipboard.setContent(content);
        mainCtrl.showInformationalSnackBar("Copied!");
    }

    /**
     * Event handler for when you get kicked out of a lobby.
     */
    @SSEEventHandler(SSEMessageType.YOU_HAVE_BEEN_KICKED)
    public void kicked() {
        ClientState.game = null;
        ServerUtils.sseHandler.kill();
        mainCtrl.showLobbyListScreen();
        mainCtrl.showErrorSnackBar("You have been kicked from the lobby!");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpVolume();
    }
}
