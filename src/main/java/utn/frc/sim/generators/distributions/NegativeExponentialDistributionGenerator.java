package utn.frc.sim.generators.distributions;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

import static java.lang.Math.log;

/**
 * Clase que representa un generador exponencial negativo.
 */
public class NegativeExponentialDistributionGenerator extends BaseRandomGenerator {

    private final double lambda;

    private NegativeExponentialDistributionGenerator(double lambda) {
        this.lambda = lambda;
    }

    /**
     * Metodo para crear un generador con parametros lambda
     * Patron factory method.
     */
    public static NegativeExponentialDistributionGenerator createOf(double lambda) {
        return new NegativeExponentialDistributionGenerator(lambda);
    }

    /**
     * Metodo que genera un numero pseudo aleatorio de
     * distribucion exponencial negativa.
     * La formula usada es:
     * x = -(1/lambda) * log( 1 - u)
     * donde u es un numero aleatorio del
     * intervalo [0,1).
     */
    @Override
    public double random() {
        return (-(1 / lambda) * log(1 - Math.random()));
    }

    /**
     * Metodo que devuelve la distribucion del generador.
     */
    @Override
    public DistributionValueGenerator getDistribution() {
        return new NegativeExponentialValueGenerator();
    }

    /**
     * Clase que representa la distribucion del generador.
     */
    private class NegativeExponentialValueGenerator implements DistributionValueGenerator{

        /**
         * Metodo que calcula la frecuencia esperada para un intervalo
         * en una distribucion exponencial negativa.
         */
        @Override
        public double getExpectedFrequency(double from, double to) {
            ExponentialDistribution distribution = new ExponentialDistribution(lambda);
            double upper = distribution.cumulativeProbability(to);
            double lower = distribution.cumulativeProbability(from);
            return upper - lower;
        }
    }


}
