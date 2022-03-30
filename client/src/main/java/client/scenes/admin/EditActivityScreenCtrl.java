package client.scenes.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import commons.entities.ActivityDTO;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * Edit activity screen controller.
 * Controls the edit activity pop-up that is part of the admin panel.
 */
public class EditActivityScreenCtrl implements Initializable {

    private ActivityView activity;
    private SaveHandler saveHandler;

    @FXML private JFXButton saveActivityButton;
    @FXML private TextField activityCostTextField;
    @FXML private ImageView activityImageView;
    @FXML private JFXTextArea activityDescriptionTextArea;
    @FXML private JFXTextArea activitySourceTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityCostTextField.setText(activity.getCost().toString());
        activityImageView.setImage(activity.getImage().getImage());
        activityDescriptionTextArea.setText(activity.getDescription());
        activitySourceTextArea.setText(activity.getSource());

        activityCostTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // We check if there exists a non digit character
            if (!newValue.matches("\\d*")) {

                // We remove the non digits characters if they are present.
                this.activityCostTextField.setText(newValue.replaceAll("[^\\d]", ""));
            } else {

                activity.setCost(Long.valueOf(newValue));
            }
        });

        activityDescriptionTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.setDescription(newValue);
        });

        activitySourceTextArea.textProperty().addListener((observable, oldValue, newValue) -> {
            activity.setSource(newValue);
        });
    }

    /**
     * Handles the save activity action.
     */
    public interface SaveHandler {
        void handle(ActivityDTO activity);
    }


    /**
     * Edit activity screen controller constructor.
     *
     * @param activity the activity we want to edit(if=null a new activity will be created)
     * @param saveHandler the save handler.
     */
    public EditActivityScreenCtrl(ActivityView activity, SaveHandler saveHandler) {
        this.activity = Objects.requireNonNullElseGet(activity, () -> {
            var a = new ActivityDTO();
            a.setId(UUID.randomUUID());
            a.setCost(0L);
            a.setDescription("");
            a.setSource("");
            a.setIcon("");
            return new ActivityView(a);
        });

        this.saveHandler = saveHandler;
    }



    @FXML
    private void saveActivityButtonClick() {
        saveHandler.handle(activity.toDTO());
    }

    @FXML
    private void changeImageClick() {
        var fileSelector = new FileChooser();
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png");
        fileSelector.setTitle("Select your Profile Picture");
        fileSelector.getExtensionFilters().add(imageFilter);

        File pictureFile = fileSelector.showOpenDialog(null);
        if (pictureFile != null) {
            this.saveActivityButton.setDisable(false);
            this.activity.setImage(new ImageView(new Image(pictureFile.getAbsolutePath())));
            this.activityImageView.setImage(this.activity.getImage().getImage());
        }
    }
}
