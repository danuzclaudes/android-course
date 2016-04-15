package edu.unc.chongrui.assignment4;

/**
 * Use Java Thread to create a background thread
 * that is supposed to periodically read sensor
 * values into new BeanData objects.
 */
public class BackgroundBeanDataReader_Thread extends Thread {
    private boolean stopped;
    private BeanDataFactory factory;

    public BackgroundBeanDataReader_Thread(BeanDataFactory factory) {
        this.factory = factory;
        stopped = false;
    }

    public void halt() { stopped = true;  /* To break while loop */ }

    public void run() {
        while(! stopped) {
            _generateSyntheticData();
            _generateRealData();
        }
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

    private void _generateRealData() {
        // ToDo
    }
}
