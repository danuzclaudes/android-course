package edu.unc.chongrui.lecture3_plotpoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chongrui on 2/4/16.
 */
public class CustomView extends View {
    // Declare all types of constructors here.
    public CustomView(Context context){
        super(context);
    }

    public CustomView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public CustomView(Context context, AttributeSet attributeSet, int i){
        super(context, attributeSet, i);
    }

    public CustomView(Context context, AttributeSet attributeSet, int i1, int i2){
        super(context, attributeSet, i1, i2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Use canvas.draw*()
        // Paint p = new Paint(Color.BLACK);
        Paint p = new Paint();
        p.setColor(Color.RED);
        canvas.drawLine(5, 5, getWidth() - 5, getHeight() - 5, p);
        canvas.drawCircle(MainActivity.X, MainActivity.Y, 20f, p);
    }
}
