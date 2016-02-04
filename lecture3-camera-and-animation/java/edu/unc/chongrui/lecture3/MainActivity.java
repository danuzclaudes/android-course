package edu.unc.chongrui.lecture3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void camera(View v){
        // open camera and display the image taken in the ImageView
        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x, 1);
    }

    @Override
    protected void onActivityResult(int rc, int resc, Intent data){
        ImageView iv = null;
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        iv = ((ImageView) findViewById(R.id.imageView));
        iv.setBackgroundResource(0);
        iv.setImageBitmap(bm);
    }

    public void anim(View v){
        ImageView img = (ImageView) findViewById(R.id.imageView);
        img.setBackgroundResource(R.drawable.my_animation_list);
        img.setImageBitmap(null);
        ((AnimationDrawable) img.getBackground()).start();
    }
}
