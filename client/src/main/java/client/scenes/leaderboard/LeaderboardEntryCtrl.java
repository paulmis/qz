package client.scenes.leaderboard;

import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.communication.FileUtils;
import client.utils.communication.ServerUtils;
import commons.entities.auth.UserDTO;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Generated;

/**
 * Leaderboard entry controller.
 * Controls the user entry in the leaderboard list.
 */
@Generated
public class LeaderboardEntryCtrl implements Initializable {

    private final UserDTO user;
    private final Integer rank;

    @FXML private Label rankLabel;
    @FXML private Label nameLabel;
    @FXML private Label gamesLabel;
    @FXML private Label scoreLabel;
    @FXML private ImageView imageView;

    public LeaderboardEntryCtrl(UserDTO user, Integer rank) {
        this.user = user;
        this.rank = rank;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.rankLabel.setText(String.valueOf(rank));
        this.nameLabel.setText(user.getUsername());
        this.gamesLabel.setText(String.valueOf(user.getGamesPlayed()));
        this.scoreLabel.setText(String.valueOf(user.getScore()));

        String imageUrl = FileUtils.defaultUserPic;
        if (user.getProfilePic() != null) {
            imageUrl = ServerUtils.getImagePathFromId(user.getProfilePic());
        }
        this.imageView.setImage(new Image(imageUrl, true));
    }
}
