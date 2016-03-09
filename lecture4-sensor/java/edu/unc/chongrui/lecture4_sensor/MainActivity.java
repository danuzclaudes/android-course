package edu.unc.chongrui.lecture4_sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager sm;
    private Sensor s1, s2;
    private List<Sensor> ls;

    private long lastPrinted = 0, lastPrinted2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ls = sm.getSensorList(Sensor.TYPE_ALL);  // TYPE_LIGHT
        for(int i = 0; i < ls.size(); i++){
            Log.v("**************", i + " " + ls.get(i).getName());
        }

        s1 = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        s2 = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // impl and register sensor event listener
        sm.registerListener(this, s1, 1000000);
        sm.registerListener(this, s2, 500000);  // micro-second
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get value by every one second
        if(event.timestamp - lastPrinted >= 1e9
                && event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            Log.v("GRAVITY", "" + event.values[0]);
            lastPrinted = event.timestamp;
        }

        if(event.timestamp - lastPrinted2 >= .5e9
                && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            Log.v("PROXIMITY", "" + event.values[0]);
            lastPrinted2 = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}