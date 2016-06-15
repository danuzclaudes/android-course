package edu.unc.chongrui.assignment2.viewer;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.unc.chongrui.assignment2.R;

public class DashboardActivity extends SensorParentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.v("SENSOR", "Dashboard activity started.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        getSensorStatusAndInfo(event);
    }

    /**
     * Display sensor status and information: range, resolution and delay.
     *
     * @param event a Sensor event and holds information such as the sensor's type,
     *              the time-stamp, accuracy and sensor data
     */
    private void getSensorStatusAndInfo(SensorEvent event) {
        int sensorType = event.sensor.getType();
        String sensorName = event.sensor.getName();
        // Log.v("SensorData type: ", sensorType + " | name: " + sensorName);
        StringBuilder sb = new StringBuilder("Status: ");
        TextView tv = (TextView) findViewById(
            sensorType == Sensor.TYPE_PROXIMITY ?
                R.id.status_proximity : R.id.status_accelerometer
        );

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                sb.append("Accelerometer is present.\n");
                break;
            case Sensor.TYPE_PROXIMITY:
                sb.append("Proximity is present.\n");
                break;
            default:
                sb.append(sensorName);
                sb.append(" is not present.\n");
                return;
        }
        sb.append("Info:");
        sb.append("\t\tMax Range: ").append(event.sensor.getMaximumRange());
        sb.append("\nResolution: ").append(event.sensor.getResolution());
        sb.append("\nMin Delay: ").append(event.sensor.getMinDelay());
        sb.append("\t\tMax Delay: ").append(event.sensor.getMaxDelay());

        tv.setText(sb.toString());
    }

    public void onClickSensor(View v){
        // determine which sensor is chosen
        showMsg("Choose " + v.getTag());
        int chosenSensorType;
        switch(v.getTag().toString()) {
            case "Accelerometer":
                chosenSensorType = Sensor.TYPE_ACCELEROMETER;
                break;
            case "Proximity":
                chosenSensorType = Sensor.TYPE_PROXIMITY;
                break;
            default:
                return;
        }
        // Call data plotting activity from main activity
        Intent intent = new Intent(this, DataPlottingActivity.class);
        intent.putExtra("SensorType", chosenSensorType);
        startActivity(intent);
    }
}
