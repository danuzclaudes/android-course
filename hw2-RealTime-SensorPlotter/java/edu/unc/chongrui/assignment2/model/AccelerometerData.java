package edu.unc.chongrui.assignment2.model;

/**
 * Acceleration force along the x, y and z axis including gravity.
 */
public class AccelerometerData extends SensorData {
    private float x;
    private float y;
    private float z;

    public AccelerometerData(long timestamp, String type, float x, float y, float z){
        super(timestamp, type, x, y, z);

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getZ() { return this.z; }

    @Override
    public String toString() {
        return type + " - " + this.timestamp +
                " - x=" + getX() + ", y=" + getY() + ", z=" + getZ() +
                " - value= " + getValue();
    }
}
