package utn.frc.sim.generators.intervals;

import utn.frc.sim.util.DoubleUtils;

public class Interval {
    private double from;
    private double to;
    private double observedFrequency;
    private double expectedFrequency;
    private int sampleLength;

    public Interval(double from, double to, double expectedFrequency, int sampleLength) {
        this.from = from;
        this.to = to;
        this.expectedFrequency = expectedFrequency;
        this.observedFrequency = 0;
        this.sampleLength = sampleLength;
    }

    public boolean includes(double number) {
        return from <= number && number < to;
    }

    public void addOccurrence() {
        observedFrequency += (1 / (double) sampleLength);
    }

    public double getObservedFrequency() {
        return observedFrequency;
    }

    public double getExpectedFrequency() {
        return expectedFrequency;
    }

    public double getResult() {
        return Math.pow(observedFrequency - expectedFrequency, 2) / expectedFrequency;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "from=" + getDisplayable(from) +
                ", to=" + getDisplayable(to) +
                ", observedFrequency=" + getDisplayable(observedFrequency) +
                ", expectedFrequency=" + getDisplayable(expectedFrequency) +
                '}';
    }

    public String getDisplayableName() {
        return getDisplayable(from) + "-" + getDisplayable(to);
    }

    public String getDisplayableInterval() {
        return getDisplayable((from + to) / 2);
    }

    public String getDisplayableExpectedFrequency() {
        return getDisplayable(expectedFrequency);
    }

    public String getDisplayableObservedFrequency() {
        return getDisplayable(observedFrequency);
    }

    public String getDisplayableResult() {
        return getDisplayable(getResult());
    }

    private String getDisplayable(double value) {
        return DoubleUtils.getDoubleStringFormat(value, 4);
    }

}
