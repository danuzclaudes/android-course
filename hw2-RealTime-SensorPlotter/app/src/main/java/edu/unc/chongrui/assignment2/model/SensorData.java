package edu.unc.chongrui.assignment2.model;

/**
 * Parent class for all types of sensor data.
 *
 * A SensorData object would contain timestamp of event,
 * type of sensor, and the sensor value.
 *
 * As for Environmental Sensors, there would be only one
 * sensor value as `event.values[0]`; however, for Motion
 * Sensors, the values would be computed as `Sqrt(x*2 + y*2 + z*2)`
 * from raw values on three dimensions.
 */
public class SensorData {
    protected long timestamp;
    protected String type;
    protected float value;

    public SensorData(long timestamp, String type, float val){
        this(timestamp, type, val, 0, 0);
    }

    public SensorData(long timestamp, String type, float x, float y, float z) {
        this.timestamp = timestamp;
        this.type      = type;
        this.value     = computeValue(x, y, z);
    }

    public float getValue(){ return this.value; }

    public String toString() {
        return type + " - " + this.timestamp + " - value=" + getValue();
    }

    private float computeValue(float x, float y, float z){
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
