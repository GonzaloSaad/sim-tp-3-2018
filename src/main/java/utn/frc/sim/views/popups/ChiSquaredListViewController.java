package utn.frc.sim.views.popups;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import utn.frc.sim.util.SimStringUtils;
import java.util.List;
import java.util.stream.Collectors;

public class ChiSquaredListViewController {

    @FXML
    private ListView<String> lstNumbers;


    /**
     * Metodo que toma una lista de numeros y los
     * setea en la lista.
     */
    public void setListToListView(List<Double> listToAdd) {
        ObservableList<String> items = lstNumbers.getItems();
        items.clear();
        items.addAll(listToAdd.stream().map(SimStringUtils::getDoubleWithFourPlaces).collect(Collectors.toList()));
    }

}
