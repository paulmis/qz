package client.scenes.lobby;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.AlgorithmicUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.Comparator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Generated;

/**
 * Lobby list controller. Controls the lobby list.
 */
@Generated
public class LobbyListCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton leaderboardButton;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton searchButton;
    @FXML private JFXButton fetchButton;
    @FXML private GridPane userPanelGrid;
    @FXML private JFXButton createLobbyButton;
    @FXML private TextField searchField;
    @FXML private VBox lobbyListVbox;
    @FXML private JFXButton signOutButton;
    @FXML private JFXButton editButton;
    @FXML private JFXButton joinRandomLobbyButton;
    @FXML private TextField usernameField;
    @FXML private ImageView playerImageView;
    @FXML private FontAwesomeIconView editIcon;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public LobbyListCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            //TODO: Replace this with a server call to change the username when it becomes available.
            System.out.println(newValue);
        });


        // This changes the icon of the edit username button depending on if the username field is editable.
        this.editIcon.glyphNameProperty().bind(
                Bindings.when(usernameField.editableProperty()).then(
                        "PAPER_PLANE"
                ).otherwise("EDIT")
        );
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
        if (!userPanelGrid.isVisible()) {
            server.getMyInfo(userDTO -> {
                runLater(() -> {
                    this.usernameField.setText(userDTO.getUsername());
                    userPanelGrid.setVisible(true);
                    playerImageView.setImage(new Image("https://upload.wikimedia.org/wikipedia/commons/e/e3/Klaus_Iohannis_din_interviul_cu_Dan_Tapalag%C4%83_cropped.jpg"));
                });
            }, () -> runLater(() ->
                            mainCtrl.showErrorSnackBar("Something went wrong while fetching your user data.")));
        } else {
            userPanelGrid.setVisible(false);
        }
    }

    @FXML
    private void createLobbyButtonClick() {
        server.createLobby(game -> runLater(mainCtrl::showLobbyScreen),
                () -> runLater(() -> mainCtrl.showErrorSnackBar("Something went wrong while creating the new lobby.")));
    }

    @FXML
    private void signOutButtonClick() {
        server.signOut();
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
        userPanelGrid.setVisible(false);
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

    /**
     * Function that lets the user join a random lobby.
     */
    @FXML
    private void joinRandomLobby() {
        server.getLobbies(
            games -> {
                // Gets a random available lobby and joins it
                var game = games.get(new Random().nextInt(games.size()));
                server.joinLobby(game.getId(), gameDTO -> {
                    runLater(mainCtrl::showLobbyScreen);
                }, () -> {
                    runLater(() -> {
                        mainCtrl.showErrorSnackBar("Couldn't join random game.");
                    });
                });
            },
            () -> {
                // If there are no available games, the user creates a new lobby
                runLater(this::createLobbyButtonClick);
            });
    }

    @FXML
    private void fetchButtonClick() {
        updateLobbyList(searchField.getText());
    }
}
