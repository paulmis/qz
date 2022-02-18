package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import org.checkerframework.checker.units.qual.A;

/**
 * Game Screen Controller.
 */
public class MultipleChoiceQuestionCtrl implements Initializable {

    @FXML
    private Label questionLabel;

    @FXML
    private ImageView imageOptionA;
    @FXML
    private ImageView imageOptionB;
    @FXML
    private ImageView imageOptionC;
    @FXML
    private ImageView imageOptionD;


    @FXML
    private Label labelOptionA;
    @FXML
    private Label labelOptionB;
    @FXML
    private Label labelOptionC;
    @FXML
    private Label labelOptionD;

    @FXML
    private JFXButton buttonOptionA;
    @FXML
    private JFXButton buttonOptionB;
    @FXML
    private JFXButton buttonOptionC;
    @FXML
    private JFXButton buttonOptionD;


    @FXML
    private VBox imageVBox;

    private final String questionText;
    private final List<URL> answersImages;
    private final List<String> answersText;
    private final List<EventHandler<ActionEvent>> actions;

    /** Constructs the controller for multiple choice question type.
     *
     * @param questionText The question in string format.
     * @param answersImages A list of url images of the answers.
     * @param answersText A list of texts of the answers.
     * @param actions A list of actions that each option should perform.
     */
    public MultipleChoiceQuestionCtrl(String questionText,
                                      List<URL> answersImages,
                                      List<String> answersText,
                                      List<EventHandler<ActionEvent>> actions) {
        this.questionText = questionText;
        this.answersImages = answersImages;
        this.answersText = answersText;
        this.actions = actions;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.questionLabel.setText(questionText);

        var imageOptionArray = Arrays.asList(imageOptionA, imageOptionB, imageOptionC, imageOptionD);
        var labelOptionArray = Arrays.asList(labelOptionA, labelOptionB, labelOptionC, labelOptionD);
        var buttonOptionArray = Arrays.asList(buttonOptionA, buttonOptionB, buttonOptionC, buttonOptionD);

        for (int i = 0; i < imageOptionArray.size(); i++) {
            var imageOption = imageOptionArray.get(i);
            var imageUrl = answersImages.get(i);
            imageOption.setImage(new Image(imageUrl.toString()));
            imageOption.fitHeightProperty().bind(Bindings.min(
                    imageVBox.widthProperty(),
                    imageVBox.heightProperty()).multiply(0.8));
            imageOption.fitWidthProperty().bind(imageOption.fitHeightProperty());

            var labelOption = labelOptionArray.get(i);
            var answerText = answersText.get(i);
            labelOption.setText(answerText);

            var buttonOption = buttonOptionArray.get(i);
            var action = actions.get(i);
            buttonOption.setOnAction(action);

        }
    }
}
