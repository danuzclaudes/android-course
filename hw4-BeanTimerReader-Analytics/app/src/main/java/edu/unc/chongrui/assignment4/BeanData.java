package edu.unc.chongrui.assignment4;

/**
 * Wrap the timestamp, Ax, Ay, Az into an object.
 */
public class BeanData {
    private long timestamp;
    private double x;
    private double y;
    private double z;

    public BeanData(long t, double x, double y, double z) {
        this.timestamp = t;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public long getTimestamp() { return timestamp; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}
