package client.scenes.authentication;

import java.net.URL;
import java.util.ResourceBundle;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
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

    /**
     * The interface for the answer handler.
     * Handles the change text event.
     */
    public interface AnswerHandler {
        /**
         * Handle function of the interface.
         * Deals with the change of the text field.
         *
         * @param text The text contexts of the field.
         */
        void handle(String text);
    }

    private final String email;
    private final String password;
    private final AnswerHandler answerHandler;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button signUpButton;

    @FXML
    private Button haveAccountButton;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    /**
     * Constructor for the estimate question control.
     *
     * @param email The email of the user
     * @param password The password of the user
     * @param answerHandler The action that is to be performed when the player answers.
     */
    public RegisterScreenCtrl(String email, String password, AnswerHandler answerHandler, ServerUtils server, MainCtrl mainCtrl) {
        this.email = email;
        this.password = password;
        this.answerHandler = answerHandler;
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
               String ans = emailField.getText();
               if(ans.length()>0)
                   System.out.println("Da");
           });
    }
}
