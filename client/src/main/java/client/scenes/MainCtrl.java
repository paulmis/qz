/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.scenes;

import client.scenes.admin.ActivityListScreenCtrl;
import client.scenes.authentication.LogInScreenCtrl;
import client.scenes.authentication.RegisterScreenCtrl;
import client.scenes.authentication.ServerConnectScreenCtrl;
import client.scenes.game.GameScreenCtrl;
import client.scenes.leaderboard.GlobalLeaderboardCtrl;
import client.scenes.lobby.*;
import client.scenes.lobby.configuration.ConfigurationScreenCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.questions.QuestionDTO;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Main controller for the client application.
 */
@Slf4j
@Generated
public class MainCtrl {

    /**
     * The size listener for the scene.
     * We keep a reference to it to be
     * able to remove it when needed.
     */
    SceneSizeChangeListener sizeListener =
            null;
    private Stage primaryStage;
    private ServerConnectScreenCtrl serverConnectScreenCtrl;
    private Parent serverConnectScreen;
    private LogInScreenCtrl logInScreenCtrl;
    private Parent logInScreen;
    private RegisterScreenCtrl registerScreenCtrl;
    private Parent registerScreen;
    private LobbyScreenCtrl lobbyScreenCtrl;
    private Parent lobbyScene;
    @Getter
    private GameScreenCtrl gameScreenCtrl;
    private Parent gameScreen;
    private GlobalLeaderboardCtrl globalLeaderboardCtrl;
    private Parent globalLeaderboardScreen;
    private LobbyListCtrl lobbyListCtrl;
    private Parent lobbyListScreen;
    private LobbyCreationScreenCtrl lobbyCreationScreenCtrl;
    private Parent lobbyCreationScreen;
    private ActivityListScreenCtrl activityListScreenCtrl;
    private Parent activityListScreen;
    private Popup lobbySettingsPopUp;
    private Popup lobbyLeavePopUp;
    private Popup gameLeavePopUp;
    private Popup lobbyDisbandPopUp;
    private Popup startGamePopUp;
    private Parent activeScreen;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage Primary stage of the application
     */
    public void initialize(Stage primaryStage,
                           Pair<ServerConnectScreenCtrl, Parent> serverConnectScreen,
                           Pair<LogInScreenCtrl, Parent> logInScreen,
                           Pair<RegisterScreenCtrl, Parent> registerScreen,
                           Pair<LobbyScreenCtrl, Parent> lobbyScreen,
                           Pair<GameScreenCtrl, Parent> gameScreen,
                           Pair<GlobalLeaderboardCtrl, Parent> globalLeaderboardScreen,
                           Pair<LobbyListCtrl, Parent> lobbyListScreen,
                           Pair<LobbyCreationScreenCtrl, Parent> lobbyCreationScreen,
                           Pair<ActivityListScreenCtrl, Parent> activityListScreen) {
        this.primaryStage = primaryStage;

        this.serverConnectScreen = serverConnectScreen.getValue();
        this.serverConnectScreenCtrl = serverConnectScreen.getKey();

        this.logInScreen = logInScreen.getValue();
        this.logInScreenCtrl = logInScreen.getKey();

        this.registerScreen = registerScreen.getValue();
        this.registerScreenCtrl = registerScreen.getKey();

        this.lobbyScene = lobbyScreen.getValue();
        this.lobbyScreenCtrl = lobbyScreen.getKey();

        this.gameScreen = gameScreen.getValue();
        this.gameScreenCtrl = gameScreen.getKey();

        this.globalLeaderboardScreen = globalLeaderboardScreen.getValue();
        this.globalLeaderboardCtrl = globalLeaderboardScreen.getKey();

        this.lobbyListScreen = lobbyListScreen.getValue();
        this.lobbyListCtrl = lobbyListScreen.getKey();

        this.lobbyCreationScreen = lobbyCreationScreen.getValue();
        this.lobbyCreationScreenCtrl = lobbyCreationScreen.getKey();

        this.activityListScreen = activityListScreen.getValue();
        this.activityListScreenCtrl = activityListScreen.getKey();

        primaryStage.getIcons().add(new Image(getClass().getResource("/client/images/logo.png").toExternalForm()));

        lobbySettingsPopUp = new Popup();
        lobbyLeavePopUp = new Popup();
        gameLeavePopUp = new Popup();
        lobbyDisbandPopUp = new Popup();
        startGamePopUp = new Popup();
        showServerConnectScreen();
    }

    /**
     * Setup that is ran after the client has connected to the server.
     */
    public void setup() {
        gameScreenCtrl.setup();
    }

