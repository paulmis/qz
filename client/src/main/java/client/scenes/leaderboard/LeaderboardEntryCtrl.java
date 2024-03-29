package client.scenes.leaderboard;

import client.utils.FileUtils;
import client.utils.communication.ServerUtils;
import commons.entities.auth.UserDTO;
import commons.entities.game.GamePlayerDTO;
import java.net.URL;
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
        String name = gamePlayer == null ? user.getUsername() : gamePlayer.getNickname();
        int points = gamePlayer == null ? user.getScore() : gamePlayer.getScore();

        this.rankLabel.setText(String.valueOf(rank));
        this.nameLabel.setText(name);
        this.scoreLabel.setText(String.valueOf(points));

        if (gamePlayer != null) {
            this.gamesLabel.setVisible(false);
            String imageUrl = FileUtils.defaultUserPic;
            if (gamePlayer.getProfilePic() != null) {
                imageUrl = ServerUtils.getImagePathFromId(gamePlayer.getProfilePic());
            }
            this.imageView.setImage(new Image(imageUrl, true));
        } else {
            this.gamesLabel.setText(String.valueOf(user.getGamesWon()));
            String imageUrl = FileUtils.defaultUserPic;
            if (user.getProfilePic() != null) {
                imageUrl = ServerUtils.getImagePathFromId(user.getProfilePic());
            }
            this.imageView.setImage(new Image(imageUrl, true));
        }

    }
}
