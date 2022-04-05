package client.scenes.leaderboard;

import client.utils.FileUtils;
import client.utils.communication.ServerUtils;
import commons.entities.auth.UserDTO;
import java.net.URL;
import java.util.ResourceBundle;

import commons.entities.game.GamePlayerDTO;
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

    private UserDTO user;
    private GamePlayerDTO gamePlayer;
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

    public LeaderboardEntryCtrl(GamePlayerDTO gamePlayer, Integer rank) {
        this.gamePlayer = gamePlayer;
        this.rank = rank;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (gamePlayer != null) {
            this.rankLabel.setText(String.valueOf(rank));
            this.nameLabel.setText(gamePlayer.getNickname());
            this.gamesLabel.setVisible(false);
            this.scoreLabel.setText(String.valueOf(gamePlayer.getScore()));
            var tempUrl = "https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg";
            this.imageView.setImage(new Image(tempUrl, true));
        } else {
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
