package utn.frc.sim.generators.distributions;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import utn.frc.sim.generators.RandomGenerator;

import static java.lang.Math.log;

public class NegativeExponentialDistributionGenerator extends BaseRandomGenerator {

    private final double lambda;

    private NegativeExponentialDistributionGenerator(double lambda) {
        this.lambda = lambda;
    }

    public static NegativeExponentialDistributionGenerator createOf(double lambda) {
        return new NegativeExponentialDistributionGenerator(lambda);
    }

    @Override
    public double random() {
        return (-(1 / lambda) * log(1 - Math.random()));
    }

    @Override
    public DistributionValueGenerator getDistribution() {
        return new NegativeExponentialValueGenerator();
    }

    private class NegativeExponentialValueGenerator implements DistributionValueGenerator{

        @Override
        public double get(double from, double to) {
            ExponentialDistribution distribution = new ExponentialDistribution(lambda);
            double upper = distribution.cumulativeProbability(to);
            double lower = distribution.cumulativeProbability(from);
            return upper - lower;
        }
    }


}
