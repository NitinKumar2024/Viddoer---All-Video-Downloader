package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class ImageViewPlayer extends AppCompatActivity {


    private ImageView imageView;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_player);

        imageView = findViewById(R.id.imageView);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float scaleFactor = 1f;
            private ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    scaleFactor *= detector.getScaleFactor();
                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f)); // Set boundaries for the scale factor
                    imageView.setScaleX(scaleFactor);
                    imageView.setScaleY(scaleFactor);
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });


        String mediaFilePath = getIntent().getStringExtra("mediaFilePath");
        if (mediaFilePath != null) {

          //  Picasso.get().load(mediaFilePath).placeholder(R.drawable.viddoer_logo).into(imageView);
            Glide.with(this).load(mediaFilePath).placeholder(R.drawable.viddoer_logo).into(imageView);


        } else {
            Toast.makeText(this, "Media file path not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}