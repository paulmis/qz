package client.scenes.authentication;

import java.net.URL;
import java.util.ResourceBundle;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.jfoenix.controls.JFXButton;
<<<<<<< HEAD
import com.google.inject.Inject;
=======
>>>>>>> 6bf611a (Added server utils function and removed useless attributes)
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
public class RegisterScreenCtrl implements Initializable {

    private final String email;
    private final String password;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button signUpButton;

    @FXML
    private Button haveAccountButton;

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
<<<<<<< HEAD
    @Inject
=======
>>>>>>> 6bf611a (Added server utils function and removed useless attributes)
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

<<<<<<< HEAD
    }

    /**
     * Function that sends new account credentials to server
     * after a button click
     */
    public void signUpButtonClick () {
        server.register(emailField.getText(), passwordField.getText());
        System.out.print("Registering new account credentials...");
        mainCtrl.showGameScreen();
    }

    /**
     * Function that takes user to login page
     * if they have an account
     */
    public void haveAccountButtonClick () {
        mainCtrl.showLogInScreen();
        System.out.print("Registering new account credentials...");
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
=======
       signUpButton.setOnAction(event -> {
               var ans = server.register(emailField.getText(), passwordField.getText());
               System.out.println("Da");
               if(ans.length()>0)
                   System.out.println("Da");
           });
>>>>>>> 6bf611a (Added server utils function and removed useless attributes)
    }

    /**
     * Function that sends new account credentials to server
     * after a button click
     */
    public void signUpButtonClick () {
        server.register(emailField.getText(), passwordField.getText());
        System.out.print("Registering new account credentials...");
        mainCtrl.showGameScreen();
    }

    /**
     * Function that takes user to login page
     * if they have an account
     */
    public void haveAccountButtonClick () {
        mainCtrl.showLogInScreen();
        System.out.print("Registering new account credentials...");
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
