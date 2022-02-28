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

package client;

import static com.google.inject.Guice.createInjector;

import client.scenes.GameScreenCtrl;
import client.scenes.LobbyCtrl;
import client.scenes.MainCtrl;
import client.scenes.authentication.LogInScreenCtrl;
import client.scenes.authentication.RegisterScreenCtrl;
import client.scenes.authentication.ServerConnectScreenCtrl;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Generated;

/**
 * Main class for the client application.
 */
@Generated
public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {

        var gameScreen = FXML.load(GameScreenCtrl.class,
                "client", "scenes", "GameScreen.fxml");
        var registerScreen = FXML.load(RegisterScreenCtrl.class,
                "client", "scenes", "authentication", "RegisterScreen.fxml");
        var serverConnectScreen = FXML.load(ServerConnectScreenCtrl.class,
                "client", "scenes", "authentication", "ServerConnectScreen.fxml");
        var logInScreen = FXML.load(LogInScreenCtrl.class,
                "client", "scenes", "authentication", "LogInScreen.fxml");
        var lobbyScreen = FXML.load(LobbyCtrl.class,
                "client", "scenes", "LobbyScreen.fxml");

        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, serverConnectScreen, logInScreen, registerScreen, lobbyScreen, gameScreen);
    }
}