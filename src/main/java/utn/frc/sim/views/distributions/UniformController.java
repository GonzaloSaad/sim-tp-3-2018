package utn.frc.sim.views.distributions;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import utn.frc.sim.util.DoubleUtils;

import java.text.DecimalFormat;
import java.text.ParseException;

public class UniformController {
    private static final double SPINNER_DOUBLE_MIN_VALUE = 0.0001;
    private static final double SPINNER_DOUBLE_MAX_VALUE = 10000000;
    private static final double SPINNER_DOUBLE_A_INITIAL_VALUE = 10;
    private static final double SPINNER_DOUBLE_B_INITIAL_VALUE = 20;
    private static final double SPINNER_DOUBLE_INITIAL_VALUE = 0.01;


    @FXML
    private TextField spnA;

    @FXML
    private TextField spnB;

    /**
     * Metodo que se ejectua luego de la inicializacion de los
     * componentes FXML.
     */
    @FXML
    public void initialize() {
        initializeSpinners();
    }


    /**
     * Metodo inicializador de los spinners de cantidad de intervalos, numeros y alpha.
     */
    private void initializeSpinners() {
        spnA.setText(String.valueOf(SPINNER_DOUBLE_A_INITIAL_VALUE));
        spnA.textProperty().addListener(getListenerForText(spnA));
        spnB.setText(String.valueOf(SPINNER_DOUBLE_B_INITIAL_VALUE));
        spnB.textProperty().addListener(getListenerForText(spnB));
    }

    /**
     * Metodo que crea un convertidor de double a string con
     * un formato #.##
     * Sirve para customizar la cantidad de decimales del spinner
     */
    private StringConverter<Double> getStringDoubleConverter() {
        return new StringConverter<Double>() {
            private final DecimalFormat df = new DecimalFormat("#0.00");

            @Override
            public String toString(Double value) {
                if (value == null) {
                    return "";
                }

                return df.format(value);
            }

            @Override
            public Double fromString(String value) {
                try {
                    if (value == null) {
                        return SPINNER_DOUBLE_INITIAL_VALUE;
                    }

                    value = value.trim();

                    if (value.length() < 1) {
                        return SPINNER_DOUBLE_INITIAL_VALUE;
                    }

                    return df.parse(value).doubleValue();
                } catch (ParseException ex) {
                    return SPINNER_DOUBLE_INITIAL_VALUE;
                }
            }
        };
    }

    /**
     * Metodo que genera un Listener para el cambio de
     * texto de un TextField.
     */
    private ChangeListener<String> getListenerForText(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches(DoubleUtils.regex)) {
                textField.setText(newValue.replaceAll(DoubleUtils.regex, ""));
            }
        };
    }

    /**
     * Metodo que retorna el valor del limite inferior.
     */
    public String getA() {
        return spnA.getText();
    }

    /**
     * Metodo que retorna el valor del limite superior.
     */
    public String getB() {
        return spnB.getText();
    }

    /**
     * Metodo que verifica si los valores del modelo son validos.
     */
    public void validateValues() throws NumberFormatException {
        if (!spnA.getText().matches(DoubleUtils.regex) || !spnB.getText().matches(DoubleUtils.regex))
            throw new NumberFormatException("Se debe ingresar un numero con formato valido para los valores A y B.");
        if (Double.parseDouble(spnA.getText()) > Double.parseDouble(spnB.getText()))
            throw new NumberFormatException("A no puede ser mayor que B.");
        if (Double.parseDouble(spnA.getText()) > SPINNER_DOUBLE_MAX_VALUE)
            throw new NumberFormatException("A no puede ser mayor que " + SPINNER_DOUBLE_MAX_VALUE);
        if (Double.parseDouble(spnB.getText()) > SPINNER_DOUBLE_MAX_VALUE)
            throw new NumberFormatException("B no puede ser mayor que " + SPINNER_DOUBLE_MAX_VALUE);
        if (Double.parseDouble(spnA.getText()) < SPINNER_DOUBLE_MIN_VALUE)
            throw new NumberFormatException("A no puede ser menor que " + SPINNER_DOUBLE_MIN_VALUE);
        if (Double.parseDouble(spnB.getText()) < SPINNER_DOUBLE_MIN_VALUE)
            throw new NumberFormatException("B no puede ser menor que " + SPINNER_DOUBLE_MIN_VALUE);
    }
}
