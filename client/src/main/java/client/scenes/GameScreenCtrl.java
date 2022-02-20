package client.scenes;

import client.scenes.questions.EstimateQuestionPane;
import client.scenes.questions.MultipleChoiceQuestionCtrl;
import client.scenes.questions.MultipleChoiceQuestionPane;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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

    @FXML
    private AnchorPane settingsPanel;

    @FXML
    private JFXButton volumeButton;

    @FXML
    private JFXSlider volumeSlider;

    @FXML
    private JFXToggleButton muteEveryoneToggleButton;

    @FXML
    private FontAwesomeIconView volumeIconView;

    private SimpleIntegerProperty volume;

    private List<FontAwesomeIcon> volumeIconList;


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
        setUpEmojis();
        setUpPowerUps();
        setUpTopBarLeaderBoard();
        setUpVolume();

        loadMockEstimate();
    }

    private void loadMockMCQ() {
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

    private void loadMockEstimate() {
        mainBorderPane.setCenter(
                new EstimateQuestionPane(
                        "Short question",
                        System.out::println));
    }

    private void setUpVolume() {
        volume = new SimpleIntegerProperty(100);
        volumeIconList = Arrays.asList(
                FontAwesomeIcon.VOLUME_OFF,
                FontAwesomeIcon.VOLUME_DOWN,
                FontAwesomeIcon.VOLUME_UP);

        volumeSlider.valueProperty().bindBidirectional(volume);
        volume.addListener((observable, oldValue, newValue) -> {
            volumeIconView.setGlyphName(volumeIconList.get(
                    Math.round(newValue.floatValue() / 100 * (volumeIconList.size() - 1))
            ).name());
        });
    }

    private void setUpEmojis() {
        emojiHBox.getChildren().clear();
        var emojiUrls = server.getEmojis();
        try {
            emojiUrls.forEach(emojiUrl -> {
                var jfxButton = new JFXButton();
                jfxButton.setPadding(Insets.EMPTY);
                jfxButton.setRipplerFill(Color.WHITESMOKE);

                jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                var image = new ImageView();

                image.setImage(new Image(String.valueOf(emojiUrl),
                        35,
                        35,
                        false,
                        true));
                jfxButton.setGraphic(image);

                emojiHBox.getChildren().add(jfxButton);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpPowerUps() {
        powerUpHBox.getChildren().clear();
        var powerUpUrls = server.getPowerUps();
        try {
            powerUpUrls.forEach(powerUpUrl -> {
                var jfxButton = new JFXButton();
                jfxButton.setPadding(Insets.EMPTY);
                jfxButton.setRipplerFill(Color.WHITESMOKE);

                jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                var image = new ImageView();

                image.setImage(new Image(String.valueOf(powerUpUrl),
                        35,
                        35,
                        false,
                        true));
                jfxButton.setGraphic(image);

                powerUpHBox.getChildren().add(jfxButton);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setUpTopBarLeaderBoard() {
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
        settingsPanel.setVisible(!settingsPanel.isVisible());
    }

    @FXML
    private void volumeButtonClick(ActionEvent actionEvent) {
        volume.setValue(volume.getValue() == 0 ? 100 : 0);
    }
}
