package utn.frc.sim.views.popups;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import utn.frc.sim.generators.intervals.Interval;

import java.util.List;

public class ChiSquaredTableViewController {

    private static final String TABLE_VIEW_INTERVAL_COLUMN_TITLE = "Intervalo";
    private static final String TABLE_VIEW_EXPECTED_FREQUENCY_COLUMN_LABEL = "FE";
    private static final String TABLE_VIEW_OBSERVED_FREQUENCY_COLUMN_LABEL = "FO";
    private static final String TABLE_VIEW_RESULT_COLUMN_LABEL = "(FE-FO)^2/FE";

    private TableView<Interval> tblIntervalTable;

    @FXML
    private TitledPane pnlChiSquare;

    @FXML
    public void initialize() {
        initializeIntervalTableView();
    }

    /**
     * Metodo que inicializa la tabla. Se generan las properties de las
     * cuales escucha cada columna para obtener su valor de la clase Interval.
     */
    private void initializeIntervalTableView() {
        tblIntervalTable = new TableView<>();

        TableColumn<Interval, String> colIntervalo = new TableColumn<>(TABLE_VIEW_INTERVAL_COLUMN_TITLE);
        colIntervalo.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDisplayableName()));

        TableColumn<Interval, String> colFE = new TableColumn<>(TABLE_VIEW_EXPECTED_FREQUENCY_COLUMN_LABEL);
        colFE.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDisplayableExpectedFrequency()));

        TableColumn<Interval, String> colFO = new TableColumn<>(TABLE_VIEW_OBSERVED_FREQUENCY_COLUMN_LABEL);
        colFO.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDisplayableObservedFrequency()));

        TableColumn<Interval, String> colResultado = new TableColumn<>(TABLE_VIEW_RESULT_COLUMN_LABEL);

        colResultado.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().getDisplayableResult()));

        tblIntervalTable.getColumns().addAll(colIntervalo, colFE, colFO, colResultado);

        pnlChiSquare.setContent(tblIntervalTable);
    }

    /**
     * Metodo que toma una lista de intervalos y los setea en la tabla.
     */
    public void setItemsInTableView(List<Interval> listOfIntervals) {
        tblIntervalTable.getItems().setAll(listOfIntervals);
    }

}
