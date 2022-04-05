package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.utils.FileUtils;
import client.utils.communication.ServerUtils;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GamePlayerDTO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;


/**
 * Lobby player controller.
 */
@Generated
@Slf4j
public class LobbyPlayerCtrl implements Initializable {
    @FXML private Label playerName;
    @FXML private ImageView hostCrown;
    @FXML private Label playerScore;
    @FXML private JFXButton kickOutBtn;
    @FXML private ImageView playerImageView;

    private KickOutPlayer kickAction;

    /**
     * Action to execute if user clicked kick player.
     */
    public interface KickOutPlayer {
        void handle();
    }

    private GamePlayerDTO playerDTO;

    public LobbyPlayerCtrl(GamePlayerDTO playerDTO, KickOutPlayer kickAction) {
        this.playerDTO = playerDTO;
        this.kickAction = kickAction;
    }

    public void showRemovePlayerBtn(boolean doShow) {
        kickOutBtn.setVisible(doShow);
    }

    public void setPlayerHost(boolean isHost) {
        hostCrown.setVisible(isHost);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Stop managing the kick out button when it's not visible
        kickOutBtn.managedProperty().bind(kickOutBtn.visibleProperty());

        // Default setup
        playerName.setText(playerDTO.getNickname());

        // Sets the number of won games by the player by getting the user info
        ServerUtils.getUserInfoById(playerDTO.getUserId(), userDTO -> {
            runLater(() -> playerScore.setText(String.valueOf(userDTO.getGamesWon())));
        }, error -> log.error("Failed to get user."));

        String imageUrl = FileUtils.defaultUserPic;
        if (playerDTO.getProfilePic() != null) {
            imageUrl = ServerUtils.getImagePathFromId(playerDTO.getProfilePic());
        }
        this.playerImageView.setImage(new Image(imageUrl, true));

        setPlayerHost(false);
        showRemovePlayerBtn(false);
    }

    @FXML
    private void kickOutPlayer() {
        kickAction.handle();
    }
}
