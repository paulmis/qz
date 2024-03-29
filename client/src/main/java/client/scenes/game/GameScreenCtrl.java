package client.scenes.game;

import static javafx.application.Platform.runLater;

import client.communication.game.GameCommunication;
import client.scenes.MainCtrl;
import client.scenes.leaderboard.LeaderboardPane;
import client.scenes.questions.MCQuestionCtrl;
import client.scenes.questions.QuestionCtrl;
import client.scenes.questions.QuestionPane;
import client.scenes.questions.StartGamePane;
import client.utils.ClientState;
import client.utils.FileUtils;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import client.utils.communication.SSEEventHandler;
import client.utils.communication.SSEHandler;
import client.utils.communication.SSESource;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.PowerUp;
import commons.entities.game.ReactionDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import commons.entities.questions.QuestionDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.common.util.StringUtils;


/**
 * Game Screen Controller.
 * Handles question changing and interactivity of the user.
 */
@Generated
@Slf4j
public class GameScreenCtrl implements Initializable, SSESource {
    private final GameCommunication communication;
    private final MainCtrl mainCtrl;

    @FXML private BorderPane mainBorderPane;
    @Getter
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
    @FXML private Label pointsLabel;
    @FXML private AnchorPane settingsPanel;
    @FXML private JFXButton volumeButton;
    @FXML private JFXSlider volumeSlider;
    @FXML private JFXToggleButton muteEveryoneToggleButton;
    @FXML private FontAwesomeIconView volumeIconView;

    private StackPane centerPane;

    private List<FontAwesomeIcon> volumeIconList;

    private SimpleIntegerProperty timeLeft;
    private Timer timer;
    private TimerTask timerTask;

