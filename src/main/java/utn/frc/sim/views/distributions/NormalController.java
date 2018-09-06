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

public class NormalController {
    private static final double SPINNER_DOUBLE_MIN_VALUE = 0.0001;
    private static final double SPINNER_DOUBLE_MAX_VALUE = 10000000;
    private static final double SPINNER_DOUBLE_INITIAL_VALUE = 0.10;
    private static final double SPINNER_DOUBLE_MEAN_INITIAL_VALUE = 0;
    private static final double SPINNER_DOUBLE_SD_INITIAL_VALUE = 1;
    private static final double SPINNER_DOUBLE_STEP_VALUE = 0.05;
    private static final int SPINNER_NO_INCREMENT_STEP = 0;


    @FXML
    private TextField spnMean;

    @FXML
    private TextField spnSd;

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
        spnMean.setText((String.valueOf(SPINNER_DOUBLE_MEAN_INITIAL_VALUE)));
        spnMean.textProperty().addListener(getListenerForText(spnMean));
        spnSd.setText(String.valueOf(SPINNER_DOUBLE_SD_INITIAL_VALUE));
        spnSd.textProperty().addListener(getListenerForText(spnSd));
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
     * Metodo que retorna la media que se ingreso.
     */
    public String getMean() {
        return spnMean.getText();
    }

    /**
     * Metodo que retorna la desviacion que se uso.
     */
    public String getStandarDeviation() {
        return spnSd.getText();
    }

    /*
     * Metodo que verifica si los valores del modelo son validos.
     */
    public void validateValues() throws NumberFormatException {
        if (!spnMean.getText().matches(DoubleUtils.regex) || !spnSd.getText().matches(DoubleUtils.regex))
            throw new NumberFormatException("Se debe ingresar un numero con formato valido para los valores A y B.");
        if (Double.parseDouble(spnSd.getText()) < 0)
            throw new NumberFormatException("La varianza no puede ser un numero negativo.");
        if (Double.parseDouble(spnMean.getText()) > SPINNER_DOUBLE_MAX_VALUE)
            throw new NumberFormatException("La mediana no puede ser mayor que " + SPINNER_DOUBLE_MAX_VALUE);
        if (Double.parseDouble(spnSd.getText()) > SPINNER_DOUBLE_MAX_VALUE)
            throw new NumberFormatException("La varianza no puede ser mayor que " + SPINNER_DOUBLE_MAX_VALUE);
        if (Double.parseDouble(spnSd.getText()) < SPINNER_DOUBLE_MIN_VALUE)
            throw new NumberFormatException("La varianza no puede ser menor que " + SPINNER_DOUBLE_MIN_VALUE);
    }

}
