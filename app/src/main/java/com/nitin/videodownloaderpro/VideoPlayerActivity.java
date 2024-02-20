package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private AudioManager audioManager;
    private GestureDetector gestureDetector;
    private RelativeLayout relativeLayout;




    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        relativeLayout = findViewById(R.id.relative_all);
        ImageView imageView = findViewById(R.id.help_imageView);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        // Enter full screen mode
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        String mediaFilePath = getIntent().getStringExtra("mediaFilePath");
        if (mediaFilePath != null) {
            videoView.setVideoPath(mediaFilePath);
            videoView.setMediaController(new MediaController(this));
            videoView.start();
        } else {
            Toast.makeText(this, "Media file path not found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check if the device is in landscape mode and video aspect ratio is 16:9
        if (isLandscapeMode()  ) {
            // Adjust video view layout parameters for full-screen mode
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            videoView.setLayoutParams(layoutParams);
            imageView.setVisibility(View.GONE);
        }

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(VideoPlayerActivity.this, "Zoom mode On...", Toast.LENGTH_SHORT).show();

                videoView.setOnTouchListener(new View.OnTouchListener() {
                    private float scaleFactor = 1f;
                    private ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(getApplicationContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        @Override
                        public boolean onScale(ScaleGestureDetector detector) {
                            scaleFactor *= detector.getScaleFactor();
                            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f)); // Set boundaries for the scale factor
                            videoView.setScaleX(scaleFactor);
                            videoView.setScaleY(scaleFactor);
                            return true;
                        }
                    });

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        scaleGestureDetector.onTouchEvent(event);
                        return true;
                    }
                });

            }
        });




        // Set video URI and start playing the video
        videoView.setVideoPath(mediaFilePath);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private boolean isLandscapeMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE;
    }



    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_DISTANCE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        // User swiped from up to down (volume down)
                        adjustVolume(false);
                    } else {
                        // User swiped from down to up (volume up)
                        adjustVolume(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private void adjustVolume(boolean increase) {
        int flags = AudioManager.FLAG_PLAY_SOUND;

        // Determine the change factor for volume adjustment
        int changeFactor = increase ? 1 : -1;

        // Change the volume by a larger amount for more significant adjustment
        int newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + (changeFactor * 2);

        // Ensure the new volume is within valid range (0 to max volume)
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        newVolume = Math.max(0, Math.min(newVolume, maxVolume));

        // Set the new volume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, flags);
    }


}