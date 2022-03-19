package client.scenes.lobby;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameDTO;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.Comparator;
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

/**
 * Lobby list controller. Controls the lobby list.
 */
public class LobbyListCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton leaderboardButton;
    @FXML private JFXButton settingsButton;
    @FXML private JFXButton userButton;
    @FXML private JFXButton searchButton;
    @FXML private GridPane userPanelGrid;
    @FXML private JFXButton createLobbyButton;
    @FXML private TextField searchField;
    @FXML private VBox lobbyListVbox;
    @FXML private JFXButton signOutButton;
    @FXML private JFXButton editButton;
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
            System.out.println(newValue);
        });

        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateLobbyList(searchField.getText());
        });


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
                javafx.application.Platform.runLater(() -> {
                    this.usernameField.setText(userDTO.getUsername());
                    userPanelGrid.setVisible(true);
                    playerImageView.setImage(new Image("https://upload.wikimedia.org/wikipedia/commons/e/e3/Klaus_Iohannis_din_interviul_cu_Dan_Tapalag%C4%83_cropped.jpg"));
                });
            }, () -> System.out.println("Getting my data failed"));
        } else {
            userPanelGrid.setVisible(false);
        }
    }

    @FXML
    private void createLobbyButtonClick() {
        server.createLobby(game -> javafx.application.Platform.runLater(mainCtrl::showLobbyScreen),
                () -> System.out.println("creating failed"));
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
                games -> javafx.application.Platform.runLater(() -> {
                    lobbyListVbox.getChildren().clear();
                    lobbyListVbox.getChildren().addAll(
                            games.stream().sorted(Comparator.comparing(gameDTO -> levDist(filter,
                                    createSearchableString(gameDTO)))).map(gameDTO ->
                                    new LobbyListItemPane(gameDTO, (id) -> server.joinLobby(id, gameDTO1 ->
                                            javafx.application.Platform.runLater(mainCtrl::showLobbyScreen),
                                            () -> {}))).collect(Collectors.toList())
                    );
                }),
                () -> {});
    }

    private String createSearchableString(GameDTO game) {
        return game.getPlayers().stream().filter(gamePlayerDTO -> gamePlayerDTO.getId().equals(game.getHost()))
                .findFirst().get().getNickname();
    }

    private int levDist(String a, String b) {
        var dist = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i < a.length(); i++) {
            dist[i][0] = 0;
        }

        for (int i = 0; i < b.length(); i++) {
            dist[0][i] = 0;
        }

        for (int j = 1; j <= b.length(); j++) {
            for (int i = 1; i <= a.length(); i++) {
                dist[i][j] =
                        Math.min(
                                Math.min(
                                        dist[i - 1][j] + 1,
                                        dist[i][j - 1] + 1),
                                (dist[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1)));
            }
        }

        return dist[a.length()][b.length()];
    }
}
