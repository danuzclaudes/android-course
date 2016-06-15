package edu.unc.chongrui.assignment2.viewer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Parent activity for DashboardActivity and DataPlottingActivity.
 * Define sensor manager; register and resume sensors.
 * Make `onSensorChanged()` method abstract to be implemented by
 * two sub-activities.
 */
abstract public class SensorParentActivity extends AppCompatActivity
        implements SensorEventListener
{
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create an instance of the sensor service from system service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume(){
        super.onResume();
        // register accelerometer and proximity sensors
        Sensor accelerometer, proximity;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, accelerometer, 1000000);
        sensorManager.registerListener(this, proximity, 1000000);
    }

    @Override
    public void onStop(){
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    protected void showMsg(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    @Override
    abstract public void onSensorChanged(SensorEvent event);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
