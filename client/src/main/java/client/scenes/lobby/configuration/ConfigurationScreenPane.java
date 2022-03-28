package client.scenes.lobby.configuration;

import commons.entities.game.configuration.GameConfigurationDTO;
import java.lang.reflect.Field;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The class that encompasses the ConfigurationScreen.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */
@Generated
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
            Platform.exit();
            System.exit(0);
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

    public void makeTransparent() {
        this.controller.makeTransparent();
    }

    public void hideSaveButton() {
        this.controller.hideSaveButton();
    }

    public void setFieldReadOnly(Field field) {
        controller.setFieldReadOnly(field);
    }

    public void setFieldEdit(Field field) {
        controller.setFieldEdit(field);
    }
}