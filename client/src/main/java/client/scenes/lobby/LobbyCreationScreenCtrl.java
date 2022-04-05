package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.LobbyListCommunication;
import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.scenes.lobby.configuration.ConfigurationScreenPane;
import client.utils.ClientState;
import client.utils.ReflectionUtils;
import client.utils.communication.*;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import commons.entities.messages.SSEMessageType;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import jdk.jfr.Description;


/**
 * The lobby creation screen controller. Controls the lobby creation screen.
 */
public class LobbyCreationScreenCtrl implements Initializable, SSESource {
    private final MainCtrl mainCtrl;
    private final LobbyListCommunication server;
    private final LobbyCommunication serverLobby;

    @FXML private FontAwesomeIconView lockButtonIconView;
    @FXML private JFXButton standardGameConfigurationButton;
    @FXML private JFXButton survivalGameConfigurationButton;
    @FXML private JFXButton singleplayerGameButton;
    @FXML private JFXButton multiplayerGameButton;
    @FXML private JFXButton createLobbyGameButton;

    @FXML private TextField lobbyNameField;
    @FXML private VBox configurationPanelVbox;
    @FXML private Label publicPrivateLabel;

    private SimpleBooleanProperty isPrivateProperty;
    private GameConfigurationDTO config;

    private ConfigurationScreenPane configPane;

    private SimpleBooleanProperty isMultiplayer;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyCreationScreenCtrl(LobbyListCommunication server, LobbyCommunication serverLobby, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.serverLobby = serverLobby;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // A bindable boolean property so we can automatically change the icon
        // and text of the private/public label and lock.
        isPrivateProperty = new SimpleBooleanProperty(false);

        // Changes the glyph name automatically (Locked/Unlocked).
        lockButtonIconView.glyphNameProperty().bind(
                Bindings.when(isPrivateProperty)
                        .then("LOCK")
                        .otherwise("UNLOCK"));

        // Changes the label name automatically.
        publicPrivateLabel.textProperty().bind(
                Bindings.when(isPrivateProperty)
                        .then("Private")
                        .otherwise("Public"));

        isPrivateProperty.set(false);

        isMultiplayer = new SimpleBooleanProperty(true);

        isMultiplayer.addListener((observable, oldValue, newValue) -> {
            setUpGameType();
        });
        isMultiplayer.setValue(true);
    }

    public void bindHandler(SSEHandler sseHandler) {
        sseHandler.initialize(this);
    }

    /**
     * Reacts to the game being started.
     *
     * @param preparationDuration Duration of preparation phase
     */
    @SSEEventHandler(SSEMessageType.GAME_START)
    public void startGame(Integer preparationDuration) {
        mainCtrl.showGameScreen(null);
        mainCtrl.getGameScreenCtrl().startTimer(Duration.ofMillis(preparationDuration));
        ClientState.previousScore = Optional.of(0);
    }

    /**
     * This function resets the controller to a predefined default state.
     */
    public void reset() {
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
        this.lobbyNameField.setText("");

        isPrivateProperty.set(false);
        isMultiplayer.set(true);
        config = new NormalGameConfigurationDTO();
        setUpConfigScreen();
    }

    private void setUpConfigScreen() {
        configurationPanelVbox.getChildren().clear();
        configPane = new ConfigurationScreenPane(config, config -> { });
        configPane.makeTransparent();
        configPane.hideSaveButton();
        configurationPanelVbox.getChildren().add(configPane);
    }

    /**
     * Sets up the game type.
     * Does this by recreating the config screen, setting the capacity field to read only or write
     * depending if the lobby is singleplayer or multiplayer and changes the buttons and text of the
     * create lobby button.
     */
    private void setUpGameType() {
        var capacityField = ReflectionUtils.getAnnotatedFields(config, Description.class).stream()
                .filter(field -> field.getName().equals("capacity")).findFirst().get();

        if (isMultiplayer.get()) {
            config.setCapacity(5);
            setUpConfigScreen();
            configPane.setFieldEdit(capacityField);
            singleplayerGameButton.setButtonType(JFXButton.ButtonType.FLAT);
            multiplayerGameButton.setButtonType(JFXButton.ButtonType.RAISED);
            createLobbyGameButton.setText("Create");
        } else {
            config.setCapacity(1);
            setUpConfigScreen();
            configPane.setFieldReadOnly(capacityField);
            singleplayerGameButton.setButtonType(JFXButton.ButtonType.RAISED);
            multiplayerGameButton.setButtonType(JFXButton.ButtonType.FLAT);
            createLobbyGameButton.setText("Start");
        }
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
        // Start SSE
        ServerUtils.sseHandler.subscribe();
        bindHandler(ServerUtils.sseHandler);

        // Create lobby
        server.createLobby(
            config,
            isPrivateProperty.get(),
            // Success
            game -> runLater(() -> {
                if (game.getStatus() == GameStatus.CREATED) {
                    mainCtrl.showLobbyScreen();
                }
            }),
            // Failure
            (error) -> runLater(() -> {
                ServerUtils.sseHandler.kill();
                mainCtrl.showErrorSnackBar(error == null ? "Failed to create the lobby" : error.getDescription());
            }));
    }

    @FXML
    private void standardGameConfigurationButtonClick() {
        config = new NormalGameConfigurationDTO();
        isMultiplayer.setValue(isMultiplayer.getValue());
        setUpConfigScreen();
        setUpGameType();
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
    }

    @FXML
    private void survivalGameConfigurationButtonClick() {
        config = new SurvivalGameConfigurationDTO();
        isMultiplayer.setValue(isMultiplayer.getValue());
        setUpConfigScreen();
        setUpGameType();
        standardGameConfigurationButton.setButtonType(JFXButton.ButtonType.FLAT);
        survivalGameConfigurationButton.setButtonType(JFXButton.ButtonType.RAISED);
    }

    @FXML
    private void singleplayerGameButtonClick() {
        isMultiplayer.setValue(false);
    }

    @FXML
    private void multiplayerGameButtonClick() {
        isMultiplayer.setValue(true);
    }
}
