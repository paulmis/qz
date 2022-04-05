package client.scenes.authentication;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.EncryptionUtils;
import client.utils.PreferencesManager;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

/**
 * Log in Screen controller class.
 */
@Generated
@Slf4j
public class LogInScreenCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML private JFXButton logInButton;
    @FXML private JFXButton createAccountButton;
    @FXML private CheckBox rememberUser;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Pane pane;

    /**
     * Constructor for the log in screen control.
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
     * @param location  These location parameter.
     * @param resources The resource bundle.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String tkn = PreferencesManager.preferences.get("token", null);
        try {
            if (tkn != null && (tkn = EncryptionUtils.decrypt(tkn, EncryptionUtils.ENCRYPTION_KEY)) != null) {
                server.checkTokenValid(tkn, (s) -> runLater(mainCtrl::showLobbyListScreen));
            }
        } catch (Exception e) {
            log.error("Error while decrypting token", e);
        }

        // Check if preferences contain user credentials
        String email = PreferencesManager.preferences.get("email", null);
        String password = PreferencesManager.preferences.get("password", null);
        if (password != null) {
            // Decrypt the password
            password = EncryptionUtils.decrypt(password, EncryptionUtils.ENCRYPTION_KEY);
        }

        // If preferences contain user credentials,
        if (email == null || password == null) {
            rememberUser.setSelected(false);
            emailField.setText("");
            passwordField.setText("");
        } else {
            // Set saved credentials
            rememberUser.setSelected(true);
            emailField.setText(email);
            passwordField.setText(password);
        }

        // On enter, run the login code
        emailField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent enter) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    logInButtonClick();
                }
            }
        });

        // On enter, run the login code
        passwordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent enter) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    logInButtonClick();
                }
            }
        });
    }

    /**
     * Function that sends new account credentials to server.
     * after a button click
     */
    @FXML
    private void logInButtonClick() {
        if (!emailField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
            if (ServerUtils.isValidEmail(emailField.getText())) {
                RegisterScreenCtrl.setCredentialsFromFields(rememberUser, emailField, passwordField);
                server.logIn(
                        emailField.getText(), passwordField.getText(),
                        // Success
                        (s) -> runLater(() -> {
                            if (rememberUser.isSelected()) {
                                PreferencesManager.preferences.put("token",
                                        EncryptionUtils.encrypt(s.getToken(), EncryptionUtils.ENCRYPTION_KEY));
                            } else {
                                PreferencesManager.preferences.remove("token");
                            }

                            // If the user is in a lobby/game, put them in the apposite screen
                            if (s.getGame() != null) {
                                ClientState.game = s.getGame();
                                ServerUtils.sseHandler.subscribe();
                                if (s.getGame().getStatus() == GameStatus.CREATED) {
                                    mainCtrl.showLobbyScreen();
                                    //ToDo: add `mainCtrl.checkHost();` when ClientState.user is updated on login.
                                } else {
                                    mainCtrl.showGameScreen(null);
                                }
                            } else {
                                mainCtrl.showLobbyListScreen();
                            }
                        }),
                        // Failure
                        () -> runLater(() -> {
                            mainCtrl.showErrorSnackBar("Something went wrong while logging you in.");
                        })
                );
            } else {
                mainCtrl.showErrorSnackBar("Enter a valid email");
            }
        } else {
            mainCtrl.showErrorSnackBar("Missing email and/or password");
        }
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

    @FXML
    private void adminPanelButtonClick() {
        mainCtrl.showActivityListScreen();
    }
}
