package client.scenes.admin;

import client.scenes.MainCtrl;
import com.ctc.wstx.util.URLUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import commons.entities.ActivityDTO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * Edit activity screen controller.
 * Controls the edit activity pop-up that is part of the admin panel.
 */
public class EditActivityScreenCtrl implements Initializable {

    private ActivityView activity;
    private SaveHandler saveHandler;
    private MainCtrl mainCtrl;

    @FXML private JFXButton saveActivityButton;
    @FXML private TextField activityCostTextField;
    @FXML private ImageView activityImageView;
    @FXML private JFXTextArea activityDescriptionTextArea;
    @FXML private JFXTextArea activitySourceTextArea;
    @FXML private Label addImageActivityLabel;

    private File changedImage;

    private boolean creating;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        activityCostTextField.setText(activity.getCost().toString());
        activityImageView.setImage(activity.getImage());
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

        if (creating) {
            saveActivityButton.setDisable(true);
            addImageActivityLabel.setVisible(true);
            this.activityImageView.setImage(generateImage(200, 200, 200, 1));
        }


        // Make label pass-through
        addImageActivityLabel.setMouseTransparent(true);
    }

    /**
     * Handles the save activity action.
     */
    public interface SaveHandler {
        void handle(ActivityDTO activity, File image);
    }


    /**
     * Edit activity screen controller constructor.
     *
     * @param activity the activity we want to edit(if=null a new activity will be created)
     * @param saveHandler the save handler.
     */
    public EditActivityScreenCtrl(ActivityView activity, SaveHandler saveHandler, MainCtrl mainCtrl) {
        this.activity = Objects.requireNonNullElseGet(activity, () -> {
            this.creating = true;
            var a = new ActivityDTO();
            a.setId(UUID.randomUUID());
            a.setCost(0L);
            a.setDescription("");
            a.setSource("");
            return new ActivityView(a);
        });

        this.saveHandler = saveHandler;
        this.mainCtrl = mainCtrl;
    }



    @FXML
    private void saveActivityButtonClick() {
        try {
            URL url = new URL(activitySourceTextArea.getText());
            url.toURI();
            saveHandler.handle(activity.toDTO(), changedImage);
        } catch (Exception e) {
            mainCtrl.showErrorSnackBar("Make sure the source is a valid url!");
        }
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
            this.addImageActivityLabel.setVisible(false);
            changedImage = pictureFile;
            this.saveActivityButton.setDisable(false);
            this.activity.setImage(new Image(pictureFile.toURI().toString()));
            this.activityImageView.setImage(this.activity.getImage());
        }
    }

    /**
     * Function that generates an image filled with a colour
     * based on the 3 rgb values and opacity.
     *
     * @param red the amount of red
     * @param green the amount of green
     * @param blue the amount of blue
     * @param opacity the opacity of the image
     * @return a new Image file that is filled with the colour obtained out of the
     *          3 rgb values.
     */
    private Image generateImage(int red, int green, int blue, double opacity) {
        WritableImage img = new WritableImage(1, 1);
        PixelWriter pw = img.getPixelWriter();

        Color color = Color.rgb(red, green, blue, opacity);
        pw.setColor(0, 0, color);
        return img;
    }
}
