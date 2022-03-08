package client.scenes.leaderboard;

import com.jfoenix.controls.JFXListView;
import commons.entities.UserDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GlobalLeaderboardCtrl implements Initializable {
    @FXML
    private ListView usersList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // test.itemsProperty().addListener((observable, oldValue, newValue) -> {
        //    test.setPrefHeight(test.getItems().size()*50);
        //});

      //  usersList.setPrefHeight(10000*50);
      ///  usersList.setMinHeight(10000*50);
      //  usersList.setMaxHeight(10000*50);

       // javafx.application.Platform.runLater(()->
      //  {
      //      for (int i = 0; i < 10000; i++) {
      //          var user = new UserDTO();
      //          user.setUsername("david");
       //         user.setGamesPlayed(100);
       //         user.setScore(123);
        //        usersList.getItems().add(new LeaderboardEntryPane(user, i + 1));
       //         //usersList.getChildren().add(new LeaderboardEntryPane(user,i+1));
        //    }
        //});




    }
}
