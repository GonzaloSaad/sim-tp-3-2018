package utn.frc.sim.generators.distributions;

import org.apache.commons.math3.distribution.NormalDistribution;
import utn.frc.sim.generators.RandomGenerator;

import java.lang.Math.*;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.StrictMath.sqrt;

public class NormalDistributionGenerator extends BaseRandomGenerator {

    private final double mu;
    private final double sigma;

    private NormalDistributionGenerator(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    public static NormalDistributionGenerator createOf(double mu, double sigma) {
        return new NormalDistributionGenerator(mu, sigma);
    }

    @Override
    public double random() {
        return mu + getZValue() * sigma;
    }

    private double getZValue() {
        return sqrt(-2 * log(Math.random())) * cos(2 * PI * Math.random());
    }

    @Override
    public DistributionValueGenerator getDistribution() {
        return new NormalValueGenerator();
    }

    private class NormalValueGenerator implements DistributionValueGenerator {

        @Override
        public double get(double from, double to) {
            NormalDistribution distribution = new NormalDistribution(mu, sigma);
            double upper = distribution.cumulativeProbability(to);
            double lower = distribution.cumulativeProbability(from);
            return upper - lower;
        }
    }
}
