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
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
    private Scene serverConnectScreen;
    
    private LogInScreenCtrl logInScreenCtrl;
    private Scene logInScreen;

    private RegisterScreenCtrl registerScreenCtrl;
    private Scene registerScreen;

    private NicknameScreenCtrl nicknameScreenCtrl;
    private Scene nicknameScreen;

    private LobbyScreenCtrl lobbyScreenCtrl;
    private Scene lobbyScene;

    private GameScreenCtrl gameScreenCtrl;
    private Scene gameScreen;

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

        this.serverConnectScreen = new Scene(serverConnectScreen.getValue());
        this.serverConnectScreenCtrl = serverConnectScreen.getKey();

        this.logInScreen = new Scene(logInScreen.getValue());
        this.logInScreenCtrl = logInScreen.getKey();

        this.registerScreen = new Scene(registerScreen.getValue());
        this.registerScreenCtrl = registerScreen.getKey();

        this.nicknameScreen = new Scene(nicknameScreen.getValue());
        this.nicknameScreenCtrl = nicknameScreen.getKey();
        
        this.lobbyScreenCtrl = lobbyScreen.getKey();
        this.lobbyScene = new Scene(lobbyScreen.getValue());

        this.gameScreen = new Scene(gameScreen.getValue());
        this.gameScreenCtrl = gameScreen.getKey();

        primaryStage.getIcons().add(new Image(getClass().getResource("/client/images/logo.png").toExternalForm()));

        lobbySettingsPopUp = new Popup();
        showServerConnectScreen();
        primaryStage.show();
    }


    /**
     * This function displays the register screen.
     * It also sets it min width and height
     */
    public void showServerConnectScreen() {
        primaryStage.setTitle("Server Connect Screen");
        primaryStage.setScene(serverConnectScreen);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * This function displays the log in screen.
     * It also sets it min width and height.
     */
    public void showLogInScreen() {
        primaryStage.setTitle("Log In Screen");
        primaryStage.setScene(logInScreen);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * This function displays the register screen.
     * It also sets it min width and height
     */
    public void showRegisterScreen() {
        primaryStage.setTitle("Register Screen");
        primaryStage.setScene(registerScreen);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * This function displays the nickname selection screen.
     * It also sets it min width and height
     */
    public void showNicknameScreen() {
        nicknameScreenCtrl.reset();
        primaryStage.setTitle("Nickname Screen");
        primaryStage.setScene(nicknameScreen);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * Shows the lobby screen.
     */
    public void showLobbyScreen() {
        primaryStage.setTitle(lobbyCtrl.getName());
        primaryStage.setScene(lobbyScene);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * This function displays the game screen.
     * It also sets it min width and height.
     */
    public void showGameScreen() {
        primaryStage.setTitle("Game Screen");
        primaryStage.setScene(gameScreen);
        primaryStage.sizeToScene();
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(500);
    }

    /**
     * This function returns the primary stage.
     *
     * @return stage that is shown
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

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
}