package client.scenes.lobby.configuration;

import com.jfoenix.controls.JFXButton;
import commons.entities.game.configuration.GameConfigurationDTO;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.NonNull;


/**
 * The controller for the ConfigurationScreen.
 * It handles all the actions of the ConfigurationScreen.
 */
public class ConfigurationScreenCtrl implements Initializable {

    /**
     * The save handler interface.
     * The purpose of this is to allow passing of a
     * function to this controller.
     * This function will be later applied by the user when clicking
     * the save button.
     */
    public interface SaveHandler {
        void handle(GameConfigurationDTO config);
    }

    private GameConfigurationDTO gameConfig;
    private boolean editable;
    private SaveHandler saveHandler;

    @FXML
    private FlowPane mainPane;

    @FXML
    private JFXButton saveButton;

    public ConfigurationScreenCtrl(GameConfigurationDTO gameConfig) {
        this.gameConfig = gameConfig;
        this.editable = false;
    }

    /**
     * The constructor for the ConfigurationScreen controller.
     *
     * @param gameConfig the game configuration of the game.
     * @param saveHandler the action that is to be performed when the user saves the config.
     */
    public ConfigurationScreenCtrl(GameConfigurationDTO gameConfig, SaveHandler saveHandler) {
        this.gameConfig = gameConfig;
        this.editable = true;
        this.saveHandler = saveHandler;
    }

    /**
     * This function returns all the fields
     * inside the config object that have
     * the Description annotation applied.
     * Using reflection.
     *
     * @param config the game config
     * @return the list of annotated fields
     */
    private List<Field> getAnnotatedFields(@NonNull GameConfigurationDTO config) {
        var fields = new ArrayList<Field>();
        Class currentClass = config.getClass();
        while (currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields.stream()
                .filter(field -> field.isAnnotationPresent(Description.class))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to get the text
     * description from a field by accessing the
     * Description annotation.
     *
     * @param field the requested field.
     * @return the string description of the field.
     */
    private String getTextDescription(@NonNull Field field) {
        return field.getAnnotation(Description.class).value();
    }

    /**
     * Helper method to get the minimum value
     * that a field can hold by accessing the
     * DecimalMin annotation.
     *
     * @param field the requested field.
     * @return the minimum value that this field can hold.
     */
    private Float getMinValue(@NonNull Field field) {
        if (!field.isAnnotationPresent(DecimalMin.class)) {
            throw new IllegalArgumentException();
        }
        return Float.parseFloat(field.getAnnotation(DecimalMin.class).value());
    }

    /**
     * Helper method to get the maximum value
     * that a field can hold by accessing the
     * DecimalMax annotation.
     *
     * @param field the requested field.
     * @return the maximum value that this field can hold.
     */
    private Float getMaxValue(@NonNull Field field) {
        if (!field.isAnnotationPresent(DecimalMax.class)) {
            throw new IllegalArgumentException();
        }
        return Float.parseFloat(field.getAnnotation(DecimalMax.class).value());
    }

    /**
     * This function generates a ConfigurationElement
     * from a field.
     *
     * @param field the field that the element will be based on.
     * @return the generated ConfigurationElement.
     */
    private Node generateElementFromField(@NonNull Field field) {
        try {

            // This sets the field as accessible so we can set and get the field.
            field.setAccessible(true);

            // This calls the constructor of the element with the extracted annotations and data.
            var ele = new ConfigurationElementPane(getTextDescription(field),
                    Float.parseFloat(field.get(gameConfig).toString()),
                    getMinValue(field),
                    getMaxValue(field),
                    editable,
                    field.getType()
            );

            // Makes sure that when the value is changed inside
            // the element, the object is also automatically changed.
            ele.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    if (field.getType().equals(Float.class)) {
                        field.set(gameConfig, newValue.floatValue());
                    } else if (field.getType().equals(Integer.class)) {
                        field.set(gameConfig, newValue.intValue());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
            return ele;
        } catch (Exception e) {
            return new StackPane();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hides the save button when the config is not editable.
        saveButton.setVisible(editable);
        initializeChildren();
    }

    /**
     * This function adds all the elements to the
     * flow pane of the control.
     */
    private void initializeChildren() {
        mainPane.getChildren().clear();
        mainPane.getChildren().addAll(
                getAnnotatedFields(gameConfig)
                        .stream()
                        .map(this::generateElementFromField)
                        .collect(Collectors.toList()));
    }

    /**
     * This function handles the save button click.
     */
    @FXML
    private void saveConfig() {
        saveHandler.handle(this.gameConfig);
    }
}
