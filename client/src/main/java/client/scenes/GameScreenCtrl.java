package client.scenes;

import client.scenes.questions.CloseQuestionCtrl;
import client.scenes.questions.CloseQuestionPane;
import client.scenes.questions.MultipleChoiceQuestionCtrl;
import client.scenes.questions.MultipleChoiceQuestionPane;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Game Screen Controller.
 */
public class GameScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private HBox avatarHBox;

    @FXML
    private HBox emojiHBox;

    @FXML
    private HBox powerUpHBox;

    @FXML
    private JFXButton quitButton;

    @FXML
    private JFXButton settingsButton;

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
        setEmojis();
        setPowerUps();
        setTopBarLeaderBoard();


        //CloseQuestionCtrl.AnswerHandler doSomething = (e) -> {};

        //mainBorderPane.setCenter(new CloseQuestionPane("Short question", doSomething));


        try {
            MultipleChoiceQuestionCtrl.AnswerHandler doSomething = () -> {};

            mainBorderPane.setCenter(new MultipleChoiceQuestionPane("Short question",
                    Arrays.asList("answer 12", "asdasd", "asdasdasd", "asdasda"),
                    Arrays.asList(
                            new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                            new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                            new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                            new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50")),
                    Arrays.asList(doSomething, doSomething, doSomething, doSomething)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setEmojis() {
        emojiHBox.getChildren().clear();
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
    }

    private void setPowerUps() {
        powerUpHBox.getChildren().clear();
        var powerUpUrls = server.getEmojis();
        try {
            powerUpUrls.forEach(powerUpUrl -> {
                var image = new ImageView();
                image.setImage(new Image(String.valueOf(powerUpUrl),
                        40,
                        40,
                        false,
                        true));

                powerUpHBox.getChildren().add(image);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setTopBarLeaderBoard() {
        avatarHBox.getChildren().clear();
        var leaderBoardUrls = server.getLeaderBoardImages();
        try {
            for (int i = 0; i < leaderBoardUrls.size(); i++) {
                var imageCircle = new Circle(21);
                var imageUrl = leaderBoardUrls.get(i);
                imageCircle.setId("Rank" + i);

                imageCircle.setFill(new ImagePattern(new Image(String.valueOf(imageUrl),
                        40,
                        40,
                        false,
                        true)));

                avatarHBox.getChildren().add(imageCircle);
            }
            leaderBoardUrls.forEach(emojiUrl -> {

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
