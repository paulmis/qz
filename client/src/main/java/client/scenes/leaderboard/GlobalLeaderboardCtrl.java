package client.scenes.leaderboard;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;
import commons.entities.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GlobalLeaderboardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private AnchorPane anchorPane;

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
        var leaderboard = new LeaderboardPane();
        this.anchorPane.getChildren().add(leaderboard);
        anchorPane.setLeftAnchor(leaderboard,0d);
        anchorPane.setRightAnchor(leaderboard,0d);
        anchorPane.setBottomAnchor(leaderboard,0d);
        anchorPane.setTopAnchor(leaderboard,100d);
        leaderboard.setViewOrder(999);
    }
}
