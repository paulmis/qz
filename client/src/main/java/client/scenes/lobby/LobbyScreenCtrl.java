package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.SSEEventHandler;
import client.utils.SSEHandler;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.GameType;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import java.util.Set;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

    @FXML private Label gameName;
    @FXML private Label gameId;
    @FXML private Label gameType;
    @FXML private Label gameCapacity;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton copyLinkButton;
    @FXML private JFXButton startButton;
    @FXML private JFXButton lobbySettingsButton;
    @FXML private JFXButton disbandButton;
    @FXML private ListView playerList;

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
        // ToDo: get current config from gameDTO
        var config = new NormalGameConfigurationDTO(null, 60, 1, 20, 3, 2f, 100, 0, 75);
        mainCtrl.openLobbySettings(config, (conf) -> {
            System.out.println(conf);
            mainCtrl.closeLobbySettings();
            // ToDo: call endpoint to change config
            updateView();
        });
    }

    @SSEEventHandler(SSEMessageType.GAME_START)
    public void startGame() {
        mainCtrl.showGameScreen(this.sseHandler);
    }

    /**
     * Set up the screen elements according to the stored GameDTO.
     */
    public void updateView() {
        // ToDo retrieve current gameDTO from storage
        GameDTO dto = new GameDTO();
        dto.setGameId("ABCD");
        dto.setGameType(GameType.PUBLIC);
        GameConfigurationDTO confDTO = new NormalGameConfigurationDTO(null, 60, 2, 20, 3, 2f, 100, 0, 75);
        dto.setConfiguration(confDTO);
        GamePlayerDTO host = new GamePlayerDTO();
        host.setId(UUID.randomUUID());
        host.setNickname("A");
        dto.setPlayers(Set.of(host));
        dto.setHost(host.getId());

        // ToDo: use gameDTO to initialize the scene's view
        gameName.setText("prova");
        gameId.setText(dto.getGameId() == null ? "XYZ" : dto.getGameId());
        gameType.setText(dto.getGameType() == null ? "N.A." : dto.getGameType().toString());
        gameCapacity.setText(dto.getPlayers().size() + "/" + dto.getConfiguration().getCapacity());
        updatePlayerList(dto);
    }

    private void updatePlayerList(GameDTO dto) {
        // ToDo: update list of players in the lobby
    }
}
