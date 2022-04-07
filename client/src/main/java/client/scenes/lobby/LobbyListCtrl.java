package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.LobbyListCommunication;
import client.communication.user.UserCommunication;
import client.scenes.MainCtrl;
import client.scenes.UserInfoPane;
import client.utils.AlgorithmicUtils;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import commons.entities.game.GameDTO;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * Lobby list controller. Controls the lobby list.
 */
@Generated
public class LobbyListCtrl implements Initializable {
    private final MainCtrl mainCtrl;
    private final LobbyListCommunication communication;

    @FXML private AnchorPane lobbyListAnchorPane;
    @FXML private JFXButton leaderboardButton;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton searchButton;
    @FXML private JFXButton fetchButton;
    @FXML private JFXButton createLobbyButton;
    @FXML private TextField searchField;
    @FXML private VBox lobbyListVbox;
    @FXML private JFXButton signOutButton;
    @FXML private JFXButton editButton;
    @FXML private JFXButton joinPrivateLobbyButton;
    @FXML private TextField privateLobbyTextField;
    @FXML private AnchorPane settingsPanel;
    @FXML private JFXButton volumeButton;
    @FXML private JFXSlider volumeSlider;
    @FXML private JFXToggleButton muteEveryoneToggleButton;
    @FXML private FontAwesomeIconView volumeIconView;

    private List<FontAwesomeIcon> volumeIconList;
    private UserInfoPane userInfo;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param communication Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyListCtrl(MainCtrl mainCtrl, LobbyListCommunication communication) {
        this.mainCtrl = mainCtrl;
        this.communication = communication;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Enables/ Disables the button according to the string size inside the textfield.
        this.privateLobbyTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.joinPrivateLobbyButton.setDisable(newValue.length() != 6);
        });
        setUpVolume();
    }

    @FXML
    private void leaderboardButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        mainCtrl.showGlobalLeaderboardScreen();
    }

    @FXML
    private void settingsButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (userInfo != null) {
            userInfo.setVisible(false);
        }
        settingsPanel.setVisible(!settingsPanel.isVisible());
    }

    @FXML
    private void volumeButtonClick(ActionEvent actionEvent) {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        SoundManager.volume.setValue(SoundManager.volume.getValue() == 0 ? 100 : 0);
    }

    private void setUpVolume() {

        // A list of icons so we can have a swift transition
        // between them when changing the volume
        volumeIconList = Arrays.asList(
                FontAwesomeIcon.VOLUME_OFF,
                FontAwesomeIcon.VOLUME_DOWN,
                FontAwesomeIcon.VOLUME_UP);

        // Bidirectional binding of the volume with the volume
        // property. This is to ensure we can report changes
        // instantly to the ui if the volume changes from
        // outside of our control.
        volumeSlider.valueProperty().bindBidirectional(SoundManager.volume);

        // a listener on the volume to change the icon
        // of the volume.
        SoundManager.volume.addListener((observable, oldValue, newValue) -> {

            // Sets the glyph name of the iconView directly
            volumeIconView.setGlyphName(volumeIconList.get(
                    Math.round(newValue.floatValue() / 100 * (volumeIconList.size() - 1))
            ).name());
        });
        SoundManager.everyoneMuted.bindBidirectional(muteEveryoneToggleButton.selectedProperty());
    }

    @FXML
    private void userButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        settingsPanel.setVisible(false);
        if (userInfo == null) {
            // Create userInfo
            userInfo = new UserInfoPane(new ServerUtils(), new UserCommunication(), mainCtrl);
            lobbyListAnchorPane.getChildren().add(userInfo);
            userInfo.setVisible(true);
            runLater(() -> userInfo.setupPosition(userButton, lobbyListAnchorPane));
        } else {
            // Toggle visibility
            userInfo.setVisibility(!userInfo.isVisible());
        }
    }

    @FXML
    private void createLobbyButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        createLobby();
    }

    private void createLobby() {
        mainCtrl.showLobbyCreationScreen();
    }

    @FXML
    private void searchButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        updateLobbyList(searchField.getText());
    }

    /**
     * This function resets the control to a default state.
     */
    public void reset() {
        updateLobbyList("");
        if (userInfo != null) {
            userInfo.setVisible(false);
        }
        this.searchField.setText("");
        setUpVolume();
    }

    private void updateLobbyList(String filter) {
        communication.getLobbies(
                games -> runLater(() -> {
                    lobbyListVbox.getChildren().clear();

                    Comparator<GameDTO> comparator = Comparator.comparing(gameDTO ->
                            AlgorithmicUtils.levenshteinDistance(filter, gameDTO.getGameName()));

                    var sortedLobbies = games.stream().sorted(comparator);

                    var generatedLobbies =
                            sortedLobbies.map(gameDTO ->
                                    new LobbyListItemPane(gameDTO, (id) ->
                                            communication.joinLobby(id,
                                                    gameDTO1 -> runLater(mainCtrl::showLobbyScreen),
                                                    () -> runLater(() ->
                                                            mainCtrl.showErrorSnackBar(
                                                                    "Something went wrong while joining the lobby."
                                                            ))))).collect(Collectors.toList());

                    lobbyListVbox.getChildren().addAll(generatedLobbies);
                }),
                () -> runLater(() -> mainCtrl.showErrorSnackBar("Something went wrong while fetching the lobbies.")));
    }

    /**
     * Function that lets the user join a random lobby.
     */
    @FXML
    private void joinRandomLobby() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        communication.getLobbies(
                games -> {
                    // Gets a random available lobby and joins it
                    games.removeIf(game -> (game.getConfiguration().getCapacity() <= game.getPlayers().size()));
                    if (games.isEmpty()) {
                        this.createLobby();
                    } else {
                        var game = games.get(new Random().nextInt(games.size()));
                        communication.joinLobby(game.getId(), gameDTO -> {
                            runLater(mainCtrl::showLobbyScreen);
                        }, () -> {
                            runLater(() -> {
                                mainCtrl.showErrorSnackBar("Couldn't join random game.");
                            });
                        });
                    }
                },
                () -> {
                    // If there are no available games, the user creates a new lobby
                    runLater(this::createLobby);
                });
    }

    @FXML
    private void fetchButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        updateLobbyList(searchField.getText());
    }

    @FXML
    private void joinPrivateLobbyButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        communication.joinPrivateLobby(privateLobbyTextField.getText(), gameDTO -> runLater(() -> {
            mainCtrl.showInformationalSnackBar("Joined the lobby!");
            mainCtrl.showLobbyScreen();
        }), (error) -> runLater(() -> mainCtrl.showErrorSnackBar("Error occurred: " + error.getDescription())));
    }
}
