package client.scenes.authentication;

import java.net.URL;
import java.util.ResourceBundle;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 * Estimate Question type controller.
 */
public class RegisterScreenCtrl implements Initializable {

    private final String email;
    private final String password;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private JFXButton signUpButton;

    @FXML
    private JFXButton haveAccountButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    /**
     * Constructor for the estimate question control.
     *
     * @param email The email of the user
     * @param password The password of the user.
     */
    public RegisterScreenCtrl(String email, String password, ServerUtils server, MainCtrl mainCtrl) {
        this.email = email;
        this.password = password;
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

       signUpButton.setOnAction(event -> {
               var ans = server.register(emailField.getText(), passwordField.getText());
               System.out.println("Da");
               if(ans.length()>0)
                   System.out.println("Da");
           });
    }
}
