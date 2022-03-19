package client.scenes.lobby;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
            System.out.println(newValue);
        });
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
        userPanelGrid.setVisible(!userPanelGrid.isVisible());
    }

    @FXML
    private void createLobbyButtonClick() {

    }

    @FXML
    private void signOutButtonClick() {

    }

    @FXML
    private void editButtonClick() {

    }

    @FXML
    private void searchButtonClick() {
        updateLobbyList();
    }

    public void reset() {
        updateLobbyList();
        this.searchField.setText("");
    }

    private void updateLobbyList() {
        server.getLobbies(
                games -> {
                    System.out.println(games);
                    javafx.application.Platform.runLater(() -> {
                        lobbyListVbox.getChildren().addAll(
                                games.stream().map(gameDTO -> new LobbyListItemPane(gameDTO, (a) -> {})).collect(Collectors.toList())
                        );
                        lobbyListVbox.getChildren().addAll(
                                games.stream().map(gameDTO -> new LobbyListItemPane(gameDTO, (a) -> {})).collect(Collectors.toList())
                        );
                    });
                },
                () -> {
                    System.out.println("huge errorrrr");
                });
    }
}
