package client.scenes;

import client.scenes.questions.EstimateQuestionPane;
import client.scenes.questions.MultipleChoiceQuestionCtrl;
import client.scenes.questions.MultipleChoiceQuestionPane;
import client.utils.SSEEventHandler;
import client.utils.SSEHandler;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import commons.entities.messages.SSEMessageType;
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
import javax.ws.rs.core.Response;
import lombok.Generated;


/**
 * Game Screen Controller.
 * Handles question changing and interactivity of the user.
 */
@Generated
public class GameScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private SSEHandler sseHandler;

    @FXML private BorderPane mainBorderPane;
    @FXML private HBox avatarHBox;
    @FXML private HBox emojiHBox;
    @FXML private HBox powerUpHBox;
    @FXML private JFXButton quitButton;
    @FXML private JFXButton settingsButton;
    @FXML private Label timerLabel;
    @FXML private Label questionNumberLabel;
    @FXML private Button emojiBarButton;
    @FXML private Button powerUpBarButton;
    @FXML private ImageView emojiBarIcon;
    @FXML private ImageView powerUpBarIcon;
    @FXML private ScrollPane emojiScrollPane;
    @FXML private ScrollPane powerUpScrollPane;
    @FXML private AnchorPane settingsPanel;
    @FXML private JFXButton volumeButton;
    @FXML private JFXSlider volumeSlider;
    @FXML private JFXToggleButton muteEveryoneToggleButton;
    @FXML private FontAwesomeIconView volumeIconView;

    private SimpleIntegerProperty volume;

    private List<FontAwesomeIcon> volumeIconList;


    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public GameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }


    /**
     * This runs after every control has been initialized and
     * is not null.
     * We use this in order to directly reference the controls in the view.
     *
     * @param location These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // The following function calls handle
        // the set up of the emojis, powerUps, leaderBoard and volume controls.
        setUpEmojis();
        setUpPowerUps();
        setUpTopBarLeaderBoard();
        setUpVolume();

        // This loads the estimate question type.
        loadMockEstimate();
    }

    /**
     * This function resets the game screen ctrl.
     * It handles all the required set-up that needs to be done for a game to start.
     */
    public void reset(SSEHandler sseHandler) {
        this.sseHandler = sseHandler;
        sseHandler.initialize(this);
    }

    /**
     * A mock function that loads the mcq control.
     */
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

    /**
     * A mock function that loads the a estimate control.
     */
    private void loadMockEstimate() {
        mainBorderPane.setCenter(
                new EstimateQuestionPane(
                        "Short question",
                        System.out::println));
    }

    /**
     * Sets up the volume control.
     */
    private void setUpVolume() {

        // Initializes the volume property
        // The point of this property would be that in
        // further issues we can bind it to a "global"
        // settings object or something like that.
        volume = new SimpleIntegerProperty(100);

        // A list of icons so we can have a swift transition
        // between them when changing the volume
        volumeIconList = Arrays.asList(
                FontAwesomeIcon.VOLUME_OFF,
                FontAwesomeIcon.VOLUME_DOWN,
                FontAwesomeIcon.VOLUME_UP);

        // Bidirectional binding of the volume with the volume
        // property. This is to ensure we can report changes
        // instantly to the ui if the volume changes from
        // outside of our control.
        volumeSlider.valueProperty().bindBidirectional(volume);

        // a listener on the volume to change the icon
        // of the volume.
        volume.addListener((observable, oldValue, newValue) -> {

            // Sets the glyph name of the iconView directly
            volumeIconView.setGlyphName(volumeIconList.get(
                    Math.round(newValue.floatValue() / 100 * (volumeIconList.size() - 1))
            ).name());
        });
    }

    /**
     * Sets up the emoji bar.
     */
    private void setUpEmojis() {

        // Clears the emoji bar of items.
        emojiHBox.getChildren().clear();

        // Gets the emojis from the server.
        var emojiUrls = server.getEmojis();


        try {

            // Iterates over the emojis
            emojiUrls.forEach(emojiUrl -> {

                // Every emoji will be inside of a
                // button so that it is interactive.
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

    /**
     * Sets up the power ups bar.
     */
    private void setUpPowerUps() {

        // Clears the power ups bar of elements.
        powerUpHBox.getChildren().clear();

        // Gets the power ups from the server.
        // This is subject to change in the future.
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


    /**
     * Sets up the top bar leaderboard
     * of the users.
     */
    private void setUpTopBarLeaderBoard() {

        // Clears the avatars from the leaderboard
        avatarHBox.getChildren().clear();

        // Gets the leaderboard image urls from the server.
        var leaderBoardUrls = server.getLeaderBoardImages();

        try {

            // Iterate over the retrieved images
            for (int i = 0; i < leaderBoardUrls.size(); i++) {

                // We need to use a circle in order to make the
                // avatar frame.
                var imageCircle = new Circle(19);
                var imageUrl = leaderBoardUrls.get(i);

                // We set the id of the circle to Rank + the place in the rank.
                // This is done in order to style it in the css of the control.
                imageCircle.setId("Rank" + i);


                // This sets the fill of the circle to the image pattern
                imageCircle.setFill(new ImagePattern(new Image(String.valueOf(imageUrl),
                        40,
                        40,
                        false,
                        true)));

                // Adding the image to the hbox
                avatarHBox.getChildren().add(imageCircle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the emojiBar toggle button.
     * Toggles the between visible and not visible.
     *
     * @param actionEvent the action event of the button.
     */
    @FXML
    private void emojiBarButtonClick(ActionEvent actionEvent) {
        emojiScrollPane.setVisible(!emojiScrollPane.isVisible());
    }

    /**
     * Handles the powerUpBar toggle button.
     * Toggles the between visible and not visible.
     *
     * @param actionEvent the action event of the button.
     */
    @FXML
    private void powerUpBarButtonClick(ActionEvent actionEvent) {
        powerUpScrollPane.setVisible(!powerUpScrollPane.isVisible());
    }

    /**
     * Handles the click of the quit button.
     * This is handled by the server function call.
     *
     * @param actionEvent the action event of the button.
     */
    @FXML
    private void quitButtonClick(ActionEvent actionEvent) {
        //ToDo: Wrap inside a popup.
        mainCtrl.openGameLeaveWarning(() -> {
            mainCtrl.closeGameLeaveWarning();
            this.server.quitGame(new ServerUtils.QuitGameHandler() {
                @Override
                public void handle(Response response) {
                    javafx.application.Platform.runLater(() -> {
                        switch (response.getStatus()) {
                            case 200:
                                System.out.println("User successfully removed from game");
                                mainCtrl.showLobbyListScreen();
                                break;
                            case 404:
                                System.out.println("User/Game not found");
                                break;
                            case 409:
                                System.out.println("Couldn't remove player");
                                break;
                            default:
                                break;
                        }
                    });
                }
            });
        });
    }


    /**
     * Handles the settings toggle button.
     * Toggles the between visible and not visible.
     *
     * @param actionEvent the action event of the button.
     */
    @FXML
    private void settingButtonClick(ActionEvent actionEvent) {
        settingsPanel.setVisible(!settingsPanel.isVisible());
    }

    /**
     * Handles the volume button click.
     * It should toggle between 0 and 100 on different button clicks.
     *
     * @param actionEvent The actionEvent of the button.
     */
    @FXML
    private void volumeButtonClick(ActionEvent actionEvent) {
        volume.setValue(volume.getValue() == 0 ? 100 : 0);
    }

    /**
     * Example of a sse event handler.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_LEFT)
    public void playerLeft(String playerId) {

    }
}
