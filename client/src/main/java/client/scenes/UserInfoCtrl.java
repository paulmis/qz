package client.scenes;

import static javafx.application.Platform.runLater;

import client.communication.user.UserCommunication;
import client.utils.ClientState;
import client.utils.PreferencesManager;
import client.utils.communication.ServerUtils;
import commons.entities.game.GameStatus;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;


/**
 * Control of the user info widget.
 */
@Slf4j
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
            // Send update to server
            userCommunication.changeUsername(
                    this.usernameField.getText(),
                    // Success
                    user -> runLater(() ->
                            mainCtrl.showInformationalSnackBar("Changed username!")),
                    // Failure
                    (error) -> runLater(() -> {
                        mainCtrl.showErrorSnackBar(error == null
                                ? "Failed to change username"
                                : error.getDescription());

                        this.usernameField.setEditable(true);
                    }));
        }
    }

    @FXML
    private void signOutButtonClick() {
        log.info("Signing out");
        server.signOut();
        PreferencesManager.preferences.remove("email");
        PreferencesManager.preferences.remove("password");
        PreferencesManager.preferences.remove("token");
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
