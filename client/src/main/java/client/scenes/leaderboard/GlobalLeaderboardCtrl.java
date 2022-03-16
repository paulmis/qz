package client.scenes.leaderboard;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * Global leaderboard controller.
 */
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

    /**
     * Resets the leaderboard.
     * This is done in order not to load the database everytime
     * the app starts and only when the user navigates to this page.
     */
    public void resetLeaderboard() {
        if (leaderboardPane != null) {
            this.anchorPane.getChildren().remove(leaderboardPane);
        }
        leaderboardPane = new LeaderboardPane(server.getGlobalLeaderboard());
        this.anchorPane.getChildren().add(leaderboardPane);
        AnchorPane.setLeftAnchor(leaderboardPane, 0d);
        AnchorPane.setRightAnchor(leaderboardPane, 0d);
        AnchorPane.setBottomAnchor(leaderboardPane, 0d);
        AnchorPane.setTopAnchor(leaderboardPane, 100d);
        leaderboardPane.setViewOrder(999);
    }

    @FXML
    private void goBackToLobbies() {
        mainCtrl.showLobbyScreen();
        leaderboardPane.stop();
    }
}
