package utn.frc.sim.generators;

import utn.frc.sim.generators.distributions.DistributionValueGenerator;

import java.util.List;

public interface RandomGenerator {
    double random();
    List<Double> random(int n);
}
