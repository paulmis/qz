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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;


/**
 * Register Screen controller class.
 */
@Generated
public class RegisterScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton signUpButton;
    @FXML private JFXButton haveAccountButton;
    @FXML private JFXButton usernameSetButton;
    @FXML private CheckBox rememberMe;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField usernameField;
    @FXML private ImageView profilePicture;
    @FXML private ImageView logo;
    @FXML private Label uploadImage;
    @FXML private Label userExists;
    @FXML private FileChooser selectFile;
    @FXML private Pane pane1;
    @FXML private Pane pane2;
    @FXML private BorderPane borderPane1;
    @FXML private BorderPane borderPane2;

    /**
     * Constructor for the register screen control.
     *
     */
    @Inject
    public RegisterScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        this.userExists.setVisible(false);
    }
    /**
     * Function that takes user to lobby list page
     * after they set a username.
     */

    @FXML
    private void setUsername() {
        if (usernameField.getText().length() > 0 ) {
            pane1.setVisible(false);
            pane1.setDisable(true);
            pane1.setMouseTransparent(true);
            borderPane1.setMouseTransparent(true);
            pane2.setVisible(true);
            pane2.setDisable(false);
            pane2.setMouseTransparent(false);
            borderPane2.setMouseTransparent(false);
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

    /**
     * Function that resets picture and username
     * in case of logouts.
     */
    public void reset() { // We want pane1 to pop up first
        pane1.setVisible(true);
        pane1.setDisable(false);
        pane1.setMouseTransparent(false);
        borderPane1.setMouseTransparent(false);
        pane2.setVisible(false);
        pane2.setDisable(true);
        pane2.setMouseTransparent(true);
        borderPane2.setMouseTransparent(true);
        usernameSetButton.setDisable(true);
        profilePicture.setImage(generateImage(200, 200, 200, 1.0));
        uploadImage.setVisible(true);
        usernameField.clear();
    }
    /**
     * Function that sends new account credentials to server
     * after a button click.
     */
    @FXML
    private void signUpButtonClick() {
        server.register(this.usernameField.getText(),
                emailField.getText(), passwordField.getText(),
                (s) -> {
                    javafx.application.Platform.runLater(mainCtrl::showLobbyScreen);
                },
                () -> javafx.application.Platform.runLater(() -> {
                    userExists.setVisible(true); })
        );
    }

    /**
     * Function that takes user to login page
     * if they have an account.
     */
    @FXML
    private void haveAccountButtonClick() {
        mainCtrl.showLogInScreen();
    }

    /**
     * Function that resets the message.
     */
    @FXML
    private void resetMessage() {
        this.userExists.setVisible(false);
    }

    /**
     * Function that keeps track if user
     * wants to be remembered locally or not.
     */
    @FXML
    private void rememberMeTick() {
        if (rememberMe.isSelected()) {
            System.out.print("User wants to be remembered...\n");
        } else {
            System.out.print("User does not want to be remembered...\n");
        }
    }

}