    private Map<String, URI> reactions;
    // Allows us to cancel profile picture update tasks
    private final Map<UUID, Timeline> userProfileTimelines = new HashMap<>();
    // Allows us to persist pictures throughout stages
    private final Map<UUID, Circle> userCircles = new HashMap<>();
    // Allows us to restore pictures back to original state
    private final Map<UUID, String> userProfilePictures = new HashMap<>();


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
        setUpPowerUps();
        setUpLeaderBoard();
        setUpVolume();
        setUpTimer();
        questionNumberLabel.setText("");
    }

    /**
     * Setup that is run after the server has connected.
     */
    public void setup() {
        setUpEmojis();
    }

    /**
     * Resets the controller to a predefined state.
     */
    public void reset() {
        setUpPowerUps();
        pointsLabel.setText("0");
    }

    /**
     * Transits the client to the question stage.
     */
    @SSEEventHandler(SSEMessageType.START_QUESTION)
    public void toQuestionStage(Integer delay) {
        SoundManager.playMusic(SoundEffect.QUESTION_START, getClass());
        log.debug("Question stage handler triggered. Delay: {}", delay);

        communication.getQuestionNumber(ClientState.game.getId(), questionNumber -> runLater(() -> {
            // Sets the question number
            Integer qnum = questionNumber + 1;
            if (ClientState.game.getConfiguration() instanceof NormalGameConfigurationDTO) {
                questionNumberLabel.setText(
                        qnum
                                + " of "
                                + ((NormalGameConfigurationDTO) ClientState.game.getConfiguration()).getNumQuestions());
            } else {
                questionNumberLabel.setText(qnum.toString());
            }
        }), error -> runLater(() -> log.error("error occurred: " + error.getDescription())));


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

        GameCommunication.updateScoreLeaderboard(
                ClientState.game.getId(),
                // Success
                (leaderboard) -> runLater(() -> {
                    log.debug("Received leaderboard: {}", leaderboard);
                    this.showLeaderboard(leaderboard);

                    // Show player's updated score
                    Optional<GamePlayerDTO> myPlayer = leaderboard.stream()
                            .filter(player -> player.getUserId().equals(ClientState.user.getId()))
                            .findFirst();
                    myPlayer.ifPresent(this::showScore);
                }),
                // Failure
                () -> runLater(
                        () -> mainCtrl.showErrorSnackBar("Unable to retrieve the leaderboard")
                )
        );
    }

    /**
     * Handles the "player left" notification.
     *
     * @param userId ID of the user leaving
     */
    @SSEEventHandler(SSEMessageType.PLAYER_LEFT)
    public void playerLeftReact(UUID userId) {
        String username = ClientState.game.getPlayers().stream()
                .filter(player -> player.getUserId().equals(userId))
                .map(GamePlayerDTO::getNickname)
                .findFirst().orElse("<Unknown>");
        mainCtrl.showInformationalSnackBar("Player " + username + " has left the game.");
        updateInGame();
    }

    /**
     * Handles the "player joined" notification.
     */
    @SSEEventHandler(SSEMessageType.PLAYER_REJOINED)
    public void playerRejoinedReact() {
        mainCtrl.showInformationalSnackBar("A player has rejoined the game.");
        updateInGame();
    }

    /**
     * Adapts to change in game.
     */
    public void updateInGame() {
        log.debug("An update in the game has occurred");
        // Update Leaderboard to show changes
        GameCommunication.updateScoreLeaderboard(
                ClientState.game.getId(),
                // Success
                (leaderboard) -> runLater(() -> {
                    log.debug("Received leaderboard: {}", leaderboard);
                    this.showLeaderboard(leaderboard);
                }),
                // Failure
                () -> runLater(
                        () -> mainCtrl.showErrorSnackBar("Unable to retrieve the leaderboard")
                )
        );
    }

    /**
     * Sets up the top bar leaderboard
     * of the users.
     */
    private void setUpLeaderBoard() {
        // Clears the avatars from the leaderboard
        avatarHBox.getChildren().clear();
    }

    /**
     * Shows the in game leaderboard.
     *
     * @param delay integer representing how long to show the leaderboard.
     */
    @SSEEventHandler(SSEMessageType.SHOW_LEADERBOARD)
    public void toLeaderboardStage(Integer delay) {
        GameCommunication.updateScoreLeaderboard(
                ClientState.game.getId(),
                // Success
                (leaderboard) -> runLater(() -> {
                    log.debug("Received leaderboard: {}", leaderboard);
                    var leaderboardNode = new LeaderboardPane();
                    leaderboardNode.setViewOrder(Integer.MAX_VALUE);
                    leaderboardNode.resetInGame(leaderboard);
                    this.mainBorderPane.setCenter(leaderboardNode);
                    if (delay != null) {
                        startTimer(Duration.ofMillis(delay));
                    }
                }),
                // Failure
                () -> runLater(
                        () -> mainCtrl.showErrorSnackBar("Unable to retrieve the leaderboard")
                )
        );
    }

    private void showLeaderboard(List<GamePlayerDTO> players) {
        log.info("Showing leaderboard");

        // Clears the avatars from the leaderboard
        this.userCircles.clear();
        this.userProfilePictures.clear();
        // Clear the in-game leaderboard
        avatarHBox.getChildren().clear();

        // We need to keep track of the counter
        for (int i = 0; i < players.size(); ++i) {
            GamePlayerDTO player = players.get(i);
            log.debug("Adding player {} to leaderboard", player.getId());

            Circle circle;
            if (this.userCircles.containsKey(player.getUserId())) {
                // If we already have a circle instance, reuse it
                circle = this.userCircles.get(player.getUserId());
                Tooltip.uninstall(circle, null);
            } else {
                // Otherwise, create a new circle

                // Fill of the circle to the image pattern
                String imageUrl = FileUtils.defaultUserPic;
                if (player.getProfilePic() != null) {
                    imageUrl = ServerUtils.getImagePathFromId(player.getProfilePic());
                }

                circle = new Circle(19);
                circle.setFill(new ImagePattern(new Image(imageUrl,
                        40,
                        40,
                        false,
                        true)));

                this.userCircles.put(player.getUserId(), circle);
                this.userProfilePictures.put(player.getUserId(), imageUrl);
            }
            circle.setId("Rank" + i);

            // Adding the image to the HBox
            this.avatarHBox.getChildren().add(circle);

            // Create the tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setText(player.getNickname() + ": " + player.getScore() + " points");
            Tooltip.install(circle, tooltip);
        }
    }

    /**
     * Displays the score of the player.
     *
     * @param player the player information
     */
    public void showScore(GamePlayerDTO player) {
        pointsLabel.setText(String.valueOf(player.getScore()));

        if (ClientState.previousScore.isPresent()) {
            if (ClientState.previousScore.get() < player.getScore()) {
                SoundManager.playMusic(SoundEffect.CORRECT_ANSWER, getClass());
                mainCtrl.showInformationalSnackBar("You have gained "
                                + (player.getScore() - ClientState.previousScore.get()) + " points!",
                        javafx.util.Duration.seconds(2));
            } else {
                SoundManager.playMusic(SoundEffect.INCORRECT_ANSWER, getClass());
                mainCtrl.showErrorSnackBar("You have lost "
                                + (ClientState.previousScore.get() - player.getScore()) + " points!",
                        javafx.util.Duration.seconds(2));
            }
            ClientState.previousScore = Optional.of(player.getScore());
        }
    }

    /**
     * Handles the power-up played event.
     *
     * @param powerUp the power-up that has been played.
     */
    @SSEEventHandler(SSEMessageType.POWER_UP_PLAYED)
    public void handlePowerUP(PowerUp powerUp) {
        mainCtrl.showInformationalSnackBar(StringUtils.capitalize(powerUp.getPowerUpName()) + " has been played!");
        SoundManager.playMusic(SoundEffect.POWER_UP, getClass());

        switch (powerUp) {
            case HalveTime:
                timeLeft.set(timeLeft.get() / 2);
                break;
            case DoublePoints:
                // ToDo
                break;
            default:
                break;
        }
    }

    /**
     * Handles the power-up played event.
     *
     * @param reaction the reaction that has been sent.
     */
    @SSEEventHandler(SSEMessageType.REACTION)
    public void handleReaction(ReactionDTO reaction) {

        // Verify that all fields are populated
        if (reaction.getUserId() == null || reaction.getReactionType() == null) {
            log.error("Received a reaction with null values");
            return;
        }

        log.debug("Received reaction {} from user {}", reaction.getReactionType(), reaction.getUserId());

        // Check if the reactions are muted
        if (muteEveryoneToggleButton.isSelected()) {
            log.debug("Not showing the reaction as they are muted");
            return;
        }

        SoundManager.playMusic(SoundEffect.EMOJI, getClass());

        // Verify that we have the reaction URI
        URI reactionUrl = this.reactions.get(reaction.getReactionType());
        String userImageUrl = this.userProfilePictures.get(reaction.getUserId());
        if (reactionUrl == null || userImageUrl == null) {
            log.error("Received an unsupported reaction or an unknown user");
            return;
        }

        // Get the image circle
        Circle userImageCircle = this.userCircles.get(reaction.getUserId());
        if (userImageCircle == null) {
            log.error("Received a reaction from a user that is not in the leaderboard");
            return;
        }

        // If there is already a timeline registered for this particular user picture,
        // we need to remove it
        if (this.userProfileTimelines.get(reaction.getUserId()) != null) {
            try {
                this.userProfileTimelines.get(reaction.getUserId()).stop();
            } catch (Exception e) {
                log.debug("Could not properly stop timeline for user {}", reaction.getUserId());
            }
        }

        final KeyFrame kf1 = new KeyFrame(javafx.util.Duration.seconds(0), e -> {
            log.debug("Setting the reaction image to {}", reactionUrl);
            try {
                userImageCircle.setFill(new ImagePattern(new Image(reactionUrl.toString(),
                        40,
                        40,
                        false,
                        true)));
            } catch (Exception ex) {
                log.error("Failed to set reaction image", ex);
            }
        });

        final KeyFrame kf2 = new KeyFrame(javafx.util.Duration.seconds(5), e -> {
            log.debug("Restoring the user image to {}", userImageUrl);
            try {
                userImageCircle.setFill(new ImagePattern(new Image(userImageUrl,
                        40,
                        40,
                        false,
                        true)));
            } catch (Exception ex) {
                log.error("Failed to restore user image", ex);
            }
        });

        final Timeline timeline = new Timeline(kf1, kf2);
        // Run and register the timeline
        userProfileTimelines.put(reaction.getUserId(), timeline);
        runLater(timeline::play);
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
        settingsPanel.setVisible(false);
        emojiScrollPane.setVisible(false);
        powerUpScrollPane.setVisible(false);

        // Clean up the game and kill the connection
        ClientState.game = null;
        ServerUtils.sseHandler.kill();
        mainCtrl.showInformationalSnackBar("The game has ended");
        mainCtrl.showLobbyListScreen();
        this.timer.cancel();
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
        volumeSlider.valueProperty().bindBidirectional(SoundManager.volume);

        // a listener on the volume to change the icon
        // of the volume.
        SoundManager.volume.addListener((observable, oldValue, newValue) -> {

            // Sets the glyph name of the iconView directly
            volumeIconView.setGlyphName(volumeIconList.get(
                    Math.round(newValue.floatValue() / 100 * (volumeIconList.size() - 1))
            ).name());
        });

        SoundManager.everyoneMuted.bindBidirectional(muteEveryoneToggleButton.selectedProperty());
    }

    /**
     * Sets up the emoji bar.
     */
    private void setUpEmojis() {
        communication.getReactions((reactions) -> {
            // Update global reaction map
            this.reactions = reactions;

            // For each reaction, add a button to the emoji bar.
            this.emojiHBox.getChildren().setAll(reactions.entrySet().stream().map((entry) -> {
                // Construct the button
                JFXButton jfxButton = new JFXButton();
                jfxButton.setPadding(Insets.EMPTY);
                jfxButton.setRipplerFill(Color.ORANGE);

                jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                // Set reaction image
                ImageView image = new ImageView();
                image.setImage(new Image(entry.getValue().toString(), 35, 35, false, true));
                jfxButton.setGraphic(image);

                jfxButton.setOnMouseClicked((event) -> {
                    // Hide the emoji bar
                    emojiScrollPane.setVisible(false);

                    // Construct a new reaction
                    ReactionDTO reaction = new ReactionDTO(ClientState.game.getId(), entry.getKey());
                    // Send the reaction to the server
                    this.communication.sendReaction(reaction,
                            () -> log.debug("Reaction sent successfully"),
                            (error) -> this.mainCtrl.showErrorSnackBar("Failed to send reaction"));

                    jfxButton.setTooltip(new Tooltip(StringUtils.capitalize(entry.getValue().toString())));
                });
                return jfxButton;
            }).collect(Collectors.toList()));
        }, () -> this.mainCtrl.showErrorSnackBar("Unable to load reactions"));
    }

    /**
     * Sets up the power ups bar.
     */
    private void setUpPowerUps() {

        // Clears the power ups bar of elements.
        powerUpHBox.getChildren().clear();


        List<PowerUp> powerUps = Arrays.stream(PowerUp.values()).collect(Collectors.toList());

        try {
            powerUps.forEach(powerUp -> {
                JFXButton jfxButton = new JFXButton();
                jfxButton.setPadding(Insets.EMPTY);

                jfxButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                ImageView image = new ImageView();

                String imageLocation = Objects.requireNonNull(getClass()
                                .getResource("/client/images/powerups/" + powerUp.name() + ".png"))
                        .toExternalForm();

                image.setImage(new Image(imageLocation,
                        40,
                        40,
                        false,
                        true));

                jfxButton.setGraphic(image);
                jfxButton.setOnAction(event -> communication.sendPowerUp(powerUp,
                        (activity) -> runLater(() -> {
                            powerUpScrollPane.setVisible(false);
                            jfxButton.setDisable(true);
                            if (powerUp == PowerUp.IncorrectAnswer
                                    && activity != null && centerPane instanceof QuestionPane) {
                                log.warn("Eliminating answer {}", activity.toString());
                                List<ActivityDTO> activities = List.of(activity);
                                ((QuestionPane) centerPane).removeAnswer(
                                        new AnswerDTO(ClientState.game.getCurrentQuestion().getId(), activities));
                            }
                        }),
                        error -> runLater(() ->
                                mainCtrl.showErrorSnackBar("Error occured: " + error.getDescription()))));

                powerUpHBox.getChildren().add(jfxButton);
            });
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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
        settingsPanel.setVisible(false);
        emojiScrollPane.setVisible(false);
        powerUpScrollPane.setVisible(false);
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        // This makes the button just get you out of the lobby
        // if it has already finished.
        if (ClientState.game == null) {
            mainCtrl.showLobbyListScreen();
            ServerUtils.sseHandler.kill();
            return;
        }

        // Open the warning and wait for user action
        mainCtrl.openGameLeaveWarning(
                // If confirmed, exit the game
                () -> {
                    mainCtrl.closeGameLeaveWarning();
                    communication.quitGame(
                            (response) -> runLater(() -> {
                                switch (response.getStatus()) {
                                    case 200:
                                        log.debug("User successfully removed from game");
                                        mainCtrl.showLobbyListScreen();
                                        this.timer.cancel();
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        SoundManager.volume.setValue(SoundManager.volume.getValue() == 0 ? 100 : 0);
    }

    /**
     * Shows the question on the screen.
     *
     * @param question the question to show.
     */
    public void setQuestion(QuestionDTO question) {
        try {
            if (question == null) {
                // If the question is null, display an empty start pane
                log.debug("Question is null, showing start game pane");
                this.centerPane = new StartGamePane(mainCtrl, communication);
            } else {
                // Otherwise, show a question pane
                log.debug("Showing question pane for type {}", question.getClass().getSimpleName());
                this.centerPane = new QuestionPane(mainCtrl, communication, question);
            }
            mainBorderPane.setCenter(this.centerPane);
            updateInGame();
        } catch (IOException e) {
            log.error("Error loading the FXML file", e);
            Platform.exit();
            System.exit(0);
        }

        // Set the current question
        ClientState.currentQuestion = question;
    }
}
