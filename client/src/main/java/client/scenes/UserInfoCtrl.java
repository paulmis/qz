package client.scenes;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class UserInfoCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField usernameField;
    @FXML
    private FontAwesomeIconView editIcon;
    @FXML
    private ImageView playerImageView;

    /**
     * Initialize a new controller using dependency injection.
     *
     * @param server   Reference to communication utilities object.
     * @param mainCtrl Reference to the main controller.
     */
    @Inject
    public UserInfoCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    @FXML
    private void editButtonClick() {
        usernameField.setEditable(!this.usernameField.isEditable());
        if (!usernameField.isEditable()) {
            // Just finished editing, sent update to server
            // ToDo: call endpoint
            System.out.println("New nickname is " + usernameField.getText());
        }
    }

    @FXML
    private void signOutButtonClick() {
        server.signOut();
        mainCtrl.showServerConnectScreen();
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

    /**
     * Sets current username in the widget
     */
    public void setupData() {
        usernameField.setText(ClientState.user.getNickname());
        // ToDo: load user image
        playerImageView.setImage(new Image("https://upload.wikimedia.org/wikipedia/commons/e/e3/Klaus_Iohannis_din_interviul_cu_Dan_Tapalag%C4%83_cropped.jpg"));
    }
}
