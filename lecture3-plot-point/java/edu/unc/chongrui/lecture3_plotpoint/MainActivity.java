package edu.unc.chongrui.lecture3_plotpoint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    // one memory location shared by all java files
    public static int X;
    public static int Y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        X = 20;
        Y = 20;
    }

    public void click(View v){
        Log.v("CZ", "check working");

        X += 20;
        Y += 20;

        // force a redraw
        CustomView cv = ((CustomView) findViewById(R.id.funny1));
        cv.invalidate();
    }
}