package client.scenes.leaderboard;

import commons.entities.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeaderboardEntryCtrl implements Initializable {

    private UserDTO user;
    private Integer rank;

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

        try {
            this.imageView.setImage(new Image(String.valueOf(new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50")),true));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
