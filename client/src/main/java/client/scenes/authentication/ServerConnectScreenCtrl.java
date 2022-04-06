package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.PreferencesManager;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * ServerConnectScreen controller class.
 */
@Slf4j
@Generated
public class ServerConnectScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private String serverPath;

    @FXML private JFXButton connectButton;
    @FXML private TextField urlField;
    @FXML private CheckBox rememberServer;

    /**
     * Constructor for the server connect screen control.
     *
     */
    @Inject
    public ServerConnectScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * This function runs after every control has
     * been created and initialized already.
     *
     * @param location This location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Check if local preferences contain a saved server path
        this.serverPath = PreferencesManager.preferences.get("serverPath", null);
        if (serverPath != null) {
            rememberServer.setSelected(true);
        } else {
            this.serverPath = "http://localhost:8080/";
        }
        urlField.setText(this.serverPath);

        // On enter, run the server connect code
        urlField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent enter) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    clickConnectButton();
                }
            }
        });
    }

    /**
     * Function that connects the user to the server based on the url.
     */
    @FXML
    private void clickConnectButton() {
        this.serverPath = urlField.getText().isEmpty() ? "http://localhost:8080/" : urlField.getText();
        if (rememberServer.isSelected()) {
            PreferencesManager.preferences.put("serverPath", this.serverPath);
        } else {
            PreferencesManager.preferences.remove("serverPath");
        }

        log.debug("Connecting to server {}", this.serverPath);
        if (server.connect(this.serverPath)) {
            mainCtrl.setup();
            mainCtrl.showLogInScreen();
        } else {
            log.error("Could not connect to server {}", this.serverPath);
            mainCtrl.showErrorSnackBar("Could not connect to server");
        }
    }
}
