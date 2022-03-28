package client.scenes.lobby;

import com.jfoenix.controls.JFXButton;
import commons.entities.game.GamePlayerDTO;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Generated;

/**
 * Lobby player controller.
 */
@Generated
public class LobbyPlayerCtrl implements Initializable {
    @FXML private Label playerName;
    @FXML private ImageView hostCrown;
    @FXML private Label playerScore;
    @FXML private JFXButton kickOutBtn;

    private GamePlayerDTO playerDTO;

    public LobbyPlayerCtrl(GamePlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
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
        playerScore.setText(playerDTO.getScore().toString());
        setPlayerHost(false);
        showRemovePlayerBtn(false);
    }

    @FXML
    private void kickOutPlayer() {
        // ToDo: contact endpoint to kick out player
    }
}
