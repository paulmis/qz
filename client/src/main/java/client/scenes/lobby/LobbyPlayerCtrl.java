package client.scenes.lobby;

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
    @FXML private ImageView hostImg;
    @FXML private Label playerScore;

    private GamePlayerDTO playerDTO;

    public LobbyPlayerCtrl(GamePlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
    }

    public void setPlayerHost(boolean isHost) {
        hostImg.setVisible(isHost);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerName.setText(playerDTO.getNickname());
        playerScore.setText(playerDTO.getScore().toString());
        setPlayerHost(false);
    }
}
