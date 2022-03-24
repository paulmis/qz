package client.scenes.lobby;

import client.scenes.MainCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LobbyCreationScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;


    @FXML private FontAwesomeIconView lockButtonIconView;
    @FXML private JFXButton standardGameConfigurationButton;
    @FXML private JFXButton survivalGameConfigurationButton;
    @FXML private TextField lobbyNameField;
    @FXML private VBox configurationPanelVbox;

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
        isPrivateProperty.set(false);
    }

    public void reset() {
        //isPrivateProperty.set(false);
        //setUpConfigScreen();
    }

    private void setUpConfigScreen() {
        configurationPanelVbox.getChildren().clear();
        configurationPanelVbox.getChildren().add(new ConfigurationScreenPane(config, new ConfigurationScreenCtrl.SaveHandler() {
            @Override
            public void handle(GameConfigurationDTO config) {

            }
        }));
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

    }

    @FXML
    private void standardGameConfigurationButtonClick() {
        config = new NormalGameConfigurationDTO(null, 60, 1, 20, 3, 2f, 100, 0, 75);
        setUpConfigScreen();
    }

    @FXML
    private void survivalGameConfigurationButtonClick() {

    }
}
