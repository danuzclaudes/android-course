package edu.unc.chongrui.assignment4;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chongrui on 4/14/16.
 * The factory supports the following functions:
 * <p>
 * readBeanData:            Generate list of Bean data by BackgroundGenerator;
 * stopReadingGenerator:    Pause the background thread of reading Bean data;
 * writeBeanData:           Save the list of data into files on sdcard.
 */
public class BeanDataFactory {
    private Activity activity;
    private BackgroundBeanDataReader_Thread bgReader;
    private List<BeanData> listOfData;

    public BeanDataFactory(Activity activity) {
        _initializeListReader();
        this.activity = activity;
    }

    /**
     * Read from the Bean device, and store sensor values into list.
     * <p>
     * Temporarily, create synthetic sensor data and return a list
     * of data as soon as the user has clicked "START" button.
     *
     * @return a list of SensorData objects read from Bean device.
     */
    public List<BeanData> readBeanData() {
        // Run the background thread to generate BeanData objects
        /* BZ: if click the START again, must reinitialize the thread */
        _initializeListReader();
        bgReader.start();
        return listOfData;
    }

    private void _initializeListReader() {
        bgReader = new BackgroundBeanDataReader_Thread(this);
        listOfData = new ArrayList<>();
    }

    public void updateBeanList(BeanData beanData) {
        Log.v("BEAN", "" + beanData.getTimestamp());
        listOfData.add(beanData);
    }

    /**
     * Stop the BackgroundGenerator thread
     * when the user has clicked "STOP" button.
     */
    public void stopReadingGenerator() {
        bgReader.halt();
    }

    /**
     * Write a list of BeanData into 10 separate files for each
     * combination of activity and bean location.
     * <p>
     * Each line will contain the timestamp and the sensor values X, Y, Z.
     * Each file will bucketize every M / 10 entries of the list,
     * while appending all of the remaining lines to the last file.
     * Save only after the user has clicked the "SAVE" button.
     *
     * @param activityName the chosen activity name
     * @param beanLocation the chosen Bean location
     */
    public void writeBeanData(String activityName, String beanLocation) {
        if (listOfData.isEmpty()) return;
        BufferedWriter bufferedWriter;
        int bucketSize = listOfData.size() / Constants.NUM_DATA_FILES;
        Log.v("BEAN", "Total: " + listOfData.size() + "; Bucket size: " + bucketSize);
        Iterator<BeanData> listIterator = listOfData.iterator();
        try {
            /* BZ: permission to write file to sdcard */
            /* http://www.stackoverflow.com/a/2122304 */
            /* http://www.stackoverflow.com/a/3551906 */
            /* https://stackoverflow.com/q/32635704 */

            File myDir = new File(
                    // Environment.getExternalStorageDirectory()
                    System.getenv("EXTERNAL_STORAGE") + Constants.DATAFOLDER);
            boolean dirCreated = true, fileCreated = true;
            if (!myDir.exists()) {
                Log.v("BEAN", "Creating data folder " + myDir.getPath() + " ...");
                dirCreated = myDir.mkdirs();
            }
            // Check availability of directory
            if (dirCreated)
                Log.v("BEAN", myDir.getName() + " is created...");
            else {
                Log.v("BEAN", "Failed to create " + myDir.getAbsolutePath() + " ...");
                return;
            }

            try {
                // Generate 10 files for each Activity_Location combination
                for (int i = 1; i <= Constants.NUM_DATA_FILES; i++) {
                    File myFile = new File(
                            myDir,
                            activityName + "_" + beanLocation + "_" + i + ".txt");
                    bufferedWriter = new BufferedWriter(
                            new OutputStreamWriter(new FileOutputStream(myFile)));
                    fileCreated = myFile.createNewFile();
                    if (! fileCreated) {
                        Log.v("BEAN", "Failed to create " + myFile.getName() + " ...");
                        // continue;
                    }

                    // Write # of M data into the 10 files by
                    // either bucketizing every M / 10 lines into a new file,
                    // or writing the rest of rows by last file
                    for (int j = 1; i < Constants.NUM_DATA_FILES && j <= bucketSize ||
                            i == Constants.NUM_DATA_FILES && listIterator.hasNext(); j++) {
                        /* BZ: missing i == 10? */
                        /* What if i < 10 but over bucket size, still has next... */
                        BeanData tmp = listIterator.next();
                        bufferedWriter.write(
                                tmp.getTimestamp() + " " +
                                        tmp.getX() + " " +
                                        tmp.getY() + " " +
                                        tmp.getZ());
                        bufferedWriter.newLine();
                    }
                    /* BZ: close the bufferedWriter at the end of each file */
                    bufferedWriter.close();
                }
            } catch (IOException ioexception) {
                Log.v("BEAN", "Exception on writing new file...");
                ioexception.printStackTrace();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        listOfData.clear();
    }

    public int getSize() {
        return listOfData.size();
    }

    public Activity getActivity() { return activity; }

}
