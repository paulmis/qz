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
import client.scenes.authentication.RegisterScreenCtrl;
import client.scenes.authentication.ServerConnectScreenCtrl;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.Generated;

/**
 * Main controller for the client application.
 */
@Generated
public class MainCtrl {

    private Stage primaryStage;

    private GameScreenCtrl gameScreenCtrl;
    private Scene gameScreen;
    private LobbyCtrl lobbyCtrl;
    private Scene lobbyScene;


    private RegisterScreenCtrl registerScreenCtrl;
    private Scene registerScreen;

    private ServerConnectScreenCtrl serverConnectScreenCtrl;
    private Scene serverConnectScreen;

    private LogInScreenCtrl logInScreenCtrl;
    private Scene logInScreen;

    /**
     * Initialize the main controller.
     *
     * @param primaryStage Primary stage of the application
     */
    public void initialize(Stage primaryStage,
                           Pair<ServerConnectScreenCtrl, Parent> serverConnectScreen,
                           Pair<LogInScreenCtrl, Parent> logInScreen,
                           Pair<RegisterScreenCtrl, Parent> registerScreen,
                           Pair<LobbyCtrl, Parent> lobbyScreen,
                           Pair<GameScreenCtrl, Parent> gameScreen) {
        this.primaryStage = primaryStage;

        this.lobbyCtrl = lobbyScreen.getKey();
        this.lobbyScene = new Scene(lobbyScreen.getValue());

        this.logInScreen = new Scene(logInScreen.getValue());
        this.logInScreenCtrl = logInScreen.getKey();

        this.registerScreen = new Scene(registerScreen.getValue());
        this.registerScreenCtrl = registerScreen.getKey();

        this.serverConnectScreen = new Scene(serverConnectScreen.getValue());
        this.serverConnectScreenCtrl = serverConnectScreen.getKey();

        this.gameScreen = new Scene(gameScreen.getValue());
        this.gameScreenCtrl = gameScreen.getKey();

        primaryStage.getIcons().add(new Image(getClass().getResource("/client/images/logo.png").toExternalForm()));
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
}