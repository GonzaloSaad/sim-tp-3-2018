package utn.frc.sim.generators.distributions;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRandomGenerator implements DistributionRandomGenerator {

    @Override
    public List<Double> random(int n) {
        List<Double> listOfRandoms = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listOfRandoms.add(random());
        }
        return listOfRandoms;
    }
}
