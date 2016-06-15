package edu.unc.chongrui.assignment4;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * BZ: must declare the class as Activity instead of a view.
 */
public class DataPlottingActivity extends Activity {

    private XYPlot xyPlot;
    private long start;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_plot);

        start = System.currentTimeMillis();
        // Initialize XYPlot reference
        xyPlot = (XYPlot) findViewById(R.id.chart);
        /* BZ: */
        PixelUtils.init(this);

        // Log.v("BEAN", "Entering DataPlottingActivity activity...");
        Bundle extras = getIntent().getExtras();
        // Plot list of standard deviations as chart
        _plot((List<Double>) getIntent().getSerializableExtra("stddevs"),
                extras.getString("name") + "_STDDEV.png",
                false);
        // Plot list of means as chart
        _plot((List<Double>) getIntent().getSerializableExtra("means"),
                extras.getString("name") + "_MEAN.png",
                true);
    }

    private void _plot(List<Double> result, String chartName,
                       boolean plottingMean) {
        Log.v("BEAN", "Plotting " + chartName.split("\\.")[0]
                + ": " + result + "...");

        // Set list of means/stddevs as series
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries dataSeries = new SimpleXYSeries(
                result,
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                plottingMean ? "Mean" : "Standard Deviation");

        // Set Title
        xyPlot.setTitle("Line Chart for " + chartName.split("\\.")[0]);
        // Set point label
        PointLabelFormatter plf = new PointLabelFormatter(Color.WHITE);
        plf.getTextPaint().setTextSize(8);

        // Create formats for drawing a series using LineAndPointRenderer
        LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setPointLabelFormatter(plf);
        formatter.configure(
                getApplicationContext(),
                plottingMean ? R.xml.chart_formatter_mean :
                        R.xml.chart_formatter_stddev);

        // http://stackoverflow.com/q/23165132/
        xyPlot.setDomainBoundaries(0, result.size() - 1, BoundaryMode.FIXED);
        // http://stackoverflow.com/q/28406153
        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        // xyPlot.setRangeBoundaries(0, 5, BoundaryMode.FIXED);
        // xyPlot.setRangeValueFormat(new DecimalFormat("#.#"));

        xyPlot.addSeries(dataSeries, formatter);
        _saveViewAsBitmap(xyPlot, chartName);
    }

    /**
     * @param view
     * @param chartName `~/Android/Sdk/platform-tools/adb shell`
     *                  `~/Android/Sdk/platform-tools/adb pull
     *                  /storage/self/primary/a4 ~/Downloads/a4`
     */
    private void _saveViewAsBitmap(View view, String chartName) {
        view.setDrawingCacheEnabled(true);

        // Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        Bitmap bitmap = _loadBitmapFromView(view);

        // Create output folder if not exists
        try {
            // File sdcard = Environment.getExternalStorageDirectory();
            // File outdir = new File(sdcard.getPath() + Constants.OUTFOLDER);
            File outdir = new File(System.getenv("EXTERNAL_STORAGE") +
                    Constants.OUTFOLDER);
            if (!outdir.exists() && outdir.mkdirs())
                Log.v("BEAN", "Creating output folder: " + outdir.getPath());

            try {
                /* BZ: split the output folder with data directory */
                FileOutputStream fos = new FileOutputStream(
                        System.getenv("EXTERNAL_STORAGE") +
                        Constants.OUTFOLDER + "/" + chartName, false);
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, fos);
                view.setDrawingCacheEnabled(false); // clear drawing cache
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /* BZ: avoid accumulating previous series */
        xyPlot.clear();
        /* BZ: finsh current activity */
        // www.stackoverflow.com/q/10847526
        this.finish();
        long end = System.currentTimeMillis();
        Log.v("BEAN", "Total running time: " + (end - start));
    }

    /**
     * stackoverflow.com/q/2339429/
     *
     * @param v
     * @return
     */
    private static Bitmap _loadBitmapFromView(View v) {
        /* BZ: */
        Bitmap bitmap = Bitmap.createBitmap(
                Constants.PNGWIDTH,
                Constants.PNGHEIGHT,
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        v.layout(0, 0, bitmap.getWidth(), bitmap.getHeight());
        v.draw(canvas);
        return bitmap;
    }
}
