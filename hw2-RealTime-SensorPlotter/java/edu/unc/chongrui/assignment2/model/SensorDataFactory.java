package edu.unc.chongrui.assignment2.model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class SensorDataFactory {

    /**
     * Apply Factory Design Pattern to create SensorData objects by sensor types.
     *
     * @param event a `Sensor` event and holds information such as the sensor's type,
     *              the time-stamp, accuracy and sensor data
     * @return      a `SensorData` object according to sensor type
     */
    public SensorData getSensorData(SensorEvent event) {
        if(event == null) return null;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            return new AccelerometerData(
                    event.timestamp,
                    "ACCELEROMETER",
                    event.values[0],
                    event.values[1],
                    event.values[2]
            );
        else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
            return new ProximityData(
                    event.timestamp,
                    "PROXIMITY",
                    event.values[0]
            );
        else
            return null;
    }

}
