package edu.unc.chongrui.assignment4;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chongrui on 4/15/16.
 * The factory supports the following functions:
 * <p/>
 * Group the duration of one second on a given file.
 * Compute average acceleration per second.
 * Compute standard deviation of acceleration per second.
 * Draw the plot of average acceleration per second.
 * Draw the plot of stddev of acceleration per second.
 * <p/>
 * Download files on sdcard to localhost by command:
 * `~/Android/Sdk/platform-tools/adb pull /storage/sdcard/a4 ~/Downloads/a`
 */
public class DataAnalysisFactory {

    private File infolder;
    private List<BackgroundBeanDataPlotter_Thread> plotterThreadList;
    private List<BeanData> dataList;
    private MainActivity mainActivity;

    public DataAnalysisFactory(MainActivity activity) {
        infolder = new File(
                // Environment.getExternalStorageDirectory()
                System.getenv("EXTERNAL_STORAGE") + Constants.DATAFOLDER);
        mainActivity = activity;
        plotterThreadList = new ArrayList<>();
    }

    /**
     * Read in all data files in a directory.
     * Traverse each file to group by every one second for computation.
     */
    public void analyze() {
        Log.v("BEAN", "Starting Data Analysis Factory...");
        // Get all data files from the folder
        File[] fList = infolder.listFiles();
        for (File file : fList) {
            if (!file.isFile()) continue;
            // Log.v("BEAN", "Analyzing: " + file.getName() + "...");
            // Read a file line by line to fill list with BeanData objects
            _readDataFile(file);
            if(dataList.isEmpty()) continue;
            // Get # of rows constituting 1s in that file.
            // Divide the file into 1s sets and compute stat for each set.
            List<ArrayList<Double>> res = _getRowSetStat(_getNumRowsPerSecond(),
                    dataList.size());
            // Log.v("BEAN", "File means:  " + res.get(0));
            // Log.v("BEAN", "File stddev: " + res.get(1));
            // Launch a thread to plot chart in background
            _plot(res, file.getName().split("\\.")[0]);
            // break; // for debugging
        }

        for (BackgroundBeanDataPlotter_Thread thread : plotterThreadList) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.v("BEAN", "Analysis completes...");
    }

    /**
     * Read a file line-by-line.
     * Convert each line into BeanData object.
     * Store these objects into a list.
     *
     * @param file a txt file recording timestamp and
     *             reads of Bean device on each line
     */
    private void _readDataFile(File file) {
        String line;
        dataList = new ArrayList<>();
        try {
            // Wrap a BufferedReader around FileReader
            BufferedReader bufferedReader = new BufferedReader(
                    new FileReader(file));

            // Read all lines into a list by readLine method
            while ((line = bufferedReader.readLine()) != null) {
                // Log.v("BEAN", line);  // for debugging
                String[] values = line.split(" ");
                try {
                    dataList.add(
                            new BeanData(Long.parseLong(values[0]),
                                    Double.parseDouble(values[1]),
                                    Double.parseDouble(values[2]),
                                    Double.parseDouble(values[3])));
                } catch (NumberFormatException nfe) {
                    Log.v("BEAN", "Exception when parsing data from file");
                }
            }

            // Close the BufferedReader
            bufferedReader.close();

        } catch (IOException ie) {
            Log.v("BEAN", "Exception when reading file from sdcard");
        }
    }

    /**
     * Compute the number of rows that constitute 1 second.
     * Divide total number of rows by duration of the file.
     *
     * @return number of rows that constitute 1 second
     */
    private int _getNumRowsPerSecond() {
        if (dataList.isEmpty()) return 0;
        // Compute the duration of the file by subtracting
        // the last and the first timestamp.
        double duration = (dataList.get(dataList.size() - 1).getTimestamp() -
                dataList.get(0).getTimestamp()) / 1000.0;
        // If the duration is N seconds,
        // then divide the total number of rows by N to
        // approximately get the number of rows that constitute 1 second.
        return (int) Math.ceil(dataList.size() / duration);
    }

    /**
     * Group a file of N lines (fileSize) by every K rows
     * constituting 1s (numRowsPerSecond).
     * <p/>
     * The file is thus divided into N / K sets of rows.
     * <p/>
     * Accumulate the mean of each set, as well as stddev,
     * by computing the value of accelerometer on each row.
     * <p/>
     * Record lists of mean/stddev values for all sets.
     *
     * @param numRowsPerSecond number of rows constituting 1s
     * @param fileSize         total number of lines of file
     * @return lists of mean/stddev values for all sets
     */
    private List<ArrayList<Double>> _getRowSetStat(int numRowsPerSecond,
                                                   int fileSize) {
        List<ArrayList<Double>> listOfMeanStddev = new ArrayList<>();
        if (fileSize * numRowsPerSecond == 0) return listOfMeanStddev;
        /* BZ: how to divide extra lines? to a separate group? */
        int numSets = (int) (Math.ceil((double) fileSize / numRowsPerSecond));
        // Compute mean/standard deviation by one-pass algorithm
        Iterator<BeanData> iterator = dataList.iterator();
        for (int i = 1; i <= numSets; i++) {
            /* BZ: reset variables within each 1s set */
            /* BZ: the size of last set depends on # of sets */
            int n = 0;              // number input values
            double sum = 0;         // sum of input values
            double sumSquare = 0;   // sum of squares of input values
            for (int j = 0; i < numSets && j < numRowsPerSecond ||
                    i == numSets && iterator.hasNext(); j++) {
                BeanData bean = iterator.next();
                n++;
                sum += bean.getValue();
                sumSquare += bean.getValue() * bean.getValue();
            }
            if (listOfMeanStddev.isEmpty()) {
                listOfMeanStddev.add(0, new ArrayList<Double>());
                listOfMeanStddev.add(1, new ArrayList<Double>());
            }
            // Log.v("BEAN", "n=" + n + " sum=" + sum + " sum2=" + sumSquare);
            listOfMeanStddev.get(0).add(Math.round((sum / n) * 100.0) / 100.0);
            // listOfMeanStddev.get(0).add(sum / n);
            double variance = (n * sumSquare - sum * sum) / (n * (n - 1));
            // Testing real numbers with epsilon bounds
            variance = Math.abs(variance) < Constants.EPSILON ? 0 : variance;
            listOfMeanStddev.get(1)
                    .add(Math.round(Math.sqrt(variance) * 100.0) / 100.0);
        }
        return listOfMeanStddev;
    }

    /**
     * Plot list of mean values and list of stddev values on charts.
     * <p/>
     * BZ: plotting needs to launch another activity, which is much slower
     * than reading files...must kick off a thread and join!
     *
     * @param result    a list containing list of mean and stddev values
     *                  for each 1s set of data file
     * @param chartName the title of generated chart
     */
    private void _plot(final List<ArrayList<Double>> result,
                       final String chartName) {

        BackgroundBeanDataPlotter_Thread plotter_thread = new BackgroundBeanDataPlotter_Thread(
                mainActivity,
                result.get(0),
                result.get(1),
                chartName);
        plotterThreadList.add(plotter_thread);

        /*
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(mainActivity, DataPlottingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("means", result.get(0));
                intent.putExtra("stddevs", result.get(1));
                intent.putExtra("name", chartName);
                mainActivity.startActivity(intent);
                Log.v("BEAN", "Launching the activity to plot " +
                        chartName + "...");
            }
        });
        */
    }
}
