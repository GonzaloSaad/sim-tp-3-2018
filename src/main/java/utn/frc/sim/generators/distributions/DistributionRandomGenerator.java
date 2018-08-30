package utn.frc.sim.generators.distributions;

import utn.frc.sim.generators.RandomGenerator;
import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

public interface DistributionRandomGenerator extends RandomGenerator {
    DistributionValueGenerator getDistribution();
}
