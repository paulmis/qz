package client.scenes.game;

import static javafx.application.Platform.runLater;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.scenes.questions.EstimateQuestionPane;
import client.scenes.questions.QuestionPane;
import client.scenes.questions.StartGamePane;
import client.utils.ClientState;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import commons.entities.AnswerDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.messages.SSEMessageType;
import commons.entities.questions.QuestionDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import javafx.application.Platform;
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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;


/**
 * Game Screen Controller.
 * Handles question changing and interactivity of the user.
 */
@Generated
@Slf4j
public class GameScreenCtrl implements Initializable, SSESource {
    private final GameCommunication communication;
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
    private Label pointsLabel;
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

    private StackPane centerPane;

    private SimpleIntegerProperty volume;
    private List<FontAwesomeIcon> volumeIconList;

    private SimpleIntegerProperty timeLeft;
    private Timer timer;
    private TimerTask timerTask;


    /**
     * Initialize a new controller using dependency injection.
     *
     * @param communication Reference to communication utilities object.
     * @param mainCtrl      Reference to the main controller.
     */
    @Inject
    public GameScreenCtrl(GameCommunication communication, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
    }

    public void bindHandler(SSEHandler handler) {
        handler.initialize(this);
    }

    /**
     * This runs after every control has been initialized and
     * is not null.
     * We use this in order to directly reference the controls in the view.
     *
     * @param location  These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // The following function calls handle
        // the set-up of the emojis, powerUps, leaderBoard and volume controls.
        setUpEmojis();
        setUpPowerUps();
        setUpTopBarLeaderBoard();
        setUpVolume();
        setUpTimer();

        // This loads the estimate question type.
        loadMockEstimate();
    }

    /**
     * Transits the client to the question stage.
     */
    @SSEEventHandler(SSEMessageType.START_QUESTION)
    public void toQuestionStage(Integer delay) {
        log.debug("Question stage handler triggered. Delay: {}", delay);
        // Set the current question
        GameCommunication.updateCurrentQuestion(
                ClientState.game.getId(),
                // Success
                (question) -> runLater(() -> {
                    log.debug("Received a question: {}", question.getId());
                    setQuestion(question);
                    // Start the timer
                    startTimer(Duration.ofMillis(delay));
                }),
                // Failure
                () -> runLater(
                        () -> mainCtrl.showErrorSnackBar("Unable to retrieve the current question")));
    }

    /**
     * Transits the client to the answer stage.
     */
    @SSEEventHandler(SSEMessageType.STOP_QUESTION)
    public void toAnswerStage(Integer delay) {
        // TODO: implement
        log.debug("The answer stage has been reached. Delay: {}", delay);
        GameCommunication.updateCurrentAnswer(
                ClientState.game.getId(),
                // Success
                (answer) -> runLater(() -> {
                    log.debug("Received answer for question {}", answer.getQuestionId());
                    setAnswer(answer);
                    // Start the timer
                    startTimer(Duration.ofMillis(delay));
                }),
                // Failure
                () -> {
                    log.error("Unable to retrieve the current answer.");
                    runLater(
                            () -> mainCtrl.showErrorSnackBar("Unable to retrieve the current answer")
                    );
                }
        );

//        GameCommunication.updateScoreLeaderboard(
//                ClientState.game.getId(),
//                // Success
//                (leaderboard) -> runLater(() -> {
//                    log.debug("Received leaderboard: {}", (GamePlayerDTO[]) leaderboard);
//                }),
//                // Failure
//                () -> runLater(
//                        () -> mainCtrl.showErrorSnackBar("Unable to retrieve the leaderboard")
//                )
//        );
    }

    /**
     * Show the answer in the UI.
     *
     * @param answer The answer to set.
     */
    private void setAnswer(AnswerDTO answer) {
        if (answer == null) {
            mainCtrl.showErrorSnackBar("Unable to retrieve the current answer");
            log.warn("setAnswer: answer is null");
            return;
        }
        if (!(this.centerPane instanceof QuestionPane)) {
            log.warn("setAnswer: centerPane is not a QuestionPane, it is a {}", this.centerPane.getClass());
            return;
        }

        log.debug("setAnswer: setting answer");
        ((QuestionPane) this.centerPane).showAnswer(answer);
    }

    /**
     * Transits the client to the finish stage.
     */
    @SSEEventHandler(SSEMessageType.GAME_END)
    public void toFinishStage() {
        // Clean up the game and kill the connection
        ClientState.game = null;
        ServerUtils.sseHandler.kill();

        // TODO: display final standings instead
        mainCtrl.showInformationalSnackBar("The game has ended");
        mainCtrl.showLobbyListScreen();
    }


    /**
     * A mock function that loads the estimate control.
     */
    private void loadMockEstimate() {
        this.centerPane = new EstimateQuestionPane(
                "Short question",
                log::debug);
        mainBorderPane.setCenter(this.centerPane);
    }

