package client.scenes.leaderboard;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;
import commons.entities.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GlobalLeaderboardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private AnchorPane anchorPane;
    @FXML private GridPane gridPane;

    private LeaderboardPane leaderboardPane;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public GlobalLeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void resetLeaderboard() {
        if(leaderboardPane!=null) {
            this.anchorPane.getChildren().remove(leaderboardPane);
        }
        leaderboardPane = new LeaderboardPane(server.getGlobalLeaderboard());
        this.anchorPane.getChildren().add(leaderboardPane);
        AnchorPane.setLeftAnchor(leaderboardPane,0d);
        AnchorPane.setRightAnchor(leaderboardPane,0d);
        AnchorPane.setBottomAnchor(leaderboardPane,0d);
        AnchorPane.setTopAnchor(leaderboardPane,100d);
        leaderboardPane.setViewOrder(999);
    }

    @FXML
    private void goBackToLobbies() {
        mainCtrl.showLobbyScreen();
    }
}