    private void showScreenLetterBox(Parent parent, StageScalingStrategy strategy) {
        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(new Group(new StackPane(parent))));
        }
        activeScreen = parent;

        primaryStage.setTitle("Quizzzzz");
        var scene = primaryStage.getScene();
        var topGroup = ((Group) scene.getRoot());

        switch (strategy) {
            case Identity:
            case Fixed:
            case ScaledIdentity:
                var anchor = new AnchorPane(parent);
                topGroup.getChildren().set(0, new Group(anchor));

                AnchorPane.setBottomAnchor(parent, 0d);
                AnchorPane.setTopAnchor(parent, 0d);
                AnchorPane.setLeftAnchor(parent, 0d);
                AnchorPane.setRightAnchor(parent, 0d);

                primaryStage.show();
                scaling(scene, anchor, 1024, 576, strategy);
                break;
            case Stretch:
            case ForcedScaled:
            case Letterbox:
                var pane = new StackPane(parent);
                topGroup.getChildren().set(0, new Group(pane));

                primaryStage.show();
                scaling(scene, pane, 1024, 576, strategy);
                break;
            default:
                break;
        }

    }

    /**
     * Displays the server connect screen.
     */
    public void showServerConnectScreen() {
        this.showScreenLetterBox(serverConnectScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * Displays the global leaderboard screen.
     */
    public void showGlobalLeaderboardScreen() {
        this.showScreenLetterBox(globalLeaderboardScreen, StageScalingStrategy.Letterbox);
        globalLeaderboardCtrl.reset();
    }

    /**
     * Displays the lobby list screen.
     */
    public void showLobbyListScreen() {
        ClientState.game = null;
        this.showScreenLetterBox(lobbyListScreen, StageScalingStrategy.Letterbox);
        lobbyListCtrl.reset();
    }

    /**
     * This function displays the lobby creation screen.
     */
    public void showLobbyCreationScreen() {
        this.showScreenLetterBox(lobbyCreationScreen, StageScalingStrategy.Letterbox);
        lobbyCreationScreenCtrl.reset();
    }


    /**
     * This function displays the log in screen.
     */
    public void showLogInScreen() {
        this.showScreenLetterBox(logInScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the register screen.
     */
    public void showRegisterScreen() {
        this.registerScreenCtrl.reset();
        this.showScreenLetterBox(registerScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * Shows the lobby screen.
     */
    public void showLobbyScreen() {
        this.lobbyScreenCtrl.reset();
        this.checkHost();
        lobbyScreenCtrl.bindHandler(ServerUtils.sseHandler);
        this.showScreenLetterBox(lobbyScene, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the game screen.
     */
    public void showGameScreen(QuestionDTO question) {
        gameScreenCtrl.setQuestion(question);
        gameScreenCtrl.bindHandler(ServerUtils.sseHandler);
        this.showScreenLetterBox(gameScreen, StageScalingStrategy.Letterbox);
        gameScreenCtrl.reset();
    }

    /**
     * This function displays the game screen.
     */
    public void showActivityListScreen() {
        this.showScreenLetterBox(activityListScreen, StageScalingStrategy.Letterbox);
        activityListScreenCtrl.reset();
    }

    /**
     * This function returns the primary stage.
     *
     * @return stage that is shown
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * This function opens a popup with
     * the game config settings.
     *
     * @param config      the config of the game.
     * @param saveHandler the action that is to be performed on config save.
     */
    public void openLobbySettings(GameConfigurationDTO config, ConfigurationScreenCtrl.SaveHandler saveHandler) {
        lobbySettingsPopUp = new Popup();
        lobbySettingsPopUp.setOnShown(e -> {
            lobbySettingsPopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - lobbySettingsPopUp.getWidth() / 2);

            lobbySettingsPopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - lobbySettingsPopUp.getHeight() / 2);
        });

        lobbySettingsPopUp.setAutoFix(true);
        lobbySettingsPopUp.setAutoHide(true);
        lobbySettingsPopUp.setHideOnEscape(true);

        var configPane = new ConfigurationScreenPane(config, saveHandler);

        configPane.setPrefWidth(primaryStage.getWidth() / 2);
        configPane.setPrefHeight(primaryStage.getHeight() / 2);

        lobbySettingsPopUp.getContent().add(configPane);
        lobbySettingsPopUp.show(primaryStage);
    }

    /**
     * This function closes the lobby settings popUp.
     */
    public void closeLobbySettings() {
        lobbySettingsPopUp.hide();
    }

    /**
     * This function opens a popup with
     * a leave warning for leaving the lobby.
     *
     * @param leaveHandler  the action that is to be performed when the user leaves the lobby.
     * @param cancelHandler the action that is to be performed when the user cancels leaving the lobby.
     */
    public void openLobbyLeaveWarning(LobbyLeaveScreenCtrl.LeaveHandler leaveHandler,
                                      LobbyLeaveScreenCtrl.CancelHandler cancelHandler) {
        lobbyLeavePopUp.setOnShown(e -> {
            lobbyLeavePopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - lobbyLeavePopUp.getWidth() / 2);

            lobbyLeavePopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - lobbyLeavePopUp.getHeight() / 2);
        });

        var lobbyLeavePane = new PopupPane(new LobbyLeaveScreenCtrl(leaveHandler, cancelHandler),
                "/lobby/LobbyLeaveScreen");
        lobbyLeavePopUp.getContent().add(lobbyLeavePane);
        lobbyLeavePopUp.show(primaryStage);
    }

    /**
     * This function closes the lobby leave popUp.
     */
    public void closeLobbyLeaveWarning() {
        lobbyLeavePopUp.hide();
        lobbyLeavePopUp.getContent().clear();
    }

    /**
     * This function opens a popup with
     * a leave warning for leaving the game.
     *
     * @param leaveHandler  the action that is to be performed when the user leaves the game.
     * @param cancelHandler the action that is to be performed when the user cancels leaving the lobby.
     */
    public void openGameLeaveWarning(GameLeaveScreenCtrl.LeaveHandler leaveHandler,
                                     GameLeaveScreenCtrl.CancelHandler cancelHandler) {
        gameLeavePopUp.setOnShown(e -> {
            gameLeavePopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - gameLeavePopUp.getWidth() / 2);

            gameLeavePopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - gameLeavePopUp.getHeight() / 2);
        });

        var gameLeavePane = new PopupPane(new GameLeaveScreenCtrl(leaveHandler, cancelHandler),
                "GameLeaveScreen");
        gameLeavePopUp.getContent().add(gameLeavePane);
        gameLeavePopUp.show(primaryStage);
    }

    /**
     * This function closes the game leave popUp.
     */
    public void closeGameLeaveWarning() {
        gameLeavePopUp.hide();
        gameLeavePopUp.getContent().clear();
    }

    /**
     * This function opens a popup with
     * a disband warning for disbanding the lobby.
     *
     * @param disbandHandler the action that is to be performed when the host disbands the lobby.
     * @param cancelHandler  the action that is to be performed when the host cancels disbanding the lobby.
     */
    public void openLobbyDisbandWarning(LobbyDisbandScreenCtrl.DisbandHandler disbandHandler,
                                        LobbyDisbandScreenCtrl.CancelHandler cancelHandler) {
        lobbyDisbandPopUp.setOnShown(e -> {
            lobbyDisbandPopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - lobbyDisbandPopUp.getWidth() / 2);

            lobbyDisbandPopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - lobbyDisbandPopUp.getHeight() / 2);
        });

        var lobbyDisbandPane = new PopupPane(new LobbyDisbandScreenCtrl(disbandHandler, cancelHandler),
                "/lobby/LobbyDisbandScreen");
        lobbyDisbandPopUp.getContent().add(lobbyDisbandPane);
        lobbyDisbandPopUp.show(primaryStage);
    }

    /**
     * This function closes the lobby disband popUp.
     */
    public void closeLobbyDisbandWarning() {
        lobbyDisbandPopUp.hide();
        lobbyDisbandPopUp.getContent().clear();
    }

    /**
     * This function opens a popup with
     * a start warning for starting the game.
     *
     * @param startHandler the action that is to be performed when the host starts the game.
     * @param cancelHandler  the action that is to be performed when the host cancels starting the game.
     */
    public void openStartGameWarning(GameStartScreenCtrl.StartHandler startHandler,
                                        GameStartScreenCtrl.CancelHandler cancelHandler) {
        startGamePopUp.setOnShown(e -> {
            startGamePopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - startGamePopUp.getWidth() / 2);

            startGamePopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - startGamePopUp.getHeight() / 2);
        });

        var startGamePane = new PopupPane(new GameStartScreenCtrl(startHandler, cancelHandler),
                "/lobby/GameStartScreen");
        startGamePopUp.getContent().add(startGamePane);
        startGamePopUp.show(primaryStage);
    }

    /**
     * This function closes the start game popUp.
     */
    public void closeStartGameWarning() {
        startGamePopUp.hide();
        startGamePopUp.getContent().clear();
    }

    /**
     * Open warning pop-up before closing the application.
     */
    public void openAppCloseWarning() {
        gameLeavePopUp.setOnShown(e -> {
            gameLeavePopUp.setX(primaryStage.getX() + primaryStage.getWidth() / 2
                    - gameLeavePopUp.getWidth() / 2);

            gameLeavePopUp.setY(primaryStage.getY() + primaryStage.getHeight() / 2
                    - gameLeavePopUp.getHeight() / 2);
        });

        var gameLeavePane = new PopupPane(new GameLeaveScreenCtrl(
                () -> Platform.runLater(() -> {
                    log.info("Exit requested, closing application.");
                    Platform.exit();
                    System.exit(0);
                }),
                () -> Platform.runLater(() -> {
                    closeAppCloseWarning();
                })),
                "GameLeaveScreen");
        gameLeavePopUp.getContent().add(gameLeavePane);
        gameLeavePopUp.show(primaryStage);
    }

    /**
     * Close the application leave pop-up.
     */
    public void closeAppCloseWarning() {
        gameLeavePopUp.hide();
        gameLeavePopUp.getContent().clear();
    }

    /**
     * This function checks if the player is the host of the lobby.
     */
    public void checkHost() {
        //Request user's data
        Optional<GamePlayerDTO> gamePlayerData = ClientState.game.getPlayers()
                .stream()
                .filter(gp -> ClientState.user.getId().equals(gp.getUserId()))
                .findAny();
        //Check if game player data is empty
        if (gamePlayerData.isPresent()) {
            // Compare gamePlayer id with lobby host id to check if player is host
            if (gamePlayerData.get().getId().equals(ClientState.game.getHost())) {
                log.debug("Player is host");
                this.showDisbandButton();
            } else {
                log.debug("Player is not host");
                this.hideDisbandButton();
            }
        } else {
            log.error("Couldn't retrieve game player/User is not a game player");
            this.hideDisbandButton();
        }
    }

    public void showDisbandButton() {
        lobbyScreenCtrl.getDisbandButton().setVisible(true);
    }

    public void hideDisbandButton() {
        lobbyScreenCtrl.getDisbandButton().setVisible(false);
    }

    /**
     * Displays an error snackBar to the user.
     *
     * @param message The text message that will be displayed in the snackBar.
     */
    public void showErrorSnackBar(String message) {
        showErrorSnackBar(message, Duration.seconds(3));
    }

    /**
     * Displays an error snackBar to the user.
     *
     * @param message  The text message that will be displayed in the snackBar.
     * @param duration The duration of the snackBar.
     */
    public void showErrorSnackBar(String message, Duration duration) {
        JFXSnackbar snack = new JFXSnackbar();
        snack.setStyle("-fx-text-fill: red");
        snack.registerSnackbarContainer((Pane) activeScreen);
        snack.fireEvent(new JFXSnackbar.SnackbarEvent(
                new JFXSnackbarLayout(message),
                duration, new PseudoClass() {
                    @Override
                    public String getPseudoClassName() {
                        return "error";
                    }
                }));
        snack.toFront();
        snack.setViewOrder(-1);
    }

    /**
     * Displays an informational snackBar to the user.
     *
     * @param message The text message that will be displayed in the snackBar.
     */
    public void showInformationalSnackBar(String message) {
        showInformationalSnackBar(message, Duration.seconds(3));
    }

    /**
     * Displays an informational snackBar to the user.
     *
     * @param message  The text message that will be displayed in the snackBar.
     * @param duration The duration of the snackBar.
     */
    public void showInformationalSnackBar(String message, Duration duration) {
        JFXSnackbar snack = new JFXSnackbar();
        snack.registerSnackbarContainer((Pane) activeScreen);
        snack.enqueue(new JFXSnackbar.SnackbarEvent(
                new JFXSnackbarLayout(message),
                duration, null));
        snack.toFront();
        snack.setViewOrder(-1);
    }

    /**
     * This function sets the scaling of a scene using the provided strategy.
     *
     * @param scene       The scene which is inside the stage.
     * @param contentPane The pane that contains everything relevant in the screen.
     * @param initWidth   The initial width the scene had.
     * @param initHeight  The initial height the scene had.
     * @param strategy    The resize strategy provided.
     */
    private void scaling(final Scene scene,
                         final Pane contentPane, float initWidth, float initHeight, StageScalingStrategy strategy) {

        // We remove the listeners, so we don't have conflicts.
        if (sizeListener != null) {
            scene.widthProperty().removeListener(sizeListener);
            scene.heightProperty().removeListener(sizeListener);
        }

        double ratio = initWidth / initHeight;

        // Creates the new listener
        sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane, strategy);

        // Adds the listener to width and height
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);

        // Unbinds the stage minWidth and minHeight
        // This is done to reverse effects of ForcedScaled strategy
        primaryStage.minWidthProperty().unbind();
        primaryStage.minHeightProperty().unbind();

        // Sets the minwidth and minheight to initial size this is done to reset
        // the effects of ForcedScaled
        primaryStage.setMinWidth(initWidth);
        primaryStage.setMinHeight(initHeight);

        // This is done to reset the effects of Fixed strategy.
        primaryStage.setResizable(true);

        switch (strategy) {
            case ForcedScaled:
                // We add the listeners in order to force a certain ratio for the screen.
                primaryStage.minWidthProperty().bind(scene.heightProperty().multiply(ratio));
                primaryStage.minHeightProperty().bind(scene.widthProperty().divide(ratio));
                break;
            case Fixed:
                primaryStage.setResizable(false);
                break;
            default:
                break;

        }
    }

    enum StageScalingStrategy {
        /**
         * Does absolutely nothing.
         * Should be used only when a screen is really good at resizing.
         */
        Identity,
        /**
         * Stretches the UI to fit the window.
         * Very situational. Looks ugly.
         */
        Stretch,
        /**
         * Scales the UI by the ratio and adds some
         * white borders where it couldn't fit.
         * One of the better options. The design will always scale good.
         */
        Letterbox,
        /**
         * This is the same as Identity but it also scales the UI
         * as much as it can.
         * Should be used when a screen resizes good enough but text and other
         * stuff should be also big.
         */
        ScaledIdentity,
        /**
         * This forces a ratio on the user window.
         * Pretty cool but buggy (Try resizing down the window, there doesn't seem to actually be a solution to this)
         */
        ForcedScaled,
        /**
         * Fixes the window size.
         */
        Fixed,
    }

    /**
     * This class is our change listener for
     * automatic scene scaling.
     */
    private static class SceneSizeChangeListener implements ChangeListener<Number> {

        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;
        private final StageScalingStrategy strategy;

        /**
         * Constructor for the listener.
         *
         * @param scene       The scene which properties we listen on.
         * @param ratio       The initial ratio.
         * @param initHeight  The initial width.
         * @param initWidth   The initial height.
         * @param contentPane The pane which contains the important content of the scene.
         * @param strategy    The resizing strategy we will use.
         */
        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth,
                                       Pane contentPane, StageScalingStrategy strategy) {
            this.scene = scene;
            this.ratio = ratio;
            this.initHeight = initHeight;
            this.initWidth = initWidth;
            this.contentPane = contentPane;
            this.strategy = strategy;
            changed(null, scene.getWidth(), scene.getWidth());
        }

        /**
         * This is the change handler.
         *
         * @param observableValue The bindable property.
         * @param oldValue        The old value of the property.
         * @param newValue        The new value of the property.
         */
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {

            // We get the new size of the scene.
            final double newWidth = scene.getWidth();
            final double newHeight = scene.getHeight();

            switch (strategy) {
                case Fixed:
                case Identity:
                    // This just resizes the content pane to the scene size.
                    contentPane.setMinWidth(newWidth);
                    contentPane.setMinHeight(newHeight);
                    break;
                case Stretch:
                    Scale scaling = new Scale(newWidth / initWidth, newHeight / initHeight);
                    scaling.setPivotX(0);
                    scaling.setPivotY(0);
                    scene.getRoot().getTransforms().setAll(scaling);
                    contentPane.setPrefWidth(newWidth / (newWidth / initWidth));
                    contentPane.setPrefHeight(newHeight / (newHeight / initHeight));
                    break;
                case Letterbox:
                case ForcedScaled:
                case ScaledIdentity:
                    double scaleFactor =
                            newWidth / newHeight > ratio
                                    ? newHeight / initHeight
                                    : newWidth / initWidth;
                    Scale letterScale = new Scale(scaleFactor, scaleFactor);
                    letterScale.setPivotX(0);
                    letterScale.setPivotY(0);
                    scene.getRoot().getTransforms().setAll(letterScale);
                    contentPane.setPrefWidth(newWidth / scaleFactor);
                    contentPane.setPrefHeight(newHeight / scaleFactor);
                    break;
                default:
                    break;
            }
        }
    }
}
