package edu.unc.chongrui.assignment4;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Use Java Thread to create a background thread
 * that is supposed to launch an activity to
 * plot lists of mean/stddev data through library
 * `AndroidPlot`, and to save the view as bitmap
 * without actually displaying on the UI.
 */
public class BackgroundBeanDataPlotter_Thread extends Thread {
    private MainActivity mainActivity;
    private String chartName;
    private ArrayList<Double> means;
    private ArrayList<Double> stddevs;

    public BackgroundBeanDataPlotter_Thread(MainActivity activity,
                                            ArrayList<Double> means,
                                            ArrayList<Double> stddevs,
                                            String name) {
        this.mainActivity = activity;
        this.chartName = name;
        this.means = means;
        this.stddevs = stddevs;
    }

    @Override
    public void run() {
        Intent intent = new Intent(mainActivity, DataPlottingActivity.class);
        /* BZ: launch an Activity without a UI */
        // http://www.stackoverflow.com/a/2704264
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        /* BZ: elevate the performance */
        // stackoverflow.com/a/10807848
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        /* BZ: intent only accepts ArrayList */
        intent.putExtra("means", means);
        intent.putExtra("stddevs", stddevs);
        intent.putExtra("name", chartName);
        mainActivity.startActivity(intent);
        // Log.v("BEAN", "Launching the activity to plot " + chartName + "...");
    }
}
