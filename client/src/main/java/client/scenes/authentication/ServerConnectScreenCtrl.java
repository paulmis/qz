package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.communication.FileUtils;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.io.File;
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

/**
 * ServerConnectScreen controller class.
 */
@Generated
public class ServerConnectScreenCtrl implements Initializable {

    private final FileUtils file;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private File localFile;
    private String serverPath;

    @FXML private JFXButton connectButton;
    @FXML private TextField urlField;
    @FXML private CheckBox rememberServer;

    /**
     * Constructor for the server connect screen control.
     *
     */
    @Inject
    public ServerConnectScreenCtrl(FileUtils file, ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.file = file;
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
        // Create a local file in documents to store server path
        this.localFile = new File(System.getProperty("user.home") + "/Documents/quizzzServerPath.txt");
        // Check if local file has a saved server path
        this.serverPath = file.retrievePath(localFile);
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
            file.savePath(localFile, this.serverPath);
        } else {
            localFile.delete();
        }
        server.connect(this.serverPath);
        System.out.print("Connecting to server....\n");
        mainCtrl.showLogInScreen();
    }
}
