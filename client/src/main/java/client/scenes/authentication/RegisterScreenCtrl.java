package client.scenes.authentication;


import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.EncryptionUtils;
import client.utils.PreferencesManager;
import client.utils.SoundEffect;
import client.utils.SoundManager;
import client.utils.communication.ServerUtils;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;


/**
 * Register Screen controller class.
 */
@Slf4j
@Generated
public class RegisterScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String emailText;
    private String passwordText;
    private File userImage = null;

    @FXML private JFXButton signUpButton;
    @FXML private JFXButton haveAccountButton;
    @FXML private JFXButton usernameSetButton;
    @FXML private CheckBox rememberUser;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private TextField usernameField;
    @FXML private ImageView profilePicture;
    @FXML private Label uploadImage;
    @FXML private FileChooser selectFile;
    @FXML private Pane pane1;
    @FXML private Pane pane2;
    @FXML private BorderPane borderPane1;
    @FXML private BorderPane borderPane2;

    /**
     * Constructor for the register screen control.
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
     * @param location  These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // On enter, run the login code
        emailField.setOnKeyPressed(enter -> {
            if (enter.getCode().equals(KeyCode.ENTER)) {
                signUpButtonClick();
            }
        });

        // On enter, run the login code
        passwordField.setOnKeyPressed(enter -> {
            if (enter.getCode().equals(KeyCode.ENTER)) {
                signUpButtonClick();
            }
        });
    }

    /**
     * Function that takes user to lobby list page
     * after they set a username.
     */
    @FXML
    private void setUsername() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (usernameField.getText().length() > 0) {
            setCredentialsFromFields(rememberUser, emailField, passwordField);
            log.debug("{}: {} [{}]", usernameField.getText(), emailText, userImage.getAbsolutePath());
            server.register(usernameField.getText(),
                    emailText,
                    passwordText,
                    userImage,
                    (response, dto, error) -> runLater(() -> {
                        switch (response.getStatus()) {
                            case 201: {
                                if (rememberUser.isSelected()) {
                                    PreferencesManager.preferences.put("token",
                                            EncryptionUtils.encrypt(dto.getToken(),
                                                    EncryptionUtils.ENCRYPTION_KEY));
                                } else {
                                    PreferencesManager.preferences.remove("token");
                                }

                                mainCtrl.showLobbyListScreen();
                                mainCtrl.showInformationalSnackBar("Success!");
                                break;
                            }
                            case 400: {
                                String s = error.toString();
                                String[] split1 = s.split("\\[");
                                mainCtrl.showErrorSnackBar(split1[1]);
                                break;
                            }
                            case 409: {
                                mainCtrl.showErrorSnackBar("User already exists!");
                                break;
                            }
                            default: {
                                break;
                            }
                            //If the function fails it triggers the error message.
                        }
                    }));
        }
    }

    static void setCredentialsFromFields(CheckBox rememberUser, TextField emailField, TextField passwordField) {
        if (rememberUser.isSelected()) {
            PreferencesManager.preferences.put("email", emailField.getText());
            PreferencesManager.preferences.put("password", EncryptionUtils.encrypt(passwordField.getText(),
                    EncryptionUtils.ENCRYPTION_KEY));
        } else {
            PreferencesManager.preferences.remove("email");
            PreferencesManager.preferences.remove("password");
        }
    }

    /**
     * Function that lets the user
     * upload a picture.
     */
    @FXML
    private void uploadPicture() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        selectFile = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        selectFile.setTitle("Select your Profile Picture");
        selectFile.getExtensionFilters().add(imageFilter);
        // Filter that allows user to only select image files of types jpg png
        File pictureFile = selectFile.showOpenDialog(mainCtrl.getPrimaryStage());

        // Opens up a dialogue that lets user select a file
        if (pictureFile != null) {  // Every user needs to select a picture
            usernameSetButton.setDisable(false);
            uploadImage.setVisible(false);
            profilePicture.setImage(new Image(pictureFile.toURI().toString()));
            userImage = pictureFile;
        }
    }

    /**
     * Function that generates an image filled with a colour
     * based on the 3 rgb values and opacity.
     *
     * @param red     the amount of red
     * @param green   the amount of green
     * @param blue    the amount of blue
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

    /**
     * Function that resets picture and username
     * in case of logouts.
     */
    public void reset() { // We want pane1 to pop up first
        pane2.setVisible(true);
        pane2.setDisable(false);
        pane2.setMouseTransparent(false);
        borderPane2.setMouseTransparent(false);
        pane1.setVisible(false);
        pane1.setDisable(true);
        pane1.setMouseTransparent(true);
        borderPane1.setMouseTransparent(true);
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
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        if (!emailField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            if (ServerUtils.isValidEmail(emailField.getText())) {
                emailText = emailField.getText();
                passwordText = passwordField.getText();
                pane2.setVisible(false);
                pane2.setDisable(true);
                pane2.setMouseTransparent(true);
                borderPane2.setMouseTransparent(true);
                pane1.setVisible(true);
                pane1.setDisable(false);
                pane1.setMouseTransparent(false);
                borderPane1.setMouseTransparent(false);
            } else {
                mainCtrl.showErrorSnackBar("Enter a valid email");
            }
        } else {
            mainCtrl.showErrorSnackBar("Missing email and/or password");
        }
    }

    /**
     * Function that takes user to login page
     * if they have an account.
     */
    @FXML
    private void haveAccountButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        mainCtrl.showLogInScreen();
    }

    /**
     * Opens the admin panel.
     */
    @FXML
    private void adminPanelButtonClick() {
        SoundManager.playMusic(SoundEffect.BUTTON_CLICK, getClass());
        mainCtrl.showActivityListScreen();
    }
}
