package edu.unc.chongrui.assignment4;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Test cases for START/STOP/SAVE:
 *     START-STOP-START-STOP
 *     START-STOP-SAVE
 *     START-SAVE-START-STOP
 *     START-START-START-STOP/SAVE
 *     START-STOP-STOP-STOP-SAVE
 *     START-STOP-STOP-STOP-SAVE-STOP-SAVE
 */
public class MainActivity extends AppCompatActivity {
    private AsyncTimer timer;
    private BeanDataFactory factory;
    private double elapsedTime;
    private StringBuilder activityName, beanLocation;
    private TextView timerText;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyStoragePermissions(this);

        activityName = new StringBuilder();
        beanLocation = new StringBuilder();
        elapsedTime = 0.0;
        factory = new BeanDataFactory(this);
        timerText = (TextView) findViewById(R.id.timer);
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.radioButton1:
            case R.id.radioButton2:
            case R.id.radioButton3:
            case R.id.radioButton4:
            case R.id.radioButton5:
            case R.id.radioButton6:
                activityName = new StringBuilder()
                        .append(((RadioButton) view).getText());
                break;
            case R.id.radioButton7:
            case R.id.radioButton8:
            case R.id.radioButton9:
                beanLocation = new StringBuilder()
                        .append(view.getTag());
                break;
        }
        showMsg(activityName + " " + beanLocation);
    }

    public void onClickStart(View view) {
        /* BZ: cannot start again while running */
        if(timer != null && ! timer.isCancelled()) return;
        showMsg("START");
        factory.readBeanData();
        timer = new AsyncTimer();
        timer.execute();
    }

    public void onClickStop(View view) {
        showMsg("STOP");
        _resetTimer();
    }

    public void onClickSave(View view) {
        // showMsg("SAVE");
        /* BZ: reset if start again without saving */
        if(! timer.isCancelled()) _resetTimer();
        factory.writeBeanData(activityName.toString(), beanLocation.toString());
        showMsg("SAVED");
    }

    private void _resetTimer() {
        /* BZ: must reset elapsed time before starting again */
        elapsedTime = 0.0;
        factory.stopReadingGenerator();
        showMsg(factory.getSize() + " data collected");
        timer.cancel(false);
        timerText.setText("" + 0.00); // Reset the text of timer
    }

    public void onClickAnalyze(View view) {
        showMsg("ANALYZING...");
        new DataAnalysisFactory(this).analyze();
    }

    public void showMsg(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    /**
     * Use Android AsyncTask to create a timer widget.
     */
    private class AsyncTimer
            extends AsyncTask<Void, Double, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v("BEAN", "Inside aync Timer task...");
            long start = System.currentTimeMillis();

            /* BZ: while(true) */
            /* check if isCancelled() to ensure a task is cancelled */
            while (! isCancelled()) {
                try {
                    Thread.sleep(Constants.TIMER_FREQ);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }

                // This thread can produce synthetic BeanData as well.
                // But delegate it to `BeanDataFactory` still...
                // so that reading from device doesn't need
                // to be put into this `MainActivity`.
                // BeanData data = new BeanData(start, 1.0, 2.0, 3.0);
                // factory.updateBeanList(data);

                // Update timer in UI thread
                long end = System.currentTimeMillis();
                publishProgress(((double) (end - start) / 1000));
                start = end;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Double... elapsed) {
            try {
                elapsedTime += elapsed[0];
                /* BZ: timer.setText("" + elapsedTime) -> will product extra digits */
                timerText.setText(String.format("%5.2f", elapsedTime));
                // Log.v("BEAN", "elapsed here:" + elapsedTime);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void onCancelled () {
            /* Won't execute this if using while(true) */
            Log.v("BEAN", "The timer task has been cancelled...");
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        Log.v("BEAN", "Verifying permissions...");
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.v("BEAN", "Requesting permissions...");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
