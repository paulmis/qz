package client.scenes;

import static javafx.application.Platform.runLater;

import client.communication.user.UserCommunication;
import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Control of the user info widget.
 */
public class UserInfoCtrl implements Initializable {
    private final UserCommunication userCommunication;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField usernameField;
    @FXML
    private FontAwesomeIconView editIcon;
    @FXML
    private ImageView playerImageView;


    /**
     * User info controller initializes the communication and mainctrl variables.
     *
     * @param server the server object.
     * @param userCommunication the user communication object.
     * @param mainCtrl the main controller object.
     */
    public UserInfoCtrl(ServerUtils server, UserCommunication userCommunication, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.userCommunication = userCommunication;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // This changes the icon of the edit username button depending on if the username field is editable.
        editIcon.glyphNameProperty().bind(
                Bindings.when(usernameField.editableProperty()).then(
                        "PAPER_PLANE"
                ).otherwise("EDIT")
        );
    }

    @FXML
    private void editButtonClick() {
        usernameField.setEditable(!this.usernameField.isEditable());
        if (!usernameField.isEditable() && !ClientState.user.getUsername().equals(usernameField.getText())) {
            // Just finished editing, sent update to server
            userCommunication.changeUsername(this.usernameField.getText(),
                    (response) -> runLater(() -> {
                        switch (response.getStatus()) {
                            case 200:
                                mainCtrl.showInformationalSnackBar("Changed username!");
                                ClientState.user.setUsername(this.usernameField.getText());
                                break;
                            case 409:
                                mainCtrl.showErrorSnackBar("Username is in use try something else.");
                                this.usernameField.setEditable(true);
                                break;
                            default:
                                mainCtrl.showErrorSnackBar("An unknown error occurred.");
                                this.usernameField.setEditable(true);
                                break;
                        }
                    }),
                    () -> runLater(() -> mainCtrl.showInformationalSnackBar("Something went wrong")));
        }
    }

    @FXML
    private void signOutButtonClick() {
        server.signOut();
        mainCtrl.showServerConnectScreen();
    }



    /**
     * Sets current username in the widget.
     */
    public void setupData() {
        usernameField.setText(ClientState.user.getUsername());
        // ToDo: load user image
        playerImageView.setImage(new Image("https://upload.wikimedia.org/wikipedia/commons/e/e3/Klaus_Iohannis_din_interviul_cu_Dan_Tapalag%C4%83_cropped.jpg"));
    }
}
