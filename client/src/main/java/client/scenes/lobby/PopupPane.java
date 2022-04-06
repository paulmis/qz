package client.scenes.lobby;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * The class that encompasses all warning popups.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */
@Slf4j
@Generated
public class PopupPane extends StackPane {

    private Node view;
    private Object controller;

    private void setUpScreen(Object ctrl, String fxmlPath) {
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/" + fxmlPath + ".fxml"));
        fxmlLoader.setControllerFactory(param ->
                controller = ctrl);
        try {
            view = fxmlLoader.load();
        } catch (Exception e) {
            log.error("Error loading popup pane: " + e.getMessage());
            Platform.exit();
            System.exit(0);
        }
        getChildren().add(view);
    }

    /**
     * This constructor sets up popup panes.
     *
     * @param ctrl the controller that needs to be setup.
     * @param fxmlPath the path of UI controller being setup.
     */
    public PopupPane(Object ctrl, String fxmlPath) {
        setUpScreen(ctrl, fxmlPath);
    }
}
