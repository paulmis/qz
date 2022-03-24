package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.utils.ClientState;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import javafx.fxml.FXML;
import lombok.Generated;
import lombok.Getter;

/**
 * Lobby controller.
 */
@Getter
@Generated
public class LobbyScreenCtrl {
    private final LobbyCommunication communication;
    private final MainCtrl mainCtrl;

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
     * @param communication Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(LobbyCommunication communication, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
    }

    /**
     * Fired when the start button is clicked.
     */
    public void startButtonClick() {
        LobbyCommunication.startGame(
            ClientState.game.getId(),
            // Success
            () -> runLater(() -> {
                ClientState.game.setStatus(GameStatus.ONGOING);
                this.mainCtrl.showGameScreen();
            }),
            // Failure
            () -> runLater(() -> mainCtrl.showErrorSnackBar("Failed to start game.")));
    }

    /**
     * Fired when the lobby settings button is clicked.
     */
    public void lobbySettingsButtonClick() {
        var config = new SurvivalGameConfigurationDTO();
        config.setSpeedModifier(1.5f);
        config.setAnswerTime(15);
        mainCtrl.openLobbySettings(config, (conf) -> {
            System.out.println(conf);
            mainCtrl.closeLobbySettings();
        });
    }
}
