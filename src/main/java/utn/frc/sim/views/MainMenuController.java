package utn.frc.sim.views;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import utn.frc.sim.util.DoubleUtils;
import utn.frc.sim.views.distributions.ExpNegController;
import utn.frc.sim.views.distributions.NormalController;
import utn.frc.sim.views.distributions.UniformController;
import utn.frc.sim.views.popups.ChiSquaredListViewController;
import utn.frc.sim.views.popups.ChiSquaredTableViewController;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class MainMenuController {

    private ExecutorService executorService;
    private Future<?> futureOfExecution;
    private static final Logger logger = LogManager.getLogger(MainMenuController.class);

    private static final String X_AXIS_LABEL = "Intervalos.";
    private static final String Y_AXIS_LABEL = "Frecuencia relativa.";
    private static final int SPINNER_INTEGER_MIN_VALUE = 10;
    private static final int MAX_AMOUNT_NUMBERS = 1000000;
    private static final int MAX_AMOUNT_INTERVALS = 50;
    private static final String NORMAL_DISTRIBUTION = "NORMAL";
    private static final String UNIFORM_DISTRIBUTION = "UNIFORME";
    private static final String NEG_EXP_DISTRIBUTION = "EXP. NEG.";
    private static final int COMBO_BOX_FIRST_ELEMENT = 0;
    private static final String RELATIVE_FREQUENCY_SERIES_LABEL = "Frecuencia Relativa.";
    private static final String HO_ACCEPTED = "NO RECHAZADA";
    private static final String HO_REJECTED = "RECHAZADA";
    private static final double SPINNER_DOUBLE_MIN_VALUE = 0.01;
    private static final int NORMAL_DEGREES_OF_FREEDOM = 2;
    private static final int NEG_EXP_DEGREES_OF_FREEDOM = 1;
    private static final int UNIFORM_DEGREES_OF_FREEDOM = 2;
    private static final int PLACES = 4;
    private static final String STATE_PROCESSING = "Procesando...";
    private static final String STATE_INTERRUPTED = "Interrumpido.";
    private static final String STATE_FINISHED = "Terminado.";

    private static final int THREADS = Runtime.getRuntime().availableProcessors();

    private Optional<ExpNegController> expNegController;
    private Optional<NormalController> normalController;
    private Optional<UniformController> uniformController;

    private List<Double> numbers;
    private List<Interval> intervals;

    @FXML
    private AnchorPane pnlParameters;

    @FXML
    private ComboBox<String> cmbDistribution;

    @FXML
    private TextField spnAmountOfNumbers;

    @FXML
    private TextField spnAmountOfIntervals;

    @FXML
    private TextField spnAlpha;

    @FXML
    private BarChart<String, Number> grpGraficoDeFrecuencias;

    @FXML
    private Label lblDistribution;

    @FXML
    private Label lblActual;

    @FXML
    private Label lblExpected;

    @FXML
    private Label lblHypothesis;

    @FXML
    private Hyperlink lblShowList;

    @FXML
    private Hyperlink lblShowTable;

    @FXML
    private Label lblState;

    @FXML
    private Button btnStop;

    @FXML
    private Button btnGenerar;

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
        spnAmountOfIntervals.setText(String.valueOf(SPINNER_INTEGER_MIN_VALUE));
        spnAmountOfNumbers.setText(String.valueOf(SPINNER_INTEGER_MIN_VALUE));
        spnAlpha.setText(String.valueOf(SPINNER_DOUBLE_MIN_VALUE));
        spnAmountOfIntervals.textProperty().addListener(getListenerForText(spnAmountOfIntervals));
        spnAmountOfNumbers.textProperty().addListener(getListenerForText(spnAmountOfNumbers));
        spnAlpha.textProperty().addListener(getListenerForText(spnAlpha));

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
            this.validateValues();
            if (cmbDistribution.getSelectionModel().getSelectedItem().equals("UNIFORME")) {
                uniformController.ifPresent(UniformController::validateValues);
            } else if (cmbDistribution.getSelectionModel().getSelectedItem().equals("NORMAL")) {
                normalController.ifPresent(NormalController::validateValues);
            } else {
                expNegController.ifPresent(ExpNegController::validateValues);
            }
            executorService = Executors.newFixedThreadPool(THREADS);
            futureOfExecution = executorService.submit(this::generateValuesAndAddThemToListAndGraph);

        } catch (Exception ex) {
            showErrorDialog(ex.getMessage());
        }
    }

    @FXML
    void verTablaClick(ActionEvent event) {
        loadAndSetTableView();
    }

    @FXML
    void verListaClick(ActionEvent event) {
        loadAndSetListView();
    }

    @FXML
    void btnStopClick(ActionEvent event) {
        if (futureOfExecution != null) {
            futureOfExecution.cancel(Boolean.TRUE);
            setUIToInterrumpedState();
        }
    }

    /**
     * Metodo que carga la lista de numeros.
     */
    private void loadAndSetListView() {
        loadListView().ifPresent(listController -> listController.setListToListView(numbers));
    }

    /**
     * Metodo que carga la tabla de chi cuadrado.
     */
    private void loadAndSetTableView() {
        loadTableView().ifPresent(tableController -> tableController.setItemsInTableView(intervals));
    }

    /**
     * Metodo que maneja la creacion de los numeros y el
     * posterior seteo en la lista y el grafico.
     */
    private void generateValuesAndAddThemToListAndGraph() {
        Platform.runLater(this::setUIToProcessingState);
        IntervalsCreator creator = getIntervalsCreator();

        numbers = creator.getNumbers()
                .stream()
                .map(aDouble -> DoubleUtils.round(aDouble, 4))
                .collect(Collectors.toList());
        intervals = creator.getIntervals();

        Platform.runLater(this::setResultsToUI);
        Platform.runLater(this::setUIToFinishedState);
        executorService.shutdownNow();

    }

    /**
     * Metodo que setea los resultados a los distintos
     * componentes de la UI.
     */
    private void setResultsToUI() {
        plotIntervalsInGraph(intervals);
        setResultLabels(intervals);
        setHyperlinkEnable();
    }

    /**
     * Metodo que setea a la UI en estado de procesando.
     * Deshabilita el boton de generar y habilita el de stop.
     * Setea el label de estado a procesando.
     */
    private void setUIToProcessingState() {
        enableStopButton();
        disableGenerarButton();
        setStatusLabelToProcessing();
    }

    /**
     * Metodo que setea a la UI en estado de terminado.
     * Deshabilita el boton de stop y habilita el de generar.
     * Setea el label de estado a terminado.
     */
    private void setUIToFinishedState() {
        disableStopButton();
        enableGenerarButton();
        setStatusLabelToFinished();
    }

    /**
     * Metodo que setea a la UI en estado de interrumpido.
     * Deshabilita el boton de stop y habilita el de generar.
     * Setea el label de estado a interrumpido
     */
    private void setUIToInterrumpedState() {
        disableStopButton();
        enableGenerarButton();
        setStatusLabelToInterrupted();
    }

    /**
     * Metodo que activa los hiperlinks de tabla y lista
     * y los deja como nunca visitados.
     */
    private void setHyperlinkEnable() {
        lblShowList.setVisited(Boolean.FALSE);
        lblShowList.disableProperty().setValue(Boolean.FALSE);
        lblShowTable.setVisited(Boolean.FALSE);
        lblShowTable.disableProperty().setValue(Boolean.FALSE);
    }

    /**
     * Metodo que setea todos los labels de resultado y el
     * label de distribucion.
     */
    private void setResultLabels(List<Interval> intervals) {
        setDistributionLabel();
        setResultsDisplay(intervals);
    }

    /**
     * Metodo que segun una lista de intervalos, genera
     * los labels de resultados (chi actual, chi esperado e
     * Hipotesis)
     */
    private void setResultsDisplay(List<Interval> intervals) {
        double actualChi = intervals.stream().mapToDouble(Interval::getResult).sum();
        double expectedChi = getChiSquaredTableValueFromParameters();

        setExpectedChiValue(expectedChi);
        setActualChiValue(actualChi);
        setHypothesis(actualChi, expectedChi);
    }

    /**
     * Metodo que setea el valor del label de hipostesis segun
     * se rechazo o no.
     */
    private void setHypothesis(double actualChi, double expectedChi) {

        if (Double.isNaN(actualChi)){
            lblHypothesis.setText("NaN");
        } else if (actualChi < expectedChi) {
            lblHypothesis.setText(HO_ACCEPTED);
        } else {
            lblHypothesis.setText(HO_REJECTED);
        }
    }

    /**
     * Metodo que setea el valor de chi cuadrado en el label
     * de chi actual.
     */
    private void setActualChiValue(double actualChi) {
        lblActual.setText(DoubleUtils.roundString(actualChi, PLACES));
    }

    /**
     * Metodo que setea el valor de chi cuadrado en el label
     * de chi esperado.
     */
    private void setExpectedChiValue(double expectedChi) {
        lblExpected.setText(DoubleUtils.roundString(expectedChi, PLACES));
    }

    /**
     * Metodo que obtiene el correspondiente valor de la tabla de
     * chi cuadrado en funcion de los parametros ingresados.
     */
    private double getChiSquaredTableValueFromParameters() {
        int degreesOfFreedom = Integer.valueOf(spnAmountOfIntervals.getText()) - getDistributionParameterCount() - 1;
        double alpha = Double.valueOf(spnAlpha.getText());
        return new ChiSquaredDistribution(degreesOfFreedom).inverseCumulativeProbability(1 - alpha);

    }

    /**
     * Metodo que retorna la cantidad de parametros segun
     * la distribucion se haya elegido. Normal y Uniforme
     * tiene 2 parametros mientras que la exponencial negativa
     * tiene uno solo.
     */
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

    /**
     * Metodo que setea el label de distribucion segund la
     * seleccion del combo de distribucion.
     */
    private void setDistributionLabel() {
        lblDistribution.setText(cmbDistribution.getValue());
    }

    /**
     * Metodo que toma una lista de intervalos y las plotea en el grafico.
     */
    private void plotIntervalsInGraph(List<Interval> listOfIntervals) {

        XYChart.Series<String, Number> relative = new XYChart.Series<>();
        relative.setName(RELATIVE_FREQUENCY_SERIES_LABEL);

        for (Interval interval : listOfIntervals) {
            relative.getData().add(new XYChart.Data<>(interval.getDisplayableInterval(), interval.getObservedFrequency()));
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
        return Integer.valueOf(spnAmountOfIntervals.getText());
    }

    private int getAmountOfNumbers() {
        return Integer.valueOf(spnAmountOfNumbers.getText());
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
        double lambda = Double.parseDouble(controller.getLambda());
        return NegativeExponentialDistributionGenerator.createOf(lambda);
    }

    private DistributionRandomGenerator getUniformGenerator() {
        UniformController controller = uniformController.orElseThrow(IllegalStateException::new);
        double a = Double.parseDouble(controller.getA());
        double b = Double.parseDouble(controller.getB());
        return UniformDistributionGenerator.createOf(a, b);
    }

    private DistributionRandomGenerator getNormalGenerator() {
        NormalController controller = normalController.orElseThrow(IllegalStateException::new);
        double mean = Double.parseDouble(controller.getMean());
        double sd = Double.parseDouble(controller.getStandarDeviation());
        return NormalDistributionGenerator.createOf(mean, sd);
    }

    /**
     * Metodo que carga la vista de la tabla y la genera como popup.
     */
    private Optional<ChiSquaredTableViewController> loadTableView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popups/chi-squared-table.fxml"));
            openNewDialog(loader.load());
            return Optional.ofNullable(loader.getController());

        } catch (IOException e) {
            logger.error("Problem opening table.", e);
            return Optional.empty();
        }
    }

    /**
     * Metodo que carga la vista de la lista ya lgenera como popup.
     */
    private Optional<ChiSquaredListViewController> loadListView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popups/list-view.fxml"));
            openNewDialog(loader.load());
            return Optional.ofNullable(loader.getController());

        } catch (IOException e) {
            logger.error("Problem opening list view.", e);
            return Optional.empty();
        }
    }

    /**
     * Metodo que abre un nuevo dialogo con una escena.
     */
    private void openNewDialog(Parent parent) {
        final Stage dialog = new Stage();
        Scene scene = new Scene(parent);
        dialog.setScene(scene);
        dialog.setResizable(Boolean.FALSE);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.show();
    }

    private void showErrorDialog(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void validateValues() throws NumberFormatException {
        if (!spnAmountOfNumbers.getText().matches(DoubleUtils.regex) || !spnAmountOfIntervals.getText().matches(DoubleUtils.regex))
            throw new NumberFormatException("Se debe ingresar un numero con formato valido para la cantidad de numeros y los intervalos.");
        if (Double.parseDouble(spnAmountOfNumbers.getText()) > MAX_AMOUNT_NUMBERS)
            throw new NumberFormatException("La cantidad de numeros no puede ser mayor a " + MAX_AMOUNT_NUMBERS);
        if (Double.parseDouble(spnAmountOfIntervals.getText()) > MAX_AMOUNT_INTERVALS)
            throw new NumberFormatException("La cantidad de intervalos no puede ser mayor a " + MAX_AMOUNT_INTERVALS);
        if (Double.parseDouble(spnAmountOfNumbers.getText()) < 0)
            throw new NumberFormatException("La cantidad de numeros no puede ser menor a 0.");
        if (Double.parseDouble(spnAmountOfIntervals.getText()) < 0)
            throw new NumberFormatException("La cantidad de intervalos no puede ser menor a 0.");
    }

    private void setStatusLabelToProcessing() {
        lblState.setText(STATE_PROCESSING);
    }

    private void setStatusLabelToFinished() {
        lblState.setText(STATE_FINISHED);
    }

    private void setStatusLabelToInterrupted() {
        lblState.setText(STATE_INTERRUPTED);
    }

    private void disableStopButton() {
        btnStop.setDisable(Boolean.TRUE);
    }

    private void enableStopButton() {
        btnStop.setDisable(Boolean.FALSE);
    }

    private void disableGenerarButton() {
        btnGenerar.setDisable(Boolean.TRUE);
    }

    private void enableGenerarButton() {
        btnGenerar.setDisable(Boolean.FALSE);
    }

}
