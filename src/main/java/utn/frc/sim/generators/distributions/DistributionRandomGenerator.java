package utn.frc.sim.generators.distributions;

import utn.frc.sim.generators.RandomGenerator;

public interface DistributionRandomGenerator extends RandomGenerator {
    DistributionValueGenerator getDistribution();
}
