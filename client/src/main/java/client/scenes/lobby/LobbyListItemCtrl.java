package client.scenes.lobby;

import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.Generated;

/**
 * The lobby list item controller.
 * Controls a lobby list item.
 */
@Generated
public class LobbyListItemCtrl implements Initializable {
    @FXML private AnchorPane lobbyInfoPane;
    @FXML private JFXButton showLobbyInfoButton;
    @FXML private Label lobbyNameLabel;
    @FXML private JFXButton joinLobbyButton;
    @FXML private AnchorPane topLevelAnchorPane;
    @FXML private FontAwesomeIconView expandIcon;
    private GameDTO game;
    private JoinHandler joinHandler;

    /**
     * The interface for the join handler.
     * Handles the joining of a game.
     */
    public interface JoinHandler {
        /**
         * Handle function of the interface.
         * Deals with joining a lobby.
         *
         * @param lobbyId The id of the lobby.
         */
        void handle(UUID lobbyId);
    }


    public LobbyListItemCtrl(GameDTO game, JoinHandler joinHandler) {
        this.game = game;
        this.joinHandler = joinHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.lobbyNameLabel.setText(game.getGameName());

        this.topLevelAnchorPane.maxHeightProperty().bind(
                Bindings.when(lobbyInfoPane.visibleProperty()).then(283).otherwise(63)
        );

        this.topLevelAnchorPane.minHeightProperty().bind(this.topLevelAnchorPane.maxHeightProperty());
        this.topLevelAnchorPane.prefHeightProperty().bind(topLevelAnchorPane.maxHeightProperty());

        this.expandIcon.glyphNameProperty().bind(
                Bindings.when(lobbyInfoPane.visibleProperty()).then("ANGLE_UP").otherwise("ANGLE_DOWN")
        );
        var extraInfoScreen = new ConfigurationScreenPane(game.getConfiguration());
        extraInfoScreen.makeTransparent();
        this.lobbyInfoPane.getChildren().add(extraInfoScreen);
        AnchorPane.setBottomAnchor(extraInfoScreen, 0d);
        AnchorPane.setTopAnchor(extraInfoScreen, 0d);
        AnchorPane.setLeftAnchor(extraInfoScreen, 0d);
        AnchorPane.setRightAnchor(extraInfoScreen, 0d);

        // a boolean that depicts if the lobby is full
        var lobbyFull = game.getPlayers().size() == game.getConfiguration().getCapacity();

        // If the lobby is full the button is disabled and the text says FULL
        // Otherwise the text is Join and the button is enabled.
        this.joinLobbyButton.setText((lobbyFull ? "FULL " : "JOIN ")
                        + game.getPlayers().size() + "/" + game.getConfiguration().getCapacity());
        this.joinLobbyButton.setDisable(lobbyFull);
    }

    @FXML
    private void joinLobbyButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        this.joinHandler.handle(this.game.getId());
    }

    @FXML
    private void showLobbyInfoButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        this.lobbyInfoPane.setVisible(!this.lobbyInfoPane.isVisible());
    }
}
