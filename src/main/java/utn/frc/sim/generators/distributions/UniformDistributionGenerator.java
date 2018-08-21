package utn.frc.sim.generators.distributions;

import utn.frc.sim.generators.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class UniformDistributionGenerator extends BaseRandomGenerator {


    private final double a;
    private final double b;

    private UniformDistributionGenerator(double a, double b) {
        this.a = a;
        this.b = b;
    }

    public static UniformDistributionGenerator createOf(double a, double b) {
        return new UniformDistributionGenerator(a, b);
    }


    @Override
    public double random() {
        return a + Math.random() * (b - a);
    }

    @Override
    public DistributionValueGenerator getDistribution() {
        return new UniformValueGenerator();
    }

    private class UniformValueGenerator implements DistributionValueGenerator{

        @Override
        public double get(double from, double to) {
            return (to - from) / (b - a);
        }
    }

}
