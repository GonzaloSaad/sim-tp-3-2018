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

public class ExpNegController {

    private static final double SPINNER_DOUBLE_MIN_VALUE = 0.01;
    private static final double SPINNER_DOUBLE_MAX_VALUE = 10000000;
    private static final double SPINNER_DOUBLE_LAMBDA_INITIAL_VALUE = 4.00;
    private static final double SPINNER_DOUBLE_STEP_VALUE = 0.05;
    private static final int SPINNER_NO_INCREMENT_STEP = 0;


    @FXML
    private TextField spnLambda;

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
        spnLambda.setText(String.valueOf(SPINNER_DOUBLE_LAMBDA_INITIAL_VALUE));
        spnLambda.textProperty().addListener(getListenerForText(spnLambda));
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
     * Metodo que retorna el valor de lambda que se ingreso.
     */
    public String getLambda() {
        return spnLambda.getText();
    }

    /**
     * Metodo que verifica si los valores del modelo son validos.
     */
    public void validateValues() throws NumberFormatException {
        if (!spnLambda.getText().matches(DoubleUtils.regex))
            throw new NumberFormatException("Se debe ingresar un numero con formato valido para lambda.");
        if (Double.parseDouble(spnLambda.getText()) < 0)
            throw new NumberFormatException("Lambda no puede ser negativo.");
        if (Double.parseDouble(spnLambda.getText()) == 0)
            throw new NumberFormatException("Lambda no puede ser 0.");
        if (Double.parseDouble(spnLambda.getText()) < SPINNER_DOUBLE_MIN_VALUE)
            throw new NumberFormatException("Lambda no puede ser menor que " + SPINNER_DOUBLE_MIN_VALUE);
        if (Double.parseDouble(spnLambda.getText()) > SPINNER_DOUBLE_MAX_VALUE)
            throw new NumberFormatException("Lambda no puede ser mayor que " + SPINNER_DOUBLE_MAX_VALUE);
    }
}
