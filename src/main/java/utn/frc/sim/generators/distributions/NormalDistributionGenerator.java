package utn.frc.sim.generators.distributions;

import org.apache.commons.math3.distribution.NormalDistribution;
import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.StrictMath.sqrt;

/**
 * Clase que representa un generador normal.
 */
public class NormalDistributionGenerator extends BaseRandomGenerator {

    private final double mu;
    private final double sigma;

    private NormalDistributionGenerator(double mu, double sigma) {
        this.mu = mu;
        this.sigma = sigma;
    }

    /**
     * Metodo para crear un generador con parametros mu (media)
     * y sigma (desviacion).
     * Patron factory method.
     */
    public static NormalDistributionGenerator createOf(double mu, double sigma) {
        return new NormalDistributionGenerator(mu, sigma);
    }

    /**
     * Metodo que genera un numero pseudo aleatorio de
     * distribucion normal.
     * La formula usada es:
     * x = mu + z * sigma
     * donde z es:
     * raiz(-2 * log (u1) * cos (2 * pi u2))
     * donde u1 y u2 son numeros aleatorios del
     * intervalo [0,1).
     */
    @Override
    public double random() {
        return mu + getZValue() * sigma;
    }

    /**
     * Metodo que realiza el calculo de z para la formula
     * de numero aleatorio.
     * La formula es:
     * raiz(-2 * log (u1) * cos (2 * pi u2))
     * donde u1 y u2 son numeros aleatorios del un
     * intervalo [0,1).
     */
    private double getZValue() {
        return sqrt(-2 * log(Math.random())) * cos(2 * PI * Math.random());
    }

    /**
     * Metodo que devuelve la distribucion del generador.
     */
    @Override
    public DistributionValueGenerator getDistribution() {
        return new NormalValueGenerator();
    }

    /**
     * Clase que representa la distribucion del generador.
     */
    private class NormalValueGenerator implements DistributionValueGenerator {

        /**
         * Metodo que calcula la frecuencia esperada para un intervalo
         * en una distribucion normal.
         */
        @Override
        public double getExpectedFrequency(double from, double to) {
            NormalDistribution distribution = new NormalDistribution(mu, sigma);
            double upper = distribution.cumulativeProbability(to);
            double lower = distribution.cumulativeProbability(from);
            return upper - lower;
        }
    }
}
