package edu.unc.chongrui.assignment4;

import android.util.Log;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

/**
 * Use Java Thread to create a background thread
 * that is supposed to periodically read sensor
 * values into new BeanData objects.
 */
public class BackgroundBeanDataReader_Thread extends Thread {
    private boolean stopped;
    private BeanDataFactory factory;
    private Bean bean;

    public BackgroundBeanDataReader_Thread(BeanDataFactory factory) {
        this.factory = factory;
        stopped = false;

        BeanManager.getInstance().startDiscovery(beanDiscoveryListener);
    }

    BeanDiscoveryListener beanDiscoveryListener = new BeanDiscoveryListener() {

        @Override
        public void onBeanDiscovered(Bean bean, int rssi) {
            Log.v("Bean", "Discover device: " + bean.getDevice() + ", " + rssi + "...");
            BackgroundBeanDataReader_Thread.this.bean = bean;
        }

        @Override
        public void onDiscoveryComplete() {
            if(bean == null) return;
            // "Bean"              (example)
            System.out.println(bean.getDevice().getName());
            // "B4:99:4C:1E:BC:75" (example)
            System.out.println(bean.getDevice().getAddress());


            Log.v("BEAN", "Start bean...");
            bean.connect(
                    factory.getActivity().getApplicationContext(),
                    beanListener);
            try {
                // Halt every 1 second
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            Log.v("BEAN", "Bean connected: " + bean.isConnected() + "...");

        }
    };

    BeanListener beanListener = new BeanListener() {

        @Override
        public void onConnected() {
            Log.v("BEAN", "Connected to: " + bean.getDevice().getName() + "...");
        }

        @Override
        public void onConnectionFailed() {

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onSerialMessageReceived(byte[] data) {

        }

        @Override
        public void onScratchValueChanged(ScratchBank bank, byte[] value) {

        }

        @Override
        public void onError(BeanError error) {

        }
    };

    public void halt() {
        stopped = true;  /* To break while loop */
    }

    public void run() {
        while (!stopped) {
            if (Constants.DEBUGGING) _generateSyntheticData();
            else {
                if (bean == null || !bean.isConnected()) continue;
                bean.readAcceleration(new Callback<Acceleration>() {
                    @Override
                    public void onResult(Acceleration result) {
                        Log.v("BEAN", result.x() + ", " + result.y() + ", " + result.z());
                        long current = System.currentTimeMillis();
                        BeanData data = new BeanData(current, result.x(), result.y(), result.z());
                        factory.updateBeanList(data);
                    }
                });

                try {
                    // Halt every x second
                    Thread.sleep(Constants.READER_FREQUENCY);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }

            }
        }
        Log.v("BEAN", "The data collecting task has been cancelled...");
        bean.disconnect();
    }

    private void _generateSyntheticData() {
        // Declare synthetic BeanData objects and update BeanData list
        long current = System.currentTimeMillis();
        BeanData data = new BeanData(current, 1.0, 2.0, 3.0);
        factory.updateBeanList(data);

        try {
            // Halt every 0.1 second
            Thread.sleep(100);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
