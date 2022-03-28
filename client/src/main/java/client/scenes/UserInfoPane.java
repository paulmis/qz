package client.scenes;

import client.utils.communication.ServerUtils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import lombok.Generated;

@Generated
public class UserInfoPane extends StackPane {
    private Node view;
    private UserInfoCtrl controller;

    public UserInfoPane(ServerUtils server, MainCtrl mainCtrl) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/UserInfo.fxml"));
        fxmlLoader.setControllerFactory(param ->
                controller = new UserInfoCtrl(server, mainCtrl));
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            Platform.exit();
            System.exit(0);
        }
        getChildren().add(view);
        controller.setupData();
    }

    public void setupPosition(Control btnWidget, Pane mainView) {
        double minMargins = 10;

        // Set X
        double preferredX = btnWidget.getWidth() / 2 + btnWidget.getLayoutX() - this.getWidth() / 2;
        if (preferredX + this.getWidth() + minMargins > mainView.getWidth()) {
            preferredX = mainView.getWidth() - (this.getWidth() + minMargins);
        }
        this.setLayoutX(preferredX);

        // Set Y
        this.setLayoutY(btnWidget.getLayoutY() + btnWidget.getHeight() + minMargins);
    }
}
