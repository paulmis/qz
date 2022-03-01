package client.scenes.lobby.configuration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

/**
 * The controller for a configuration element.
 * This control is to be used inside of a ConfigurationScreen.
 */
public class ConfigurationElementCtrl implements Initializable {

    @FXML
    private Label elementLabel;

    @FXML
    private Spinner<Double> valueSpinner;

    @FXML
    private Label viewLabel;

    private final SimpleFloatProperty value;

    private final boolean editable;

    private final Float initialValue;
    private final Float minValue;
    private final Float maxValue;

    private final String text;

    private final Class type;


    /**
     * The constructor of the ConfigurationElement Controller.
     *
     * @param text The text description of the property.
     * @param value The initial value of the property.
     * @param minValue The minimum value that this property can hold.
     * @param maxValue The maximum value that this property can hold.
     * @param editable True if the control should be able to edit the property and false if not.
     * @param type The type of the underlying property.
     */
    public ConfigurationElementCtrl(String text,
                                    Float value,
                                    Float minValue,
                                    Float maxValue,
                                    boolean editable,
                                    Class type) {
        this.value = new SimpleFloatProperty(value);
        this.editable = editable;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.initialValue = value;
        this.text = text;
        this.type = type;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Sets the text of the view label
        // Makes sure no extra decimals are shown.
        this.viewLabel.setText((
                type == Integer.class
                        ?
                        Integer.valueOf(initialValue.intValue()).toString() :
                        initialValue.toString()
                ));

        this.elementLabel.setText(this.text);


        // Hides/ Shows the view/ editing controls.
        this.valueSpinner.setVisible(editable);
        this.viewLabel.setVisible(!editable);

        this.valueSpinner.setEditable(editable);

        // This is a spinner value factory.
        // It handles the min,max and step size of the spinner.
        SpinnerValueFactory<Double> svf =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        minValue, maxValue, initialValue, type == Integer.class ? 1 : 0.1);

        valueSpinner.setValueFactory(svf);

        // This makes sure that when the user presses enter after changing the value,
        // the changes are shown and the text is verified to be in the correct format.
        valueSpinner.getEditor().setOnAction(action -> {
            String text = valueSpinner.getEditor().getText();
            SpinnerValueFactory<Double> valueFactory = valueSpinner.getValueFactory();
            if (valueFactory != null) {
                StringConverter<Double> converter = valueFactory.getConverter();
                if (converter != null) {
                    Double value = converter.fromString(text);
                    valueFactory.setValue(value);
                }
            }
        });

        // Binds the spinner to the volume so it can be exposed later.
        value.bind(valueSpinner.valueProperty());
    }

    /**
     * A getter for the value.
     * The purpose of this is to expose the value further
     * such that other objects can bind on it.
     *
     * @return the bindable float value.
     */
    public SimpleFloatProperty valueProperty() {
        return value;
    }
}
