package client.scenes.admin;

import static javafx.application.Platform.runLater;

import client.communication.admin.AdminCommunication;
import client.scenes.MainCtrl;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.ActivityDTO;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


/**
 * This is the controller for the activity list screen.
 * This screen is the main screen of the admin panel.
 */
public class ActivityListScreenCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final AdminCommunication server;

    @FXML private TableColumn<ActivityDTO, UUID> pictureTableColumn;
    @FXML private TableColumn<ActivityDTO, String> costTableColumn;
    @FXML private TableColumn<ActivityDTO, String> descriptionTableColumn;
    @FXML private TableColumn<ActivityDTO, String> sourceTableColumn;

    @FXML private AnchorPane topLevelAdminPanelAnchor;
    @FXML private JFXButton activitiesGoBackButton;
    @FXML private TableView<ActivityDTO> activityTable;
    @FXML private JFXButton deleteActivityButton;
    @FXML private JFXButton editActivityButton;
    @FXML private JFXButton addActivityButton;

    private final ObservableList<ActivityDTO> activities;

    private EditActivityScreenPane editActivityPane;

    /**
     * Gets the server and mainctrl by injection.
     *
     * @param server the communication point for the application.
     * @param mainCtrl the main controller.
     */
    @Inject
    public ActivityListScreenCtrl(AdminCommunication server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.activities = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This piece of code enables the edit activity and delete activity
        // buttons depending on if a row is selected in the table.
        activityTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editActivityButton.setDisable(newValue == null);
            deleteActivityButton.setDisable(newValue == null);
        });

        // This is part of the implementation of a pop-up
        // This handles closing the pop-up when the user clicks outside it.
        topLevelAdminPanelAnchor.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (editActivityPane != null && event.getPickResult().getIntersectedNode() != addActivityButton
                    && event.getPickResult().getIntersectedNode() != editActivityButton) {
                if (!inHierarchy(event.getPickResult().getIntersectedNode(), editActivityPane.getChildren().get(0))) {
                    this.closeEditActivity();
                }
            }
        });
    }

    /**
     * Resets the controller to a default state.
     */
    public void reset() {
        createTableView();
        updateActivities();
    }

    /**
     * Gets all the activities from the server and adds them to the table.
     */
    private void updateActivities() {
        server.getAllActivities(
                this.activities::setAll,
                (error) -> runLater(() ->
                        mainCtrl.showErrorSnackBar("Getting activities failed." + error.getDescription())));
    }

    /**
     * Creates the table view and binds the properties.
     */
    private void createTableView() {
        costTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("cost")
        );
        descriptionTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );
        sourceTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("source")
        );

        pictureTableColumn.setCellValueFactory(new PropertyValueFactory<>("iconId"));

        pictureTableColumn.setCellFactory(tc -> {
            TableCell<ActivityDTO, UUID> cell = new TableCell<ActivityDTO, UUID>() {
                private ImageView imageView = new ImageView();
                @Override
                protected void updateItem(UUID activityId, boolean empty) {
                    super.updateItem(activityId, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        imageView.setImage(new Image(ServerUtils.getImagePathFromId(activityId), true));
                        setGraphic(imageView);
                    }
                }
            };
            return cell;
        });
        activityTable.setItems(activities);
    }

    /**
     * Add activity button handler.
     */
    @FXML
    private void addActivityButtonClick() {
        this.openEditActivity(null, (activity, image) -> server.updateActivity(activity, image,
                        () -> runLater(() -> {
                            mainCtrl.showInformationalSnackBar("Activity has been added.");
                            this.updateActivities();
                            this.closeEditActivity();
                        }), (error) -> runLater(() ->
                                mainCtrl.showErrorSnackBar("The following error occurred: " + error.getDescription()))
        ));
    }

    /**
     * Edit activity button handler.
     */
    @FXML
    private void editActivityButtonClick() {
        this.openEditActivity(new ActivityView(activityTable.getSelectionModel().getSelectedItem()),
                (activity, image) -> server.updateActivity(activity, image,
                        () -> runLater(() -> {
                            mainCtrl.showInformationalSnackBar("Activity has been updated.");
                            this.updateActivities();
                            this.closeEditActivity();
                        }), (error) -> runLater(() ->
                                mainCtrl.showErrorSnackBar("The following error occurred: " + error.getDescription()))
        ));
    }

    /**
     * Delete activity button handler.
     */
    @FXML
    private void deleteActivityButtonClick() {
        var selectedActivity = activityTable.getSelectionModel().getSelectedItem();
        server.deleteActivity(selectedActivity.getId(),
                () -> runLater(() -> {
                    activities.remove(selectedActivity);
                    mainCtrl.showInformationalSnackBar("Deleted activity!");
                }),
                (error) -> runLater(() -> mainCtrl.showErrorSnackBar(error.getDescription())));
    }

    /**
     * Function that handles the back button click.
     */
    @FXML
    private void goBackToAuth() {
        mainCtrl.showLogInScreen();
    }

    /**
     * Opens the edit activity pop-up.
     *
     * @param activity the activity to view/edit.
     * @param saveHandler the handler for save.
     */
    private void openEditActivity(ActivityView activity, EditActivityScreenCtrl.SaveHandler saveHandler) {
        editActivityPane = new EditActivityScreenPane(activity, saveHandler, mainCtrl);
        this.topLevelAdminPanelAnchor.getChildren().add(editActivityPane);
        AnchorPane.setTopAnchor(editActivityPane, 0d);
        AnchorPane.setLeftAnchor(editActivityPane, 0d);
        AnchorPane.setBottomAnchor(editActivityPane, 0d);
        AnchorPane.setRightAnchor(editActivityPane, 0d);
    }

    /**
     * CLoses the edit activity pop-up.
     */
    private void closeEditActivity() {
        this.topLevelAdminPanelAnchor.getChildren().remove(editActivityPane);
        editActivityPane = null;
    }

    /**
     * A function that checks if a node is part of
     * the hierarchy of another node.
     *
     * @param node the parent node.
     * @param potentialHierarchyElement the potential node.
     * @return a boolean representing if the potential node is in the hierarchy.
     */
    private boolean inHierarchy(Node node, Node potentialHierarchyElement) {
        if (potentialHierarchyElement == null) {
            return true;
        }
        while (node != null) {
            if (node == potentialHierarchyElement) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    /**
     * Handles the admin panel key pressed event.
     * This is used to close the edit activity panenl on ESCAPE key pressed.
     *
     * @param event the key event.
     */
    @FXML
    private void adminPanelKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE && editActivityPane != null) {
            closeEditActivity();
        }
    }
}
