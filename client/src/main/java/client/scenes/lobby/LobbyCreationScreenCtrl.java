package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


/**
 * The lobby creation screen controller. Controls the lobby creation screen.
 */
public class LobbyCreationScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @FXML private FontAwesomeIconView lockButtonIconView;
    @FXML private JFXButton standardGameConfigurationButton;
    @FXML private JFXButton survivalGameConfigurationButton;
    @FXML private TextField lobbyNameField;
    @FXML private VBox configurationPanelVbox;
    @FXML private Label publicPrivateLabel;

    private SimpleBooleanProperty isPrivateProperty;
    private GameConfigurationDTO config;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyCreationScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isPrivateProperty = new SimpleBooleanProperty(false);
        lockButtonIconView.glyphNameProperty().bind(
                Bindings.when(isPrivateProperty)
                        .then("LOCK")
                        .otherwise("UNLOCK"));

        publicPrivateLabel.textProperty().bind(
                Bindings.when(isPrivateProperty)
                        .then("Private")
                        .otherwise("Public"));

        isPrivateProperty.set(false);
    }

    /**
     * This function resets the controller to a predefined default state.
     */
    public void reset() {
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
        this.lobbyNameField.setText("");
        isPrivateProperty.set(false);
        config = new NormalGameConfigurationDTO(null, 60, 1, 20, 3, 2f, 100, 0, 75);
        setUpConfigScreen();
    }

    private void setUpConfigScreen() {
        configurationPanelVbox.getChildren().clear();
        var configPane = new ConfigurationScreenPane(config, config -> { });

        configPane.makeTransparent();
        configPane.hideSaveButton();
        configurationPanelVbox.getChildren().add(configPane);
    }

    @FXML
    private void goBackToLobbies() {
        mainCtrl.showLobbyListScreen();
    }

    @FXML
    private void lockButtonClick() {
        isPrivateProperty.set(!isPrivateProperty.get());
    }

    @FXML
    private void createLobbyButtonClick() {
        server.createLobby(config, game -> runLater(mainCtrl::showLobbyScreen),
                () -> runLater(() -> mainCtrl.showErrorSnackBar("Something went wrong while creating the new lobby.")));
    }

    @FXML
    private void standardGameConfigurationButtonClick() {
        config = new NormalGameConfigurationDTO(null, 60, 1, 20, 3, 2f, 100, 0, 75);
        setUpConfigScreen();
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    private void survivalGameConfigurationButtonClick() {
        config = new SurvivalGameConfigurationDTO(null, 60, 1, 1f, 3, 2f, 100, 0, 75);
        setUpConfigScreen();
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
    }
}
