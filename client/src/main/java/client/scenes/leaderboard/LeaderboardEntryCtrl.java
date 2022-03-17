package client.scenes.leaderboard;

import commons.entities.UserDTO;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Leaderboard entry controller.
 * Controls the user entry in the leaderboard list.
 */
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

        var tempUrl = "https://media.wnyc.org/i/800/0/c/85/photologue/photos/putin%20square.jpg";
        this.imageView.setImage(new Image(tempUrl, true));
    }
}
