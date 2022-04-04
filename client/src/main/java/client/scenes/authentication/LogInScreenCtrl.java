package client.scenes.authentication;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.PreferencesManager;
import client.utils.communication.EncryptionUtils;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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

    @FXML
    private JFXButton logInButton;
    @FXML
    private JFXButton createAccountButton;
    @FXML
    private CheckBox rememberMe;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Pane pane;

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
    }

    /**
     * Function that sends new account credentials to server.
     * after a button click
     */
    @FXML
    private void logInButtonClick() {
        server.logIn(
                emailField.getText(), passwordField.getText(),
                // Success
                (s) -> runLater(() -> {
                    // If the user is in a lobby/game, put them in the apposite screen
                    if (s.getGame() != null) {
                        ClientState.game = s.getGame();
                        ServerUtils.sseHandler.subscribe();
                        if (s.getGame().getStatus() == GameStatus.CREATED) {
                            mainCtrl.showLobbyScreen();
                            //ToDo: add `mainCtrl.checkHost();` when ClientState.user is updated on login.
                        } else {
                            mainCtrl.showGameScreen(s.getGame().getCurrentQuestion());
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

    @FXML
    private void adminPanelButtonClick() {
        mainCtrl.showActivityListScreen();
    }
}
