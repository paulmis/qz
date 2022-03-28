package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.communication.game.LobbyCommunication;
import client.scenes.MainCtrl;
import client.scenes.UserInfoPane;
import client.utils.AlgorithmicUtils;
import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
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
    private final LobbyListCommunication server;
    private final MainCtrl mainCtrl;

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
    private UserInfoPane userInfo;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyListCtrl(ServerUtils server, MainCtrl mainCtrl, LobbyCommunication communication) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void leaderboardButtonClick() {
        mainCtrl.showGlobalLeaderboardScreen();
    }

    @FXML
    private void settingsButtonClick() {
        System.out.println("Settings");
    }

    @FXML
    private void userButtonClick() {
        if (userInfo == null) {
            // Create userInfo
            userInfo = new UserInfoPane(server, mainCtrl);
            lobbyListAnchorPane.getChildren().add(userInfo);
            userInfo.setVisible(true);
            runLater(() -> userInfo.setupPosition(userButton, lobbyListAnchorPane));
        } else {
            // Toggle visibility
            userInfo.setVisible(!userInfo.isVisible());
        }
    }

    @FXML
    private void createLobbyButtonClick() {
        server.createLobby(game -> {
            ServerUtils.sseHandler.subscribe();
            runLater(mainCtrl::showLobbyScreen);
        }, () -> runLater(() ->
                mainCtrl.showErrorSnackBar("Something went wrong while creating the new lobby.")));
        mainCtrl.showLobbyCreationScreen();
    }

    @FXML
    private void signOutButtonClick() {
        ClientState.user = null;
        mainCtrl.showServerConnectScreen();
    }

    @FXML
    private void editButtonClick() {
        this.usernameField.setEditable(!this.usernameField.isEditable());
    }

    @FXML
    private void searchButtonClick() {
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
    }

    private void updateLobbyList(String filter) {
        server.getLobbies(
                games -> runLater(() -> {
                    lobbyListVbox.getChildren().clear();

                    Comparator<GameDTO> comparator = Comparator.comparing(gameDTO ->
                            AlgorithmicUtils.levenshteinDistance(filter, createSearchableString(gameDTO)));

                    var sortedLobbies = games.stream().sorted(comparator);

                    var generatedLobbies =
                            sortedLobbies.map(gameDTO ->
                                    new LobbyListItemPane(gameDTO, (id) ->
                                            server.joinLobby(id,
                                                    gameDTO1 -> runLater(mainCtrl::showLobbyScreen),
                                                    () -> runLater(() ->
                                                            mainCtrl.showErrorSnackBar(
                                                                    "Something went wrong while joining the lobby."
                                                            ))))).collect(Collectors.toList());

                    lobbyListVbox.getChildren().addAll(generatedLobbies);
                }),
                () -> runLater(() -> mainCtrl.showErrorSnackBar("Something went wrong while fetching the lobbies.")));
    }

    private String createSearchableString(GameDTO game) {
        return game.getPlayers().stream().filter(gamePlayerDTO -> gamePlayerDTO.getId().equals(game.getHost()))
                .findFirst().get().getNickname();
    }

    @FXML
    private void fetchButtonClick() {
        updateLobbyList(searchField.getText());
    }
}
