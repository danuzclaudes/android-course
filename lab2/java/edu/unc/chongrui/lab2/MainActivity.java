package edu.unc.chongrui.lab2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Show a pop up message, saying “app started”,
        // when onCreate() is called
        showMsg("App Started");
    }

    public void onClick(View v){
        // print the which button is clicked on the TextView
        TextView tv = (TextView) findViewById(R.id.textview1);
        if(v.getId() == R.id.btn1){
            tv.setText("Button 1");
        }
        else{
            tv.setText("Button 2");
        }
        // show/hide background when it is clicked
        ImageButton btn = (ImageButton) findViewById(v.getId());
        if(v.getTag().equals("1")){
            btn.setBackgroundResource(R.drawable.signcheck);
            v.setTag("2");
        }
        else{
            btn.setBackgroundResource(R.drawable.x);
            v.setTag("1");
        }
        // Show a pop up message, saying “same image”,
        // whenever -- “a button is clicked
        // AND the two buttons have the same background image”
        ImageButton b1 = (ImageButton) findViewById(R.id.btn1);
        ImageButton b2 = (ImageButton) findViewById(R.id.btn2);
        if(b1.getTag().equals(b2.getTag()))
            showMsg("Same Image");
    }

    private void showMsg(String s){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}