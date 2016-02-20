package edu.unc.chongrui.assignment2.viewer;

import android.content.Intent;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import edu.unc.chongrui.assignment2.R;
import edu.unc.chongrui.assignment2.model.SensorDataFactory;


public class DataPlottingActivity extends SensorParentActivity
{
    private CustomChartView customChartView;
    private long lastPrinted = 0;
    private static SensorDataFactory sensorDataFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_plotting);

        // initialize custom chart view and factory of SensorData
        customChartView = ((CustomChartView) findViewById(R.id.chart));
        sensorDataFactory = new SensorDataFactory();

        // BZ: Activity already has an action bar supplied by the window decor?
        // toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        Log.v("SENSOR", "Data plotting activity started.");
    }

    /**
     * Call `onDraw()` method from Custom View when sensor has detected a change.
     * Apply Factory Design Pattern to create multiple types of sensor data objects.
     *
     * @param event the current sensor event
     */
    public void onSensorChanged(SensorEvent event) {
        // Plot data from the chosen sensor only
        Bundle extras = getIntent().getExtras();
        if(extras == null || event.sensor.getType() != extras.getInt("SensorType"))
            return;
        // BZ: must ignore timestamp less than 1s
        if(event.timestamp - lastPrinted < 1e9) return;
        lastPrinted = event.timestamp;

        // Generate specific type of sensor data object by Factory Design Pattern;
        // pass the new sensor data to custom view and force a redraw
        customChartView.addNewSensorData(sensorDataFactory.getSensorData(event));
        customChartView.invalidate();
    }

    /**
     * Click the `BACK` button to return to the dashboard activity
     *
     * @param v the current custom view
     */
    public void onClickBack(View v){
        showMsg("Choose " + v.getTag());
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
        // Clear all animations before returning to dashboard
        customChartView.clearAnimation();
        customChartView.invalidate();
    }
}
