package a4.pivi;

public class PIVIDeltaMetric {

    private double delta;
    private long wallClockMillis;

    public PIVIDeltaMetric(double delta, long wallClockMillis) {
        this.delta = delta;
        this.wallClockMillis = wallClockMillis;
    }

    public double getDelta() {
        return delta;
    }

    public long getWallClockMillis() {
        return wallClockMillis;
    }
}
