package edu.unc.chongrui.assignment2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener
{

    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        getSensorStatusAndInfo(event);
        // if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) Log.v("Sensor", "" + event.values[0]);
    }
    private void getSensorStatusAndInfo(SensorEvent event){
        // display sensor status and information like range, resolution and delay
        int sensorType = event.sensor.getType();
        String sensorName = event.sensor.getName();
        // Log.v("Sensor", sensorType + " " + sensorName);
        StringBuilder sb = new StringBuilder("Status: ");
        TextView tv = (TextView) findViewById(
            sensorType == Sensor.TYPE_PROXIMITY ?
            R.id.status_proximity: R.id.status_accelerometer
        );

        if(sensorManager.getDefaultSensor(sensorType) == null){
            sb.append(sensorName);
            sb.append(" is not present.\n");
        }
        else {
            switch(sensorType){
                case Sensor.TYPE_ACCELEROMETER:
                    sb.append("Accelerometer is present.\n");
                    break;
                case Sensor.TYPE_PROXIMITY:
                    sb.append("Proximity is present.\n");
                    break;
            }
            sb.append("Info:");
            sb.append("\t\tMax Range: ").append(event.sensor.getMaximumRange());
            sb.append("\nResolution: ").append(event.sensor.getResolution());
            sb.append("\nMin Delay: ").append(event.sensor.getMinDelay());
            sb.append("\t\tMax Delay: ").append(event.sensor.getMaxDelay());

        }
        tv.setText(sb.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showMsg(String msg){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
