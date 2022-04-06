package client.scenes.leaderboard;

import commons.entities.auth.UserDTO;
import commons.entities.game.GamePlayerDTO;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * The leaderboard entry pane.
 * This is created only to make the
 * entry creatable inside code.
 */
@Slf4j
@Generated
public class LeaderboardEntryPane extends StackPane {

    private Node view;
    private LeaderboardEntryCtrl controller;

    /**
     * This is the constructor of the leaderboard entry.
     * It creates the view and adds it to the pane and sets
     * the controller.
     *
     * @param user The user that is to be displayed inside the entry.
     * @param rank The rank of the user that is to be displayed.
     */
    public LeaderboardEntryPane(UserDTO user, Integer rank) {

        // We create the loader for the fxml of the leaderboard entry
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/LeaderboardEntry.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the user entity and the rank.
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardEntryCtrl(user, rank));

        // This loads the fxml
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            log.error("Error loading leaderboard entry fxml", e);
            Platform.exit();
            System.exit(0);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
    }

    /**
     * This is the constructor of the leaderboard entry.
     * It creates the view and adds it to the pane and sets
     * the controller.
     *
     * @param player The user that is to be displayed inside the entry.
     * @param rank The rank of the user that is to be displayed.
     */
    public LeaderboardEntryPane(GamePlayerDTO player, Integer rank) {

        // We create the loader for the fxml of the leaderboard entry
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/LeaderboardEntry.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the user entity and the rank.
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardEntryCtrl(player, rank));

        // This loads the fxml
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            log.error("Error loading leaderboard entry fxml", e);
            Platform.exit();
            System.exit(0);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
    }
}
