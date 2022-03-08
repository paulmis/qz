package client.scenes.leaderboard;

import client.scenes.leaderboard.LeaderboardEntryCtrl;
import commons.entities.UserDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public class LeaderboardEntryPane extends StackPane {

    private Node view;
    private LeaderboardEntryCtrl controller;

    public LeaderboardEntryPane(UserDTO user, Integer rank) {

        // We create the loader for the fxml of the question
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/LeaderboardEntry.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the question text and the answer handler
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardEntryCtrl(user, rank));

        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
    }
}
