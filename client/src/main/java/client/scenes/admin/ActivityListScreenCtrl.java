package client.scenes.admin;

import client.communication.admin.AdminCommunication;
import client.scenes.MainCtrl;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.ActivityDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

public class ActivityListScreenCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final AdminCommunication server;

    @FXML private TableColumn<ActivityView, ImageView> pictureTableColumn;
    @FXML private TableColumn<ActivityView, String> costTableColumn;
    @FXML private TableColumn<ActivityView, String> descriptionTableColumn;
    @FXML private TableColumn<ActivityView, String> sourceTableColumn;

    @FXML private AnchorPane topLevelAdminPanelAnchor;
    @FXML private JFXButton activitiesGoBackButton;
    @FXML private TableView<ActivityView> activityTable;
    @FXML private JFXButton deleteActivityButton;
    @FXML private JFXButton editActivityButton;
    @FXML private JFXButton addActivityButton;

    private final ObservableList<ActivityView> activities;

    private EditActivityScreenPane editActivityPane;

    @Inject
    public ActivityListScreenCtrl(AdminCommunication server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.activities = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editActivityButton.setDisable(newValue == null);
            deleteActivityButton.setDisable(newValue == null);
        });

        topLevelAdminPanelAnchor.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if(editActivityPane != null && event.getPickResult().getIntersectedNode() != addActivityButton
                    && event.getPickResult().getIntersectedNode() != editActivityButton) {
                if (!inHierarchy(event.getPickResult().getIntersectedNode(), editActivityPane.getChildren().get(0))) {
                    this.closeEditActivity();
                }
            }
        });
    }

    public void reset() {
        activities.clear();
        createTableView();
        mockActivities();
    }

    private void updateActivities() {
        server.getAllActivities(activities -> this.activities.setAll(
                activities.stream().map(ActivityView::new).collect(Collectors.toList())
        ), () -> mainCtrl.showErrorSnackBar("Getting activities failed. Check server connection!"));
    }

    private void mockActivities() {
        var activity = new ActivityDTO();
        activity.setCost(123L);
        activity.setDescription("An amazing description");
        activity.setIcon("icon url");
        activity.setId(UUID.randomUUID());
        activity.setSource("a great website here");

        this.activities.setAll(new ActivityView(activity));
    }

    private void createTableView() {
        pictureTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("image")
        );
        costTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("cost")
        );
        descriptionTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );
        sourceTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("source")
        );

        activityTable.setItems(activities);
    }

    public void addActivityButtonClick() {
        this.openEditActivity(null, activity -> {
            server.updateActivity(activity, response -> {
                switch (response.getStatus()) {
                    case 410:
                        mainCtrl.showErrorSnackBar("Source url too long.");
                        break;
                    case 200:
                        mainCtrl.showErrorSnackBar("Activity has been updated.");
                        this.updateActivities();
                        this.closeEditActivity();
                        break;
                    case 201:
                        mainCtrl.showErrorSnackBar("Activity has been added.");
                        this.updateActivities();
                        this.closeEditActivity();
                        break;
                    default:
                        mainCtrl.showErrorSnackBar("Something went wrong.");
                        break;
                }
            }, () -> mainCtrl.showErrorSnackBar("Failed to add the activity."));
        });
    }

    public void editActivityButtonClick() {
        System.out.println(activityTable.getSelectionModel().getSelectedItem());
        this.openEditActivity(activityTable.getSelectionModel().getSelectedItem(), activity -> {
            server.updateActivity(activity, response -> {
                switch (response.getStatus()) {
                    case 410:
                        mainCtrl.showErrorSnackBar("Source url too long.");
                        break;
                    case 200:
                        mainCtrl.showErrorSnackBar("Activity has been updated.");
                        this.updateActivities();
                        this.closeEditActivity();
                        break;
                    case 201:
                        mainCtrl.showErrorSnackBar("Activity has been added.");
                        this.updateActivities();
                        this.closeEditActivity();
                        break;
                    default:
                        mainCtrl.showErrorSnackBar("Something went wrong.");
                        break;
                }
            }, () -> mainCtrl.showErrorSnackBar("Failed to add the activity."));
        });
    }

    public void deleteActivityButtonClick() {
        server.deleteActivity(activityTable.getSelectionModel().getSelectedItem().getId(), response -> {
            switch (response.getStatus()) {
                case 200:
                    mainCtrl.showErrorSnackBar("Delete activity!");
                    this.updateActivities();
                    break;
                case 404:
                    mainCtrl.showErrorSnackBar("Activity not found. Refresh!");
                    break;
                default:
                    mainCtrl.showErrorSnackBar("Something went wrong while deleting the activity.");
                    break;
            }
        }, () -> mainCtrl.showErrorSnackBar("Something went wrong. Check server connection."));
    }

    public void goBackToAuth() {
        mainCtrl.showLogInScreen();
    }

    private void openEditActivity(ActivityView activity, EditActivityScreenCtrl.SaveHandler saveHandler) {
        editActivityPane = new EditActivityScreenPane(activity, saveHandler);
        this.topLevelAdminPanelAnchor.getChildren().add(editActivityPane);
        AnchorPane.setTopAnchor(editActivityPane, 0d);
        AnchorPane.setLeftAnchor(editActivityPane, 0d);
        AnchorPane.setBottomAnchor(editActivityPane, 0d);
        AnchorPane.setRightAnchor(editActivityPane, 0d);
    }

    private void closeEditActivity() {
        this.topLevelAdminPanelAnchor.getChildren().remove(editActivityPane);
        editActivityPane = null;
    }

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

    @FXML
    private void adminPanelKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE && editActivityPane != null) {
            closeEditActivity();
        }
    }
}
