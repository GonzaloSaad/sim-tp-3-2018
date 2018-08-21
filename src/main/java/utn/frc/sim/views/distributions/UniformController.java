package utn.frc.sim.views.distributions;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class UniformController {
    private static final double SPINNER_DOUBLE_MIN_VALUE = Integer.MIN_VALUE;
    private static final double SPINNER_DOUBLE_MAX_VALUE = Integer.MAX_VALUE;
    private static final double SPINNER_DOUBLE_A_INITIAL_VALUE = 50;
    private static final double SPINNER_DOUBLE_B_INITIAL_VALUE = 100;
    private static final double SPINNER_DOUBLE_STEP_VALUE = 0.05;
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
     * Metodo inicializador de los spinners de cantidad de intervalos y numeros.
     */
    private void initializeSpinners() {
        spnA.setValueFactory(getDoubleValueFactory(SPINNER_DOUBLE_A_INITIAL_VALUE));
        spnA.focusedProperty().addListener(getListenerForChangeValue(spnA));
        spnB.setValueFactory(getDoubleValueFactory(SPINNER_DOUBLE_B_INITIAL_VALUE));
        spnB.focusedProperty().addListener(getListenerForChangeValue(spnB));

    }

    /**
     * Metodo que contruye fabrica de valores para decimales.
     */
    private SpinnerValueFactory<Double> getDoubleValueFactory(double initialValue) {
        return new SpinnerValueFactory.DoubleSpinnerValueFactory(SPINNER_DOUBLE_MIN_VALUE,
                SPINNER_DOUBLE_MAX_VALUE,
                initialValue,
                SPINNER_DOUBLE_STEP_VALUE);
    }

    /**
     * Metodo que genera un listener para perdida de focus, que se usa
     * para compensar el bug de JavaFX en setear el valor al spinner cuando
     * es editado.
     */
    private <T> ChangeListener<? super Boolean> getListenerForChangeValue(Spinner<T> spinner) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                spinner.increment(SPINNER_NO_INCREMENT_STEP);
            }
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
}
