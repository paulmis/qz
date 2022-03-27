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
import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param communication Reference to communication utilities object.
     * @param mainCtrl      Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(LobbyCommunication communication, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
    }

    public void bindHandler(SSEHandler handler) {
        handler.initialize(this);
    }

    /**
     * Example of an SSE event handler.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_LEFT)
    public void playerLeft(String playerId) {
    }

    /**
     * Starts a game remotely activated.
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
     * Fired when the lobby settings button is clicked.
     */
    public void lobbySettingsButtonClick() {
        mainCtrl.openLobbySettings(ClientState.game.getConfiguration(), (conf) -> {
            System.out.println(conf);
            mainCtrl.closeLobbySettings();
            // ToDo: call endpoint to change config
            updateView();
        });
    }

    /**
     * Set up the screen elements according to the stored GameDTO.
     */
    public void updateView() {
        /* // For testing
        GameDTO dto = new NormalGameDTO();
        dto.setGameId("ABCD");
        GameConfigurationDTO confDTO = new NormalGameConfigurationDTO(
                null,
                Duration.ofSeconds(60),
                2,
                20,
                3,
                2f,
                100,
                0,
                75);
        dto.setConfiguration(confDTO);
        GamePlayerDTO sally = new GamePlayerDTO();
        sally.setId(UUID.randomUUID());
        sally.setNickname("Sally");
        sally.setScore(12);
        sally.setUserId(ClientState.user.getId());
        sally.setJoinDate(LocalDateTime.now());
        Set<GamePlayerDTO> players = new HashSet<>();
        players.add(sally);
        for (int idx = 0; idx < 40; idx++) {
            GamePlayerDTO john = new GamePlayerDTO();
            john.setId(UUID.randomUUID());
            john.setNickname("John" + idx);
            john.setScore(30 + idx);
            john.setUserId(UUID.randomUUID());
            john.setJoinDate(LocalDateTime.now().minusMinutes(idx));
            players.add(john);
        }
        dto.setPlayers(players);
        dto.setHost(sally.getId());
        */
        GameDTO dto = ClientState.game;

        // ToDo: have a game name in gameDTO
        String hostNickname = dto.getPlayers().stream()
                .filter(player -> player.getId() == dto.getHost())
                .findFirst()
                .map(GamePlayerDTO::getNickname)
                .orElse("Ligma");
        gameName.setText(hostNickname + "'s game");
        gameId.setText(dto.getGameId());
        gameType.setText(dto.getClass().getName()
                .replaceAll(".*\\.", "")
                .replaceAll("GameDTO", ""));
        gameCapacity.setText(dto.getPlayers().size() + "/" + dto.getConfiguration().getCapacity());
        updatePlayerList(dto);
    }

    /**
     * Populates the list of players given the game information.
     *
     * @param gameDTO structure containing the game information
     */
    private void updatePlayerList(GameDTO gameDTO) {
        playerList.getChildren().clear();
        int highestScore = gameDTO.getPlayers().stream()
                .mapToInt(GamePlayerDTO::getScore)
                .max()
                .orElse(Integer.MIN_VALUE);
        boolean isHost = gameDTO.getPlayers().stream()
                .filter(dto -> ClientState.user.getId().equals(dto.getUserId()))
                .anyMatch(dto -> gameDTO.getHost().equals(dto.getId()));
        List<LobbyPlayerPane> playerElements = gameDTO.getPlayers().stream()
                .sorted((p1, p2) ->
                        // Sort by join date, host always first
                        gameDTO.getHost().equals(p1.getId()) == gameDTO.getHost().equals(p2.getId())
                                ? p1.getJoinDate().compareTo(p2.getJoinDate())
                                : (gameDTO.getHost().equals(p1.getId()) ? -1 : 1))
                .map(dto -> {
                    LobbyPlayerPane elem = new LobbyPlayerPane(dto);
                    // Show highest-scoring player
                    elem.setPlayerLeading(dto.getScore() == highestScore);
                    // Show kick-out buttons only to host
                    elem.showRemovePlayerBtn(isHost);
                    // Indicate if the player is the host
                    elem.setPlayerHost(gameDTO.getHost().equals(dto.getId()));
                    return elem;
                })
                .collect(Collectors.toList());
        playerList.getChildren().addAll(playerElements);

        // Set player list spacing
        playerList.setSpacing(5);
    }
}
