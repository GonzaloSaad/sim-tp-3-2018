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
    private static final double SPINNER_DOUBLE_MAX_VALUE = Integer.MAX_VALUE;
    private static final double SPINNER_DOUBLE_A_INITIAL_VALUE = 10;
    private static final double SPINNER_DOUBLE_B_INITIAL_VALUE = 20;
    private static final double SPINNER_DOUBLE_STEP_VALUE = 0.05;
    private static final double SPINNER_DOUBLE_INITIAL_VALUE = 0.01;
    private static final int SPINNER_NO_INCREMENT_STEP = 0;


    @FXML
    private Spinner<Double> spnA;

    @FXML
    private Spinner<Double> spnB;

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
        spnA.setValueFactory(getDoubleValueFactory(SPINNER_DOUBLE_A_INITIAL_VALUE));
        spnA.focusedProperty().addListener(getListenerForChangeFocus(spnA));
        spnB.setValueFactory(getDoubleValueFactory(SPINNER_DOUBLE_B_INITIAL_VALUE));
        spnB.focusedProperty().addListener(getListenerForChangeFocus(spnB));
    }


    /*
     * Se encarga de que el valor que se ve y el valor del spinner sean lo mismo.
     */
    private <T> void commitEditorText(Spinner<T> spinner) {
        if (!spinner.isEditable()) return;
        String text = spinner.getEditor().getText();
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    /**
     * Metodo que contruye fabrica de valores para decimales.
     */
    private SpinnerValueFactory<Double> getDoubleValueFactory(double initialValue) {
        SpinnerValueFactory<Double> factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(SPINNER_DOUBLE_MIN_VALUE,
                SPINNER_DOUBLE_MAX_VALUE,
                initialValue,
                SPINNER_DOUBLE_STEP_VALUE);

//        factory.setConverter(getStringDoubleConverter());
        return factory;
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
     * Metodo que inserta un listener de texto de Texfield
     * a un spinner.
     */
    private void setTextFieldListenerToSpinner(Spinner spinner) {
        TextField textField = spinner.getEditor();
        textField.textProperty().addListener(getListenerForText(textField));
    }

    /**
     * Metodo que genera un Listener para el cambio de
     * texto de un TextField.
     */
    private ChangeListener<String> getListenerForText(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    /**
     * Metodo que genera un listener para perdida de focus, que se usa
     * para compensar el bug de JavaFX en setear el valor al spinner cuando
     * es editado.
     */
    private <T> ChangeListener<? super Boolean> getListenerForChangeFocus(Spinner<T> spinner) {
        return (observable, oldValue, newValue) -> {
            if (newValue) return;
            //Mergeamos el valor.
            commitEditorText(spinner);
        };
    }

    /**
     * Metodo que retorna el valor del limite inferior.
     */
    public double getA() {
        return spnA.getValue();
    }

    /**
     * Metodo que retorna el valor del limite superior.
     */
    public double getB() {
        return spnB.getValue();
    }

    /*
     * Metodo que verifica si los valores del modelo son validos.
     */
    public boolean hasValidValues() throws NumberFormatException{
        if(!spnA.getValue().toString().matches(DoubleUtils.regex) || !spnB.getValue().toString().matches(DoubleUtils.regex))
            throw new NumberFormatException("Se debe ingresar un numero con formato valido para los valores A y B.");
        if(spnA.getValue() > spnB.getValue())
                throw new NumberFormatException("A no puede ser mayor que B.");
        return true;
    }
}
