package client.scenes.leaderboard;

import commons.entities.UserDTO;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * The leaderboard pane class wrapper.
 * This is done so the class can be initialized in code.
 */
public class LeaderboardPane extends StackPane {

    private Node view;
    private LeaderboardCtrl controller;

    /**
     * The constructor of the class. Initializes the view and adds it to the
     * stackpane and it creates a controller.
     *
     * @param leaderboard The list of users that represent the leaderboard.
     */
    public LeaderboardPane(List<UserDTO> leaderboard) {

        // We create the loader for the fxml of the leaderboard
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/Leaderboard.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the leaderboard of users
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardCtrl(leaderboard));
        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
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
}
