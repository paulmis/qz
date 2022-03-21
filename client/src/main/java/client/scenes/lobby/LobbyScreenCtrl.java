package client.scenes.lobby;

import client.scenes.MainCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Fired when the start button is clicked.
     */
    public void startButtonClick() {
        this.mainCtrl.showGameScreen();
    }

    /**
     * Fired when the disband button is clicked.
     */
    public void disbandButtonClick() {
        try {
            this.server.leaveLobby();
            this.mainCtrl.showLobbyListScreen();
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
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
