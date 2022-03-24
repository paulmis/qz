package client.scenes;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.Generated;

/**
 * The controller for the GameLeaveScreen.
 * It handles all the actions of the GameLeaveScreen.
 */
@Generated
public class GameLeaveScreenCtrl implements Initializable {

    /**
     * The disband handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the disband button.
     */
    public interface DisbandHandler {
        void handle();
    }

    @FXML private JFXButton cancelButton;
    @FXML private JFXButton disbandButton;
    @FXML private AnchorPane rootPane;
    private DisbandHandler disbandHandler;

    /**
     * The constructor for the GameLeaveScreen controller.
     *
     * @param disbandHandler
     */
    public GameLeaveScreenCtrl(DisbandHandler disbandHandler) {
        this.disbandHandler = disbandHandler;
    }

    /**
     * This function handles the cancel button click.
     */
    @FXML
    private void cancel() {
        //ToDo: Close popup and do nothing.
    }

    /**
     * This function handles the disband button click.
     */
    @FXML
    private void disband() {
        disbandHandler.handle();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * This function makes the background of the control translucent.
     */
    public void makeTranslucent() {
        this.rootPane.setStyle("-fx-background-color: translucent; "
                + "-fx-background-radius: 0; "
                + "-fx-effect: none;");
    }
}
