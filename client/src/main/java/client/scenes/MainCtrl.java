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

import client.scenes.authentication.LogInScreenCtrl;
import client.scenes.authentication.NicknameScreenCtrl;
import client.scenes.authentication.RegisterScreenCtrl;
import client.scenes.authentication.ServerConnectScreenCtrl;
import client.scenes.lobby.LobbyScreenCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import lombok.Generated;

/**
 * Main controller for the client application.
 */
@Generated
public class MainCtrl {

    private Stage primaryStage;

    private ServerConnectScreenCtrl serverConnectScreenCtrl;
    private Parent serverConnectScreen;
    
    private LogInScreenCtrl logInScreenCtrl;
    private Parent logInScreen;

    private RegisterScreenCtrl registerScreenCtrl;
    private Parent registerScreen;

    private NicknameScreenCtrl nicknameScreenCtrl;
    private Parent nicknameScreen;

    private LobbyScreenCtrl lobbyScreenCtrl;
    private Parent lobbyScene;

    private GameScreenCtrl gameScreenCtrl;
    private Parent gameScreen;

    private Popup lobbySettingsPopUp;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage Primary stage of the application
     */
    public void initialize(Stage primaryStage,
                           Pair<ServerConnectScreenCtrl, Parent> serverConnectScreen,
                           Pair<LogInScreenCtrl, Parent> logInScreen,
                           Pair<RegisterScreenCtrl, Parent> registerScreen,
                           Pair<NicknameScreenCtrl, Parent> nicknameScreen,
                           Pair<LobbyScreenCtrl, Parent> lobbyScreen,
                           Pair<GameScreenCtrl, Parent> gameScreen) {
        this.primaryStage = primaryStage;

        this.serverConnectScreen = serverConnectScreen.getValue();
        this.serverConnectScreenCtrl = serverConnectScreen.getKey();

        this.logInScreen = logInScreen.getValue();
        this.logInScreenCtrl = logInScreen.getKey();

        this.registerScreen = registerScreen.getValue();
        this.registerScreenCtrl = registerScreen.getKey();

        this.nicknameScreen = nicknameScreen.getValue();
        this.nicknameScreenCtrl = nicknameScreen.getKey();

        this.lobbyScene = lobbyScreen.getValue();
        this.lobbyScreenCtrl = lobbyScreen.getKey();

        this.gameScreen = gameScreen.getValue();
        this.gameScreenCtrl = gameScreen.getKey();

        primaryStage.getIcons().add(new Image(getClass().getResource("/client/images/logo.png").toExternalForm()));

        lobbySettingsPopUp = new Popup();
        showServerConnectScreen();
    }

    public enum StageScalingStrategy {
        Identity,
        Stretch,
        Letterbox,
    }
    private void showScreenLetterBox(Parent parent, StageScalingStrategy strategy) {
        if(primaryStage.getScene()==null)
            primaryStage.setScene(new Scene(new Group(new StackPane(parent))));

        primaryStage.setTitle("Quizzzzz");
        var scene = primaryStage.getScene();
        var group = ((Group)scene.getRoot());
        var pane = (StackPane)group.getChildren().get(0);
        pane.getChildren().set(0,parent);
        StackPane.setAlignment(pane, Pos.CENTER_LEFT);
        primaryStage.setMinHeight(576);
        primaryStage.setMinWidth(1024);
        primaryStage.show();
        scaling(scene, pane, 1024,576, strategy);
    }

    /**
     * This function displays the register screen.
     * It also sets it min width and height
     */
    public void showServerConnectScreen() {
        this.showScreenLetterBox(serverConnectScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the log in screen.
     * It also sets it min width and height.
     */
    public void showLogInScreen() {
        this.showScreenLetterBox(logInScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the register screen.
     * It also sets it min width and height
     */
    public void showRegisterScreen() {
        this.showScreenLetterBox(registerScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the nickname selection screen.
     * It also sets it min width and height
     */
    public void showNicknameScreen() {
        this.showScreenLetterBox(nicknameScreen, StageScalingStrategy.Letterbox);
    }

    /**
     * Shows the lobby screen.
     */
    public void showLobbyScreen() {
        this.showScreenLetterBox(lobbyScene, StageScalingStrategy.Letterbox);
    }

    /**
     * This function displays the game screen.
     * It also sets it min width and height.
     */
    public void showGameScreen() {
        this.showScreenLetterBox(gameScreen, StageScalingStrategy.Identity);
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
     * @param config the config of the game.
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

    SceneSizeChangeListener sizeListener =
            null;

    private void scaling(final Scene scene,
                         final Pane contentPane, float initWidth, float initHeight, StageScalingStrategy strategy) {
        if(sizeListener!=null) {
            scene.widthProperty().removeListener(sizeListener);
            scene.heightProperty().removeListener(sizeListener);
        }

        final double ratio = initWidth / initHeight;

        sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane, strategy);

        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
    }

    private static class SceneSizeChangeListener implements ChangeListener<Number> {
        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;
        private final StageScalingStrategy strategy;

        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth,
                                       Pane contentPane, StageScalingStrategy strategy) {
            this.scene = scene;
            this.ratio = ratio;
            this.initHeight = initHeight;
            this.initWidth = initWidth;
            this.contentPane = contentPane;
            this.strategy = strategy;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            System.out.println(initHeight);
            final double newWidth = scene.getWidth();
            final double newHeight = scene.getHeight();

            switch(strategy) {
                case Identity:
                    contentPane.setPrefWidth(newWidth);
                    contentPane.setPrefHeight(newHeight);
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
            }
        }
    }
}