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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Main controller for the client application.
 */
public class MainCtrl {

    private Stage primaryStage;

    private GameScreenCtrl gameScreenCtrl;
    private Scene gameScreen;


    /** Initialize the main controller.
     *
     * @param primaryStage Primary stage of the application
     */
    public void initialize(Stage primaryStage, Pair<GameScreenCtrl, Parent> gameScreen) {
        this.primaryStage = primaryStage;

        this.gameScreenCtrl = gameScreen.getKey();
        this.gameScreen = new Scene(gameScreen.getValue());

        showGameScreen();
        primaryStage.show();
    }

    public void showGameScreen() {
        primaryStage.setTitle("Game Screen");
        primaryStage.setScene(gameScreen);
    }
}