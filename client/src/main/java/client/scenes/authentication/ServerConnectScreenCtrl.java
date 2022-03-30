package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import lombok.Generated;

/**
 * ServerConnectScreen controller class.
 */
@Generated
public class ServerConnectScreenCtrl implements Initializable {

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
        // Create a local file in documents to store server path
        this.localFile = new File(System.getProperty("user.home")+"/Documents/quizzzServerPath.txt");
        // Check if local file has a saved server path
        this.retrievePath();
        // Set default server path in url field
        urlField.setText(this.serverPath);
    }

    /**
     * Function that connects the user to the server based on the url.
     */
    @FXML
    private void clickConnectButton() {
        if (urlField.getText().isEmpty()) {
            this.serverPath = "http://localhost:8080/";
        } else {
            this.serverPath = urlField.getText();
        }
        if (rememberServer.isSelected()) {
            savePath(this.serverPath);
        } else {
            localFile.delete();
        }
        server.connect(this.serverPath);
        System.out.print("Connecting to server....\n");
        mainCtrl.showLogInScreen();
    }

    public void savePath(String serverPath){
        try {
            // Create a local file if it doesn't exist
            if(!localFile.exists()) {
                localFile.createNewFile();
            }
            // Update file with new server path
            PrintWriter writer = new PrintWriter(new FileWriter(localFile.getAbsolutePath()));
            writer.write(this.serverPath);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retrievePath(){
        try {
            // Check if local file exists
            if (localFile.exists()){
                Scanner scanner = new Scanner(localFile);
                // If server path exists then set the sever path in client and set checkbox to checked
                if (scanner.hasNextLine()) {
                    this.serverPath = scanner.nextLine();
                    rememberServer.setSelected(true);
                }
                this.serverPath = "http://localhost:8080/";
                scanner.close();
            }
            this.serverPath = "http://localhost:8080/";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
