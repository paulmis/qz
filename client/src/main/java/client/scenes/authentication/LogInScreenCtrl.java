package client.scenes.authentication;

import java.net.URL;
import java.util.ResourceBundle;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.jfoenix.controls.JFXButton;
import com.google.inject.Inject;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 * Estimate Question type controller.
 */
public class LogInScreenCtrl implements Initializable {

    private final String email;
    private final String password;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button logInButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private CheckBox rememberMe;

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
    @Inject
    public LogInScreenCtrl(String email, String password, ServerUtils server, MainCtrl mainCtrl) {
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

    }

    /**
     * Function that sends new account credentials to server
     * after a button click
     */
    public void logInButtonClick () {
        server.connectUser(emailField.getText(), passwordField.getText());
        mainCtrl.showGameScreen();
    }

    /**
     * Function that takes user to login page
     * if they have an account
     */
    public void creatAccountButtonClick () {
        mainCtrl.showRegisterScreen();
    }

    /**
     * Function that keeps track if user
     * wants to be remembered locally or not
     */
    public void rememberMeTick () {
        System.out.print("User wants to be registered...");
    }

    public void rememberMeUntick () {
        System.out.print("User wants to not be registered...");
    }
}
