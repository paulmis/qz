package client.scenes.leaderboard;

import client.scenes.MainCtrl;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import commons.entities.auth.UserDTO;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import lombok.Generated;

/**
 * Global leaderboard controller.
 */
@Generated
public class GlobalLeaderboardCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private AnchorPane anchorPane;
    @FXML private GridPane gridPane;
    @FXML private Label waitingLabel;

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
        leaderboardPane = new LeaderboardPane();
        waitingLabel.setMouseTransparent(true);
    }

    /**
     * This function resets the global leaderboard.
     * It will call the server async and update the leaderboard.
     */
    public void reset() {
        waitingLabel.setOpacity(1d);

        if (leaderboardPane != null) {
            this.anchorPane.getChildren().remove(leaderboardPane);
        }

        CompletableFuture.runAsync(() -> {
            var leaderboard = server.getGlobalLeaderboard();
            javafx.application.Platform.runLater(() -> {
                resetLeaderboard(leaderboard);
                waitingLabel.setOpacity(0d);
            });
        });
    }

    /**
     * Resets the leaderboard.
     * This is done in order not to load the database everytime
     * the app starts and only when the user navigates to this page.
     */
    public void resetLeaderboard(List<UserDTO> leaderboard) {
        leaderboardPane.reset(leaderboard);
        this.anchorPane.getChildren().add(leaderboardPane);
        AnchorPane.setLeftAnchor(leaderboardPane, 0d);
        AnchorPane.setRightAnchor(leaderboardPane, 0d);
        AnchorPane.setBottomAnchor(leaderboardPane, 0d);
        AnchorPane.setTopAnchor(leaderboardPane, 100d);
        leaderboardPane.setViewOrder(999);
    }

    @FXML
    private void goBackToLobbies() {
        mainCtrl.showLobbyListScreen();
        leaderboardPane.stop();
    }
}
