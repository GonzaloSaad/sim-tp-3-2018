package utn.frc.sim.generators.distributions;

import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

/**
 * Clase que representa un generador uniforme.
 */
public class UniformDistributionGenerator extends BaseRandomGenerator {

    private final double a;
    private final double b;

    private UniformDistributionGenerator(double a, double b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Metodo para crear un generador con parametros a (limite
     * inferior) y b (limite superior).
     * Patron factory method.
     */
    public static UniformDistributionGenerator createOf(double a, double b) {
        return new UniformDistributionGenerator(a, b);
    }

    /**
     * Metodo que genera un numero pseudoaleatorio de
     * distribucion uniforme.
     * La formula usada es:
     * x = a + u * (b - a)
     * donde u es un numero aleatorio en [0,1)
     */
    @Override
    public double random() {
        return a + Math.random() * (b - a);
    }

    /**
     * Metodo que devuelve la distribucion del generador.
     */
    @Override
    public DistributionValueGenerator getDistribution() {
        return new UniformValueGenerator();
    }

    /**
     * Clase que representa la distribucion del generador.
     */
    private class UniformValueGenerator implements DistributionValueGenerator {

        /**
         * Metodo que calcula la frecuencia esperada para un intervalo
         * en una distribucion uniforme.
         */
        @Override
        public double getExpectedFrequency(double from, double to) {
            return (to - from) / (b - a);
        }
    }

}
