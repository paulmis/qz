package client.scenes.lobby.configuration;

import client.scenes.questions.EstimateQuestionCtrl;
import commons.entities.game.configuration.GameConfigurationDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * The class that encompasses the ConfigurationScreen.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */
public class ConfigurationScreenPane extends StackPane {

    private Node view;
    private ConfigurationScreenCtrl controller;

    private void setUpScreen(ConfigurationScreenCtrl ctrl) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/configuration/ConfigurationScreen.fxml"));

        fxmlLoader.setControllerFactory(param ->
                controller = ctrl);

        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        getChildren().add(view);
    }

    /**
     * This constructor is used when the
     * config is supposed to be readOnly.
     *
     * @param gameConfig the config of the game.
     */
    public ConfigurationScreenPane(GameConfigurationDTO gameConfig) {
        setUpScreen(new ConfigurationScreenCtrl(gameConfig));
    }

    /**
     * This constructor is used when the
     * user has editing privileges over the config.
     *
     * @param gameConfig the config of the game.
     * @param saveHandler the action that is to be performed on a save.
     */
    public ConfigurationScreenPane(GameConfigurationDTO gameConfig, ConfigurationScreenCtrl.SaveHandler saveHandler) {
        setUpScreen(new ConfigurationScreenCtrl(gameConfig, saveHandler));
    }
}