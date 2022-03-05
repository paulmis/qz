package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;



/**
 * ServerConnectScreen controller class.
 */
public class ServerConnectScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton connectButton;
    @FXML private TextField urlField;

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
     * @param location These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Function that connects the user to the server based on the url.
     */
    public void clickConnectButton() {
        System.out.print("Connecting to server....\n" + urlField.getText());
        server.connect();
        mainCtrl.showLogInScreen();
    }
}
