package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;


/**
 * Log in Screen controller class.
 */
public class NicknameScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private JFXButton nicknameSetButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private ImageView profilePicture;

    @FXML
    private FileChooser selectFile;

    /**
     * Constructor for the estimate question control.
     *
     */
    @Inject
    public NicknameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
     * after they set a nickname.
     */
    public void setNickname() {
        System.out.print("Welcome " + nicknameField.getText() + " !");
        mainCtrl.showLobbyScreen();
    }

    /**
     * Function that takes user to lobby list page
     * after they set a nickname.
     */
    public void setPicture() {
        selectFile = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        selectFile.setTitle("Select your Profile Picture");
        selectFile.getExtensionFilters().add(imageFilter);
        File pictureFile = selectFile.showOpenDialog(mainCtrl.getPrimaryStage());
        if (pictureFile != null) {
            profilePicture.setImage(new Image(pictureFile.getAbsolutePath()));
        }
    }

    /**
     * Function that resets picture and username
     * in case of logouts.
     */
    public void reset() {
        profilePicture.setImage(new Image("/client/images/gray.png"));
        nicknameField.clear();
    }
}
