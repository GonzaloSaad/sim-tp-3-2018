package utn.frc.sim.generators.intervals;

import org.apache.commons.lang3.StringUtils;
import utn.frc.sim.util.MathUtils;
import utn.frc.sim.util.SimStringUtils;

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

    public String displayName() {
        return getDisplay(from) + "-" + getDisplay(to);
    }

    @Override
    public String toString() {
        return "Interval{" +
                "from=" + getDisplay(from) +
                ", to=" + getDisplay(to) +
                ", observedFrequency=" + getDisplay(observedFrequency) +
                ", expectedFrequency=" + getDisplay(expectedFrequency) +
                '}';
    }

    public double getResult() {
        return Math.pow(observedFrequency - expectedFrequency, 2) / expectedFrequency;
    }

    public String getDisplayableInterval() {
        return getDisplay((from + to) / 2);
    }

    public String getDisplayableExpectedFrequency(){
        return getDisplay(expectedFrequency);
    }

    public String getDisplayableObservedFrequency(){
        return getDisplay(observedFrequency);
    }

    public String getDisplayableResult() {
        return getDisplay(getResult());
    }

    private String getDisplay(double value) {
        return SimStringUtils.getDoubleStringFormat(value, 4);
    }

}
