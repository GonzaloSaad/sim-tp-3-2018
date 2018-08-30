package utn.frc.sim.generators;

import java.util.List;

public interface RandomGenerator {
    double random();
    List<Double> random(int n);
}
