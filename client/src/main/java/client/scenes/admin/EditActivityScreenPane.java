package client.scenes.admin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Edit activity screen pane.
 * This is done to be able to initialize the edit
 * activity screen from code.
 */
public class EditActivityScreenPane extends StackPane {

    private Node view;
    private EditActivityScreenCtrl controller;

    /**
     * The costructor for this wrapper pane.
     *
     * @param activity the activity we want to edit.
     * @param saveHandler the save handler.
     */
    public EditActivityScreenPane(ActivityView activity, EditActivityScreenCtrl.SaveHandler saveHandler) {

        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/admin/EditActivityScreen.fxml"));

        fxmlLoader.setControllerFactory(param ->
                controller = new EditActivityScreenCtrl(activity, saveHandler));

        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getChildren().add(view);
    }
}
