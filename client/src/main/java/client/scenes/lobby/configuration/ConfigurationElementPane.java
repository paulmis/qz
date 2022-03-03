package client.scenes.lobby.configuration;

import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * The class that encompasses the ConfigurationElement.
 * The purpose of this class is to allow the
 * initialization of the control inside code.
 */
public class ConfigurationElementPane extends StackPane {
    private Node view;
    private ConfigurationElementCtrl controller;

    /**
     * The constructor for the ConfigurationElementPane.
     * Creates a view of the ConfigurationElement object
     * and places it inside the children of the object.
     *
     * @param text The text description of the property.
     * @param value The initial value of the property.
     * @param minValue The minimum value that this property can hold.
     * @param maxValue The maximum value that this property can hold.
     * @param editable True if the control should be able to edit the property and false if not.
     * @param type The type of the underlying property.
     */
    public ConfigurationElementPane(String text, Float value, Float minValue, Float maxValue, boolean editable,
                                    Class type) {
        // Creates the fxml loader
        FXMLLoader fxmlLoader =
                new FXMLLoader(getClass().getResource("/client/scenes/lobby/configuration/ConfigurationElement.fxml"));

        // Sets the controller factory of the screen
        fxmlLoader.setControllerFactory(param ->
                controller = new ConfigurationElementCtrl(text, value, minValue, maxValue, editable, type));

        // Loads the fxml
        try {
            view = (Node) fxmlLoader.load();
        } catch (Exception e) {
            System.out.println(e);
        }

        // Adds it to the children of the view
        getChildren().add(view);
    }

    public SimpleFloatProperty valueProperty() {
        return controller.valueProperty();
    }
}
