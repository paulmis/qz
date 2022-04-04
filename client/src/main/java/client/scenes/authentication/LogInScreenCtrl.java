package client.scenes.authentication;

import static javafx.application.Platform.runLater;

import client.scenes.MainCtrl;
import client.utils.ClientState;
import client.utils.communication.FileUtils;
import client.utils.communication.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import commons.entities.game.GameStatus;

import java.io.File;
import java.net.URL;
import java.util.List;
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

/**
 * Log in Screen controller class.
 */
@Generated
public class LogInScreenCtrl implements Initializable {

    private final FileUtils file;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private File localFile;

    @FXML private JFXButton logInButton;
    @FXML private JFXButton createAccountButton;
    @FXML private CheckBox rememberUser;
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Pane pane;

    /**
     * Constructor for the log in screen control.
     *
     */
    @Inject
    public LogInScreenCtrl(FileUtils file, ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.file = file;
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
        // Create a local file in documents to store user credentials
        this.localFile = new File(System.getProperty("user.home") + "/Documents/quizzzCredentials.txt");
        // Check if local file has a saved user credentials
        List<String> credentials = file.retrieveCredentials(localFile);
        // Check if credentials are saved
        if (!credentials.contains(null)) {
            rememberUser.setSelected(true);
        } else {
            credentials.add(0, "");
            credentials.add(1, "");
        }
        // Set saved credentials
        emailField.setText(credentials.get(0));
        passwordField.setText(credentials.get(1));

        // On enter, switch text field to password
        emailField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent enter) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    passwordField.requestFocus();
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
        if (rememberUser.isSelected()) {
            file.saveCredentials(localFile, emailField.getText(), passwordField.getText());
        } else {
            localFile.delete();
        }
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
