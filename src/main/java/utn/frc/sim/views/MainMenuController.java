package utn.frc.sim.views;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.generators.distributions.NegativeExponentialDistributionGenerator;
import utn.frc.sim.generators.distributions.NormalDistributionGenerator;
import utn.frc.sim.generators.distributions.UniformDistributionGenerator;
import utn.frc.sim.generators.intervals.Interval;
import utn.frc.sim.generators.intervals.IntervalsCreator;
import utn.frc.sim.util.MathUtils;
import utn.frc.sim.views.distributions.ExpNegController;
import utn.frc.sim.views.distributions.NormalController;
import utn.frc.sim.views.distributions.UniformController;

import java.io.IOException;
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
    private static final String COMBO_BOX_NORMAL = "Normal";
    private static final String COMBO_BOX_UNIFORM = "Uniforme";
    private static final String COMBO_BOX_NEG_EXP = "Exp. Neg.";
    private static final int COMBO_BOX_FIRST_ELEMENT = 0;
    private static final String RELATIVE_FREQUENCY_SERIES_LABEL = "Frecuencia Relativa.";

    private Optional<ExpNegController> expNegController;
    private Optional<NormalController> normalController;
    private Optional<UniformController> uniformController;

    @FXML
    private Pane pnlParameters;

    @FXML
    private ComboBox<String> cmbDistribution;

    @FXML
    private ListView<Double> listNumbers;

    @FXML
    private Spinner<Integer> spnAmountOfNumbers;

    @FXML
    private Spinner<Integer> spnAmountOfIntervals;

    @FXML
    private BarChart<String, Number> grpGraficoDeFrecuencias;

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
                .setAll(FXCollections.observableArrayList(COMBO_BOX_UNIFORM, COMBO_BOX_NORMAL, COMBO_BOX_NEG_EXP));
        cmbDistribution.getSelectionModel().select(COMBO_BOX_FIRST_ELEMENT);
    }

    /**
     * Metodo inicializador de los spinners de cantidad de intervalos y numeros.
     */
    private void initializeSpinners() {
        spnAmountOfNumbers.setValueFactory(getIntegerValueFactory());
        spnAmountOfNumbers.focusedProperty().addListener(getListenerForChangeValue(spnAmountOfNumbers));
        spnAmountOfIntervals.setValueFactory(getIntegerValueFactory());
        spnAmountOfIntervals.focusedProperty().addListener(getListenerForChangeValue(spnAmountOfIntervals));

    }

    /**
     * Metodo que contruye fabrica de valores enteros para los spinners.
     */
    private SpinnerValueFactory<Integer> getIntegerValueFactory() {
        return new SpinnerValueFactory.IntegerSpinnerValueFactory(SPINNER_INTEGER_MIN_VALUE, SPINNER_INTEGER_MAX_VALUE);
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


    /**
     * Metodo que maneja la creacion de los numeros y el
     * posterior seteo en la lista y el grafico.
     */
    private void generateValuesAndAddThemToListAndGraph() {
        IntervalsCreator creator = getIntervalsCreator();

        List<Double> numbers = creator.getNumbers()
                .stream()
                .map(aDouble -> MathUtils.round(aDouble,4))
                .collect(Collectors.toList());
        List<Interval> intervals = creator.getIntervals();

        setListToListView(numbers);
        plotIntervalsInGraph(intervals);
    }

    /**
     * Metodo que toma una lista de numeros y los
     * setea en la lista.
     */
    private void setListToListView(List<Double> listToAdd) {
        ObservableList<Double> items = listNumbers.getItems();
        items.clear();
        items.addAll(listToAdd);
    }

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

        if (comboValue.equals(COMBO_BOX_NEG_EXP)) {
            setExponentialPanel();
        } else if (comboValue.equals(COMBO_BOX_NORMAL)) {
            setNormalPanel();
        } else if (comboValue.equals(COMBO_BOX_UNIFORM)) {
            setUniformPanel();
        }

    }

    /**
     * Metodo que setea la vista de los parametros de la
     * distribucion exponencial.
     */
    private void setExponentialPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/distributions/expneg.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/distributions/uniform.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/distributions/normal.fxml"));
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
