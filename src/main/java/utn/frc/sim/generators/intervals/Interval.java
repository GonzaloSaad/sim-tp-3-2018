package utn.frc.sim.generators.intervals;

import org.apache.commons.lang3.StringUtils;
import utn.frc.sim.util.MathUtils;

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
        return from <= number && number <= to;
    }

    public void addOccurrence() {
        observedFrequency += (1 / (double) sampleLength);
    }

    public double getObservedFrequency() {
        return observedFrequency;
    }

    public String displayName() {
        return getDisplay(from) + "-" + getDisplay(to);
    }

    @Override
    public String toString() {
        return "Interval{" +
                "from=" + getDisplay(from) +
                ", to=" + getDisplay(to) +
                ", observedFrequency=" + observedFrequency +
                ", expectedFrequency=" + expectedFrequency +
                '}';
    }

    public String getDisplay(double number) {
        return StringUtils
                .rightPad(getDoubleString(number), 6, '0');

    }

    private String getDoubleString(double number) {
        return Double.toString((MathUtils.round(number, 4)));
    }

    public double getResult() {
        return Math.pow(observedFrequency - expectedFrequency, 2) / expectedFrequency;
    }

    public String getDisplayableResult() {
        return getDisplay(getResult());
    }

    public String getPlottableInterval() {
        return getDisplay((from + to) / 2);
    }
}
