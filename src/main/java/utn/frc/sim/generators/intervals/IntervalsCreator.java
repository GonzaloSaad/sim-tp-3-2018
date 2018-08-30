package utn.frc.sim.generators.intervals;

import utn.frc.sim.generators.distributions.DistributionRandomGenerator;
import utn.frc.sim.generators.distributions.valuegenerator.DistributionValueGenerator;

import java.util.ArrayList;
import java.util.List;

public class IntervalsCreator {

    private List<Interval> intervals;
    private List<Double> numbers;
    private static final double DOUBLE_MAX_INTERVAL_ADJUSTMENT = 0.000000000001;

    public IntervalsCreator() {
    }

    /**
     * Metodo estatico que instancia a la clase con la informacion necesaria.
     * Patron factory method.
     */
    public static IntervalsCreator createFor(int amountOfNumbers, int amountOfIntervals, DistributionRandomGenerator generator) {
        IntervalsCreator intervalsCreator = new IntervalsCreator();
        intervalsCreator.createIntervals(amountOfNumbers, amountOfIntervals, generator);
        return intervalsCreator;
    }

    /**
     * Metodo que genera los intervalos.
     */
    private void createIntervals(int amountOfNumbers, int amountOfIntervals, DistributionRandomGenerator generator) {

        intervals = new ArrayList<>();
        numbers = generator.random(amountOfNumbers);

        double maxValue = numbers.stream()
                .reduce((d1, d2) -> d2 > d1 ? d2 : d1)
                .orElseThrow(IllegalArgumentException::new);

        maxValue += DOUBLE_MAX_INTERVAL_ADJUSTMENT;

        double minValue = numbers.stream()
                .reduce((d1, d2) -> d2 < d1 ? d2 : d1)
                .orElseThrow(IllegalArgumentException::new);

        double step = (maxValue - minValue) / amountOfIntervals;

        DistributionValueGenerator distributionValues = generator.getDistribution();


        for (int i = 0; i < amountOfIntervals; i++) {

            double from;
            double to;
            if (i == 0) {
                from = minValue;
                to = minValue + step;
            } else if (i == amountOfNumbers - 1) {
                from = minValue + step * i;
                to = maxValue;
            } else {
                from = minValue + step * i;
                to = minValue + step * (i + 1);
            }

            Interval interval = new Interval(from, to, distributionValues.getExpectedFrequency(from, to), amountOfNumbers);
            intervals.add(interval);
        }

        numbers.forEach(number -> intervals.stream()
                .filter(it -> it.includes(number))
                .findFirst()
                .ifPresent(Interval::addOccurrence));
    }

    public List<Double> getNumbers() {
        return numbers;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }


}

