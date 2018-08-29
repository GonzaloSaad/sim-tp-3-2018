package utn.frc.sim.views;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.generators.distributions.NegativeExponentialDistributionGenerator;
import utn.frc.sim.generators.distributions.NormalDistributionGenerator;
import utn.frc.sim.generators.distributions.UniformDistributionGenerator;
import utn.frc.sim.generators.intervals.Interval;
import utn.frc.sim.generators.intervals.IntervalsCreator;
import utn.frc.sim.util.MathUtils;
import utn.frc.sim.util.SimStringUtils;
import utn.frc.sim.views.distributions.ExpNegController;
import utn.frc.sim.views.distributions.NormalController;
import utn.frc.sim.views.distributions.UniformController;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainMenuController {

    private static final Logger logger = LogManager.getLogger(MainMenuController.class);

    private static final String X_AXIS_LABEL = "Intervalos.";
    private static final String Y_AXIS_LABEL = "Frecuencia relativa.";
    private static final int SPINNER_INTEGER_MIN_VALUE = 2;
    private static final int SPINNER_INTEGER_MAX_VALUE = Integer.MAX_VALUE;
    private static final int SPINNER_NO_INCREMENT_STEP = 0;
    private static final String NORMAL_DISTRIBUTION = "NORMAL";
    private static final String UNIFORM_DISTRIBUTION = "UNIFORME";
    private static final String NEG_EXP_DISTRIBUTION = "EXP. NEG.";
    private static final int COMBO_BOX_FIRST_ELEMENT = 0;
    private static final String RELATIVE_FREQUENCY_SERIES_LABEL = "Frecuencia Relativa.";
    private static final String HO_ACCEPTED = "NO RECHAZADA";
    private static final String HO_REJECTED = "RECHAZADA";
    private static final double SPINNER_DOUBLE_MIN_VALUE = 0.01;
    private static final double SPINNER_DOUBLE_MAX_VALUE = 1;
    private static final double SPINNER_DOUBLE_INITIAL_VALUE = 0.01;
    private static final double SPINNER_DOUBLE_STEP_VALUE = 0.01;
    private static final int NORMAL_DEGREES_OF_FREEDOM = 2;
    private static final int NEG_EXP_DEGREES_OF_FREEDOM = 1;
    private static final int UNIFORM_DEGREES_OF_FREEDOM = 2;
    private static final int PLACES = 4;


    private Optional<ExpNegController> expNegController;
    private Optional<NormalController> normalController;
    private Optional<UniformController> uniformController;

    @FXML
    private AnchorPane pnlParameters;

    @FXML
    private ComboBox<String> cmbDistribution;

    @FXML
    private Spinner<Integer> spnAmountOfNumbers;

    @FXML
    private Spinner<Integer> spnAmountOfIntervals;

    @FXML
    private Spinner<Double> spnAlpha;

    @FXML
    private BarChart<String, Number> grpGraficoDeFrecuencias;

    @FXML
    private Hyperlink lblShowTable;

    @FXML
    private Label lblDistribution;

    @FXML
    private Label lblActual;

    @FXML
    private Label lblExpected;

    @FXML
    private Label lblHypothesis;


    /**
     * Metodo que se ejectua luego de la inicializacion de los
     * componentes FXML.
     */
    @FXML
    public void initialize() {
        initializeFrequencyBarChartGraph();
        initializeDistributionComboBox();
        initializeSpinners();
        handleChangeInParametersPanel();
    }

    /**
     * Metodo que inicializa el grafico con las caracteristicas
     * que se necesitan. Se le quitan las animaciones.
     */
    private void initializeFrequencyBarChartGraph() {
        grpGraficoDeFrecuencias.setAnimated(Boolean.FALSE);
        grpGraficoDeFrecuencias.getXAxis().setAnimated(Boolean.FALSE);
        grpGraficoDeFrecuencias.getYAxis().setAnimated(Boolean.FALSE);
        grpGraficoDeFrecuencias.getXAxis().setLabel(X_AXIS_LABEL);
        grpGraficoDeFrecuencias.getYAxis().setLabel(Y_AXIS_LABEL);
    }

    /**
     * Metodo que inicializa los el combobox de tipo de Distribucion.
     * Se selecciona uniforme como defecto.
     */
    private void initializeDistributionComboBox() {
        cmbDistribution.getItems()
                .setAll(FXCollections.observableArrayList(UNIFORM_DISTRIBUTION, NORMAL_DISTRIBUTION, NEG_EXP_DISTRIBUTION));
        cmbDistribution.getSelectionModel().select(COMBO_BOX_FIRST_ELEMENT);
    }

    /**
     * Metodo inicializador de los spinners de cantidad de intervalos, numeros y alpha.
     */
    private void initializeSpinners() {
        spnAmountOfIntervals.setValueFactory(getIntegerValueFactory());
        spnAmountOfIntervals.focusedProperty().addListener(getListenerForChangeFocus(spnAmountOfIntervals));
        setTextFieldListenerToSpinner(spnAmountOfIntervals);
        spnAmountOfNumbers.setValueFactory(getIntegerValueFactory());
        spnAmountOfNumbers.focusedProperty().addListener(getListenerForChangeFocus(spnAmountOfNumbers));
        setTextFieldListenerToSpinner(spnAmountOfNumbers);
        spnAlpha.setValueFactory(getDoubleValueFactory());
    }

    /**
     * Metodo que contruye fabrica de valores enteros para los spinners.
     */
    private SpinnerValueFactory<Integer> getIntegerValueFactory() {
        return new SpinnerValueFactory.IntegerSpinnerValueFactory(SPINNER_INTEGER_MIN_VALUE,
                SPINNER_INTEGER_MAX_VALUE);
    }

    /**
     * Metodo que contruye fabrica de valores para decimales.
     */
    private SpinnerValueFactory<Double> getDoubleValueFactory() {
        SpinnerValueFactory<Double> factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(SPINNER_DOUBLE_MIN_VALUE,
                SPINNER_DOUBLE_MAX_VALUE,
                SPINNER_DOUBLE_INITIAL_VALUE,
                SPINNER_DOUBLE_STEP_VALUE);

        factory.setConverter(getStringDoubleConverter());
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
            if (!newValue) {
                spinner.increment(SPINNER_NO_INCREMENT_STEP);
            }
        };
    }

    @FXML
    void changeInDistributionClick(ActionEvent event) {
        handleChangeInParametersPanel();
    }

    /**
     * Metodo que maneja el evento de click.
     */
    @FXML
    void btnGenerarClick(ActionEvent event) {
        try {
            generateValuesAndAddThemToListAndGraph();
        } catch (Exception e) {
            logger.error("Error in click.", e);
        }
    }

    @FXML
    void verTablaClick(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
    }

    /**
     * Metodo que maneja la creacion de los numeros y el
     * posterior seteo en la lista y el grafico.
     */
    private void generateValuesAndAddThemToListAndGraph() {
        IntervalsCreator creator = getIntervalsCreator();

        List<Double> numbers = creator.getNumbers()
                .stream()
                .map(aDouble -> MathUtils.round(aDouble, 4))
                .collect(Collectors.toList());
        List<Interval> intervals = creator.getIntervals();


        //setListToListView(numbers);
        plotIntervalsInGraph(intervals);
        setResultLabels(intervals);
    }

    private void setResultLabels(List<Interval> intervals) {
        setDistributionLabel();
        setResultsDisplay(intervals);
    }

    private void setResultsDisplay(List<Interval> intervals) {
        double actualChi = intervals.stream().mapToDouble(Interval::getResult).sum();
        double expectedChi = getChiSquaredTableValueFromParameters();

        setExpectedChiValue(expectedChi);
        setActualChiValue(actualChi);
        setHypothesis(actualChi < expectedChi);
    }

    private void setHypothesis(boolean accepted) {
        if (accepted) {
            lblHypothesis.setText(HO_ACCEPTED);
        } else {
            lblHypothesis.setText(HO_REJECTED);
        }
    }

    private void setActualChiValue(double actualChi) {
        lblActual.setText(SimStringUtils.getDoubleStringFormat(actualChi, PLACES));
    }

    private void setExpectedChiValue(double expectedChi) {
        lblExpected.setText(SimStringUtils.getDoubleStringFormat(expectedChi, PLACES));
    }

    /**
     * Metodo que obtiene el correspondiente valor de la tabla de
     * chi cuadrado en funcion de los parametros ingresados.
     */
    private double getChiSquaredTableValueFromParameters() {
        int degreesOfFreedom = spnAmountOfIntervals.getValue() - getDistributionParameterCount() - 1;
        double alpha = spnAlpha.getValue();
        return new ChiSquaredDistribution(degreesOfFreedom).inverseCumulativeProbability(1 - alpha);

    }

    private int getDistributionParameterCount() {
        if (expNegController.isPresent()) {
            return NEG_EXP_DEGREES_OF_FREEDOM;
        } else if (uniformController.isPresent()) {
            return UNIFORM_DEGREES_OF_FREEDOM;
        } else if (normalController.isPresent()) {
            return NORMAL_DEGREES_OF_FREEDOM;
        }
        throw new IllegalStateException();
    }

    private void setDistributionLabel() {
        lblDistribution.setText(cmbDistribution.getValue());
    }

    /**
     * Metodo que toma una lista de numeros y los
     * setea en la lista.
     */
    /*private void setListToListView(List<Double> listToAdd) {
        ObservableList<Double> items = listNumbers.getItems();
        items.clear();
        items.addAll(listToAdd);
    }*/

    /**
     * Metodo que toma una lista de intervalos y las plotea en el grafico.
     */
    private void plotIntervalsInGraph(List<Interval> listOfIntervals) {

        XYChart.Series<String, Number> relative = new XYChart.Series<>();
        relative.setName(RELATIVE_FREQUENCY_SERIES_LABEL);


        for (Interval interval : listOfIntervals) {
            relative.getData().add(new XYChart.Data<>(interval.getPlottableInterval(), interval.getObservedFrequency()));
        }
        grpGraficoDeFrecuencias.getData().clear();
        grpGraficoDeFrecuencias.getData().add(relative);
    }

    /**
     * Metodo que maneja los cambios del combobox de distribucion.
     */
    private void handleChangeInParametersPanel() {
        String comboValue = cmbDistribution.getSelectionModel().getSelectedItem();

        if (comboValue.equals(NEG_EXP_DISTRIBUTION)) {
            setExponentialPanel();
        } else if (comboValue.equals(NORMAL_DISTRIBUTION)) {
            setNormalPanel();
        } else if (comboValue.equals(UNIFORM_DISTRIBUTION)) {
            setUniformPanel();
        }

    }

    /**
     * Metodo que setea la vista de los parametros de la
     * distribucion exponencial.
     */
    private void setExponentialPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/distributions/expneg.fxml"));
            pnlParameters.getChildren().setAll((AnchorPane) loader.load());
            expNegController = Optional.of(loader.getController());
            uniformController = Optional.empty();
            normalController = Optional.empty();

        } catch (IOException e) {
            logger.error("Problem opening exponential panel.", e);
        }
    }

    /**
     * Metodo que setea la vista de los parametros de la
     * distribucion uniforme.
     */
    private void setUniformPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/distributions/uniform.fxml"));
            pnlParameters.getChildren().setAll((AnchorPane) loader.load());
            uniformController = Optional.of(loader.getController());
            normalController = Optional.empty();
            expNegController = Optional.empty();

        } catch (IOException e) {
            logger.error("Problem opening uniform panel.", e);
        }
    }

    /**
     * Metodo que setea la vista de los parametros de la
     * distribucion normal.
     */
    private void setNormalPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/distributions/normal.fxml"));
            pnlParameters.getChildren().setAll((AnchorPane) loader.load());
            normalController = Optional.of(loader.getController());
            uniformController = Optional.empty();
            expNegController = Optional.empty();

        } catch (IOException e) {
            logger.error("Problem opening normal panel.", e);
        }
    }

    /**
     * Metodo que crea un IntervalsCreator en funcion de los
     * parametros que fueron ingresados.
     */
    private IntervalsCreator getIntervalsCreator() {
        return IntervalsCreator.createFor(
                getAmountOfNumbers(),
                getAmountOfIntervals(),
                getDistributionGenerator()
        );
    }

    private int getAmountOfIntervals() {
        return spnAmountOfIntervals.getValue();
    }

    private int getAmountOfNumbers() {
        return spnAmountOfNumbers.getValue();
    }

    /**
     * Metodo que maneja la logica de la construccion
     * de los generadores.
     */
    private DistributionRandomGenerator getDistributionGenerator() {
        if (expNegController.isPresent()) {
            return getNegExpGenerator();
        } else if (uniformController.isPresent()) {
            return getUniformGenerator();
        } else if (normalController.isPresent()) {
            return getNormalGenerator();
        }
        throw new IllegalStateException();
    }

    private DistributionRandomGenerator getNegExpGenerator() {
        ExpNegController controller = expNegController.orElseThrow(IllegalStateException::new);
        double lambda = controller.getLambda();
        return NegativeExponentialDistributionGenerator.createOf(lambda);
    }

    private DistributionRandomGenerator getUniformGenerator() {
        UniformController controller = uniformController.orElseThrow(IllegalStateException::new);
        double a = controller.getA();
        double b = controller.getB();
        return UniformDistributionGenerator.createOf(a, b);
    }

    private DistributionRandomGenerator getNormalGenerator() {
        NormalController controller = normalController.orElseThrow(IllegalStateException::new);
        double mean = controller.getMean();
        double sd = controller.getStandarDeviation();
        return NormalDistributionGenerator.createOf(mean, sd);
    }


}
