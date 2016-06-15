package edu.unc.chongrui.assignment2.viewer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.LinkedList;

import edu.unc.chongrui.assignment2.model.SensorData;

public class CustomChartView extends View {

    private Canvas canvas;
    private Paint paint = new Paint();

    /**
     * The queue to support FIFO with capacity of 10 ChartData.
     */
    private LinkedList<ChartData> queue = new LinkedList<>();
    private static final int CAPACITY          = 10;
    private static final float RADIUS          = 20f;
    private static final float SCALE           = 15f;
    private static final float SCALE_STDDEV    = 4f;
    private static int timeCount               = 0;

    // Declare all types of constructors
    public CustomChartView(Context context) {
        super(context);
    }

    public CustomChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Wrap up value, mean, std-dev and current time into an object.
     */
    private static class ChartData {
        private float value;
        private float mean;
        private float stddev;
        private int time;

        public ChartData(float val, float mean, float stddev) {
            this.value  = val;
            this.mean   = mean;
            this.stddev = stddev;
            this.time   = (timeCount++) % CAPACITY;
        }
        public float getValue()  { return value; }
        public float getMean()   { return mean; }
        public float getStddev() { return stddev; }
        public void setMean(float newMean)     { this.mean = newMean; }
        public void setStddev(float newStddev) { this.stddev = newStddev; }
        public String toString() {
            return time + " - val:" + value + " - mean:" + mean + " - std:" + stddev;
        }
    }

    /**
     * Compute mean and standard deviation for all current data points first;
     * then plot value, mean and deviation on the chart.
     *
     * @param newSensorData the new sensor data passed from DataPlottingActivity
     *
     * Maintain the capacity before offering each new data points. Traverse all
     * previous data points to generate current mean and std-dev and record the
     * results by the newly recorded `ChartData` object.
     */
    public void addNewSensorData(SensorData newSensorData) {
        if(queue.size() >= CAPACITY) queue.pollFirst();
        queue.offer(new ChartData(newSensorData.getValue(), 0, 0));
        setMeanAndStddev(queue.get(queue.size() - 1));
    }

    /**
     * Reads in a sequence of numbers, and computes average std-dev by one-pass.
     */
    private void setMeanAndStddev(ChartData newestData) {
        float sum = 0, sumSquare = 0;
        for(ChartData data : queue) {
            sum += data.value;
            sumSquare += data.value * data.value;
        }
        newestData.setMean(sum / queue.size());
        newestData.setStddev(
                queue.size() == 1 ? 0 : (queue.size() * sumSquare - sum * sum) /
                        (queue.size() * (queue.size() - 1))
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        drawAxis();
        drawLegends();
        drawChart();
    }

    /**
     * Paint x, y axis and labels
     */
    public void drawAxis() {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);
        paint.setTextSize(40f);

        // Set margin-left as 150, margin-top as 200 for y-axis
        canvas.drawLine(150, 200, 150, getHeight() - 200, paint);
        // Set margin-left as 150, margin-bottom as 200 for x-axis
        canvas.drawLine(150, getHeight() - 200, getWidth() - 50, getHeight() - 200, paint);

        for(int i = 0; i < 20; i++) {
            // Set margin-left as 80 with 20 labels on y-axis
            canvas.drawText("" + i, 80, getHeight() - 200 - i * (getHeight() - 400) / 20, paint);
        }

        for(int i = 0; i < 10; i++) {
            // Set margin-left as 150 with 10 labels on x-axis
            canvas.drawText("" + i, 150 + i * (getWidth() - 200) / 10, getHeight() - 130, paint);
        }
    }

    /**
     * Paint graph legends
     */
    public void drawLegends() {
        drawLegendLine(200, 200, 300, 200, Color.GREEN, "Value");
        drawLegendLine(500, 200, 600, 200, Color.BLUE, "Mean");
        drawLegendLine(800, 200, 900, 200, Color.RED, "Std-Dev");
    }

    private void drawLegendLine(int startX, int startY, int stopX, int stopY, int color, String text) {
        paint.setColor(color);
        paint.setStrokeWidth(7f);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.drawCircle(startX, startY, RADIUS, paint);
        canvas.drawCircle(stopX, stopY, RADIUS, paint);
        canvas.drawText(text, startX, startY - 30, paint);
    }

    /**
     * Plot lines of values, means and std-devs as graph.
     */
    public void drawChart() {
        // Draw points of current data and lines between them
        paint.setStrokeWidth(12f);
        paint.setStrokeJoin(Paint.Join.ROUND);

        // Traverse each data point to draw chart;
        for(int i = 1; i < queue.size(); i++) {
            Log.v("SENSOR", queue.get(i).toString());
            // Paint values as green line
            paint.setColor(Color.GREEN);
            drawChartLine(i - 1, queue.get(i - 1).getValue() * SCALE, i, queue.get(i).getValue() * SCALE);
            // Paint mean as blue line
            paint.setColor(Color.BLUE);
            drawChartLine(i - 1, queue.get(i - 1).getMean() * SCALE, i, queue.get(i).getMean() * SCALE);
            // Paint mean as red line
            paint.setColor(Color.RED);
            drawChartLine(i - 1, queue.get(i - 1).getStddev() * SCALE_STDDEV, i, queue.get(i).getStddev() * SCALE_STDDEV);
        }
    }

    private void drawChartLine(int startX, float startY, int stopX, float stopY) {
        int interval = (getWidth() - 200) / 10;
        canvas.drawCircle(150 + startX * interval, 900 - startY, RADIUS, paint);
        canvas.drawLine(
                150 + startX * interval, 900 - startY,
                150 + stopX * interval, 900 - stopY, paint
        );
        canvas.drawCircle(150 + stopX * interval, 900 - stopY, RADIUS, paint);
    }
}
