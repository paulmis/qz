package client.scenes.authentication;


import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import commons.entities.utils.ApiError;
import java.io.File;
import java.net.URL;
import java.util.Optional;
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
import javax.ws.rs.core.Response;
import lombok.Generated;


/**
 * Register Screen controller class.
 */
@Generated
public class RegisterScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String emailText;
    private String passwordText;

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
    @FXML private Label registerMessage;
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
        if (usernameField.getText().length() > 0) {
            System.out.print(usernameField.getText() + emailText + passwordText);
            server.register(usernameField.getText(), emailText,  passwordText, new ServerUtils.RegisterHandler() {
                @Override
                public void handle(Response response, ApiError error) {
                    javafx.application.Platform.runLater(() -> {
                        switch (response.getStatus()) {
                            case 200: {
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
                    });
                }
            });
        } else {
            //No username set
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
        // Filter that allows user to only select image files of types jpg png
        File pictureFile = selectFile.showOpenDialog(mainCtrl.getPrimaryStage());
        // Opens up a dialogue that lets user select a file
        if (pictureFile != null) {  // Every user needs to select a picture
            usernameSetButton.setDisable(false);
            uploadImage.setVisible(false);
            profilePicture.setImage(new Image(pictureFile.getAbsolutePath()));
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
        registerMessage.setVisible(false);
        usernameField.clear();
    }

    /**
     * Function that sends new account credentials to server
     * after a button click.
     */
    @FXML
    private void signUpButtonClick() {
        passwordText = passwordField.getText();
        emailText = emailField.getText();
        pane2.setVisible(false);
        pane2.setDisable(true);
        pane2.setMouseTransparent(true);
        borderPane2.setMouseTransparent(true);
        pane1.setVisible(true);
        pane1.setDisable(false);
        pane1.setMouseTransparent(false);
        borderPane1.setMouseTransparent(false);
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
