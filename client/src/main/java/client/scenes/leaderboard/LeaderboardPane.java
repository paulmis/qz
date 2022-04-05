package client.scenes.leaderboard;

import commons.entities.auth.UserDTO;
import commons.entities.game.GamePlayerDTO;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import lombok.Generated;

/**
 * The leaderboard pane class wrapper.
 * This is done so the class can be initialized in code.
 */
@Generated
public class LeaderboardPane extends StackPane {

    private Node view;
    private LeaderboardCtrl controller;

    /**
     * The constructor of the class. Initializes the view and adds it to the
     * stackpane and it creates a controller.
     */
    public LeaderboardPane() {

        // We create the loader for the fxml of the leaderboard
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/Leaderboard.fxml"));

        // We set the controller of the fxml to our newly created controller
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardCtrl());
        // This loads the fxml
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
        AnchorPane.setLeftAnchor(view, 0d);
        AnchorPane.setRightAnchor(view, 0d);
        AnchorPane.setBottomAnchor(view, 0d);
        AnchorPane.setTopAnchor(view, 0d);
    }

    /**
     * Stops the rotation timer.
     */
    public void stop() {
        controller.stop();
    }

    /**
     * Resets the leaderboard.
     *
     * @param leaderboard the new leaderboard.
     */
    public void reset(List<UserDTO> leaderboard) {
        controller.reset(leaderboard);
    }

    public void resetInGame(List<GamePlayerDTO> leaderboard) {
        controller.resetInGame(leaderboard);
        this.setPadding(new Insets(50, 0, 0, 0));
    }
}
