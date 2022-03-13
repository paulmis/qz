package client.scenes.authentication;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


/**
 * Log in Screen controller class.
 */
public class LogInScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton logInButton;
    @FXML private JFXButton createAccountButton;
    @FXML private CheckBox rememberMe;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Label wrongCredentials;
    @FXML private Pane pane;

    /**
     * Constructor for the log in screen control.
     *
     */
    @Inject
    public LogInScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
     * Function that sends new account credentials to server.
     * after a button click
     */
    @FXML
    private void logInButtonClick() {
        server.logIn(emailField.getText(), passwordField.getText(),
                (s) -> {
                    javafx.application.Platform.runLater(mainCtrl::showLobbyScreen);
                },
                () -> javafx.application.Platform.runLater(() -> {
                    wrongCredentials.setVisible(true); })
        );
    }

    /**
     * Function that takes user to login page
     * if they have an account.
     */
    @FXML
    private void createAccountButtonClick() {
        panelTransition();
    }

    /**
     * Function that resets the message
     * when entering a new email / password.
     */
    @FXML
    private void resetMessage() {
        this.wrongCredentials.setVisible(false);
    }

    /**
     * Function that keeps track if user
     * wants to be remembered locally or not.
     */
    @FXML
    private void rememberMeTick() {
        rememberMe.getUserData();
    }

    /**
     * Function that translates the panel to the right
     * for the register screen.
     */
    private void panelTransition() {
        TranslateTransition panelTranslate = new TranslateTransition();
        panelTranslate.setByX(mainCtrl.getPrimaryStage().getScene().getWidth() - pane.getWidth());
        panelTranslate.setNode(pane);
        panelTranslate.setDuration(Duration.millis(1000));
        panelTranslate.setCycleCount(1);
        panelTranslate.setAutoReverse(false);
        panelTranslate.setOnFinished(e -> mainCtrl.showRegisterScreen());
        panelTranslate.play();
    }
}
