package edu.unc.chongrui.assignment2.model;

/**
 * The proximity sensor is boolean - 'near' (0), or 'far' (5) on most devices.
 */
public class ProximityData extends SensorData {

    public ProximityData(long timestamp, String type, float x){
        super(timestamp, type, x);
    }
}
