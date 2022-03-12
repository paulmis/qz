package client.scenes.leaderboard;

import commons.entities.UserDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class LeaderboardPane extends StackPane {

    private Node view;
    private LeaderboardCtrl controller;

    public LeaderboardPane() {

        // We create the loader for the fxml of the question
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/leaderboard/Leaderboard.fxml"));

        // We set the controller of the fxml to our newly created controller
        // we also pass in the question text and the answer handler
        fxmlLoader.setControllerFactory(param ->
                controller = new LeaderboardCtrl());
        // This loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Adds it to the view of this control(stack pane)
        getChildren().add(view);
        AnchorPane.setLeftAnchor(view,0d);
        AnchorPane.setRightAnchor(view,0d);
        AnchorPane.setBottomAnchor(view,0d);
        AnchorPane.setTopAnchor(view,0d);
    }
}
