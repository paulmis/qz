package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;


/**
 * Register Screen controller class.
 */
public class RegisterScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton signUpButton;
    @FXML private JFXButton haveAccountButton;
    @FXML private CheckBox rememberMe;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    /**
     * Constructor for the register screen control.
     *
     */
    @Inject
    public RegisterScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
     * after a button click.
     */
    public void signUpButtonClick() {
        if (emailField.getText().length() > 0 && passwordField.getText().length() > 0) {
            server.register(emailField.getText(), passwordField.getText());
            System.out.print("Registering new account credentials...");
            mainCtrl.showNicknameScreen();
        } else {
            System.out.print("Can't have empty username or password ! Try again");
        }
    }

    /**
     * Function that takes user to login page
     * if they have an account.
     */
    public void haveAccountButtonClick() {
        mainCtrl.showLogInScreen();
    }

    /**
     * Function that keeps track if user
     * wants to be remembered locally or not.
     */
    public void rememberMeTick() {
        if (rememberMe.isSelected()) {
            System.out.print("User wants to be remembered...\n");
        } else {
            System.out.print("User does not want to be remembered...\n");
        }
    }

}