    /**
     * Generate a new timer task.
     *
     * @return the generated timer task.
     */
    private TimerTask getTimerTask() {
        log.debug("Generating a new timer task");
        return new TimerTask() {
            @Override
            public void run() {
                // We need to use this because we need to update the UI thread.
                Platform.runLater(() -> {
                    log.debug("Timer ticked");
                    // Decrement the time left.
                    timeLeft.set(timeLeft.get() - 1);
                    // If the time left is 0, stop the timer.
                    if (timeLeft.get() == 0) {
                        timer.cancel();
                    }
                });
            }
        };
    }

    /**
     * Initializes the timer.
     */
    private void setUpTimer() {
        // Keeps track of the time left.
        timeLeft = new SimpleIntegerProperty(10);
        // Connect the time left to the label.
        timerLabel.textProperty().bind(timeLeft.asString());

        // Create a new timer task.
        this.timer = new Timer();
    }

    /**
     * Start a timer with a specified duration.
     *
     * @param duration duration of the timer in seconds.
     */
    public synchronized void startTimer(Duration duration) {
        // XXX: this could probably be done in a better way,
        // but timers are getting cancelled randomly, and I
        // don't really have the time to fix it.

        // Stop existing timer
        if (timer != null) {
            try {
                timer.cancel();
            } catch (Exception e) {
                log.debug("Unable to cancel timer task: {}", e.getMessage());
            }
        }
        // Create a new timer
        timer = new Timer();
        // Reset the time left.
        timeLeft.set((int) duration.toSeconds());

        log.debug("Starting timer with duration {}", duration);

        // Schedule the task
        timerTask = getTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
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
        List<URL> emojiUrls = communication.getEmojis();

        try {
            // Iterates over the emojis
            emojiHBox.getChildren().addAll(
                    emojiUrls.stream().map(emojiUrl -> {
                        JFXButton jfxButton = new JFXButton();
                        jfxButton.setPadding(Insets.EMPTY);
                        jfxButton.setRipplerFill(Color.WHITESMOKE);

                        jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                        ImageView image = new ImageView();
                        image.setImage(new Image(String.valueOf(emojiUrl),
                                35,
                                35,
                                false,
                                true));
                        jfxButton.setGraphic(image);

                        return jfxButton;
                    }).collect(Collectors.toList())
            );
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
        List<URL> powerUpUrls = communication.getPowerUps();

        try {
            powerUpUrls.forEach(powerUpUrl -> {
                JFXButton jfxButton = new JFXButton();
                jfxButton.setPadding(Insets.EMPTY);
                jfxButton.setRipplerFill(Color.WHITESMOKE);

                jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                ImageView image = new ImageView();

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
        List<URL> leaderBoardUrls = communication.getLeaderBoardImages();

        try {

            // Iterate over the retrieved images
            for (int i = 0; i < leaderBoardUrls.size(); i++) {

                // We need to use a circle in order to make the
                // avatar frame.
                Circle imageCircle = new Circle(19);
                URL imageUrl = leaderBoardUrls.get(i);

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
        // Open the warning and wait for user action
        mainCtrl.openGameLeaveWarning(
                // If confirmed, exit the game
                () -> {
                    mainCtrl.closeGameLeaveWarning();
                    this.communication.quitGame(
                            (response) -> runLater(() -> {
                                switch (response.getStatus()) {
                                    case 200:
                                        System.out.println("User successfully removed from game");
                                        mainCtrl.showLobbyListScreen();
                                        ClientState.game = null;
                                        ServerUtils.sseHandler.kill();
                                        break;
                                    case 404:
                                        mainCtrl.showErrorSnackBar("Unable to quit the game: "
                                                + "user or game doesn't exist");
                                        break;
                                    case 409:
                                        mainCtrl.showErrorSnackBar("Unable to quit the game: "
                                                + "there was a conflict while removing the player");
                                        break;
                                    default:
                                        mainCtrl.showErrorSnackBar("Unable to quit the game: server error");
                                }
                            }));
                },
                // Otherwise, simply close the warning
                mainCtrl::closeGameLeaveWarning
        );
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
     * Shows the question on the screen.
     *
     * @param question the question to show.
     */
    public void setQuestion(QuestionDTO question) {
        // If the question is null, display an empty start pane
        try {
            if (question == null) {
                log.debug("Question is null, showing start game pane");
                this.centerPane = new StartGamePane(mainCtrl, communication);
                mainBorderPane.setCenter(this.centerPane);
                // Otherwise, show a question pane
            } else {
                log.debug("Showing question pane");
                this.centerPane = new QuestionPane(mainCtrl, communication, question);
                mainBorderPane.setCenter(this.centerPane);
            }
        } catch (IOException e) {
            log.error("Error loading the FXML file");
            e.printStackTrace();
            Platform.exit();
            System.exit(0);
        }

        // Set the current question
        ClientState.currentQuestion = question;
    }
}
