package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.checkerframework.checker.units.qual.A;

/**
 * Game Screen Controller.
 */
public class GameScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private HBox avatarHBox;

    @FXML
    private HBox emojiHBox;

    @FXML
    private HBox powerUpHBox;

    @FXML
    private Button quitButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Label timerLabel;

    @FXML
    private Label questionNumberLabel;

    @FXML
    private Button emojiBarButton;

    @FXML
    private Button powerUpBarButton;

    @FXML
    private ImageView emojiBarIcon;

    @FXML
    private ImageView powerUpBarIcon;

    @FXML
    private ScrollPane emojiScrollPane;

    @FXML
    private ScrollPane powerUpScrollPane;

    /** Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public GameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var emojiUrls = server.getEmojis();
        try {
            emojiUrls.forEach(emojiUrl -> {
                var image = new ImageView();
                image.setImage(new Image(String.valueOf(emojiUrl),
                        40,
                        40,
                        false,
                        true));

                emojiHBox.getChildren().add(image);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        var leaderBoardUrls = server.getLeaderBoardImages();

        try {
            leaderBoardUrls.forEach(emojiUrl -> {
                var imageCircle = new Circle(21);
                imageCircle.setFill(new ImagePattern(new Image(String.valueOf(emojiUrl),
                        40,
                        40,
                        false,
                        true)));

                avatarHBox.getChildren().add(imageCircle);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void emojiBarButtonClick(ActionEvent actionEvent) {
        emojiScrollPane.setVisible(!emojiScrollPane.isVisible());
    }

    @FXML
    private void powerUpBarButtonClick(ActionEvent actionEvent) {
        powerUpScrollPane.setVisible(!powerUpScrollPane.isVisible());
    }

    @FXML
    private void quitButtonClick(ActionEvent actionEvent) {
        server.quitGame();
    }

    @FXML
    private void settingButtonClick(ActionEvent actionEvent) {
        System.out.println("Showing settings page.");
    }
}
