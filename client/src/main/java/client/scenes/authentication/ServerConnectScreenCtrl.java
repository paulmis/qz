package client.scenes.authentication;

import java.net.URL;
import java.util.ResourceBundle;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 * ServerConnectScreen controller class.
 */
public class ServerConnectScreenCtrl implements Initializable {

    private final String url;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button connectButton;

    @FXML
    private TextField urlField;


    /**
     * Constructor for the estimate question control.
     *
     * @param url The url of the server
     */
    @Inject
    public ServerConnectScreenCtrl(String url, ServerUtils server, MainCtrl mainCtrl) {
        this.url = url;
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
     * Function that connects the user to the server based on the url
     */
    public void clickConnectButton () {
        var url = urlField.getText();
        System.out.print("Connecting to server....");
        server.connect();
        mainCtrl.showLogInScreen();
    }
}
