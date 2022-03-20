package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.Generated;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;



/**
 * username Selecting Screen controller class.
 */
public class UsernameScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton usernameSetButton;
    @FXML private TextField usernameField;
    @FXML private ImageView profilePicture;
    @FXML private ImageView logo;
    @FXML private Label uploadImage;
    @FXML private FileChooser selectFile;
    @FXML private Pane pane;

    /**
     * Constructor for the username screen control.
     *
     */
    @Inject
    public UsernameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * This function runs after every control has
     * been created and initialized already.
     *
     * @param location These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Function that takes user to lobby list page
     * after they set a username.
     */
    @FXML
    private void setUsername() {
        if (usernameField.getText().length() > 0) {
            System.out.print("Welcome " + usernameField.getText() + " !");
            mainCtrl.showRegisterScreen();
        } else {
            System.out.print("No username set !");
        }
    }

    /**
     * Function that lets the user
     * upload a picture.
     */
    @FXML
    private void uploadPicture() {
        selectFile = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        selectFile.setTitle("Select your Profile Picture");
        selectFile.getExtensionFilters().add(imageFilter);
        File pictureFile = selectFile.showOpenDialog(mainCtrl.getPrimaryStage());
        if (pictureFile != null) {
            usernameSetButton.setDisable(false);
            uploadImage.setVisible(false);
            profilePicture.setImage(new Image(pictureFile.getAbsolutePath()));
        }
    }

    private Image generateImage(int red, int green, int blue, double opacity) {
        WritableImage img = new WritableImage(1, 1);
        PixelWriter pw = img.getPixelWriter();

        Color color = Color.rgb(red, green, blue, opacity);
        pw.setColor(0, 0, color);
        return img;
    }

    public TextField getUsernameField() {
        return usernameField;
    }

    /**
     * Function that resets picture and username
     * in case of logouts.
     */
    public void reset() {
        usernameSetButton.setDisable(true);
        profilePicture.setImage(generateImage(200, 200, 200, 1.0));
        uploadImage.setVisible(true);
        usernameField.clear();
    }
}
