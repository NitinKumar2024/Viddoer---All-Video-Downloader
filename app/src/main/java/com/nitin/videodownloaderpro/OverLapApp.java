package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OverLapApp extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private View overlayView;
    private TextView overlayText;
    private WindowManager.LayoutParams params;

    private PyObject py_music_function;
    private Python py;
    private MediaPlayer mediaPlayer;

    private ImageView playButton, previous, next;


    private boolean isPlaying = false;
    private SeekBar seekBar;
    private Handler handler = new Handler();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_over_lap_app);

        overlayView = LayoutInflater.from(this).inflate(R.layout.activity_over_lap_app, null);
        overlayText = overlayView.findViewById(R.id.overlayText);

        playButton = overlayView.findViewById(R.id.play);
        seekBar = overlayView.findViewById(R.id.seekBar);

        ImageView imageView = overlayView.findViewById(R.id.delete);





        mediaPlayer = new MediaPlayer();

        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        updateOverlayView();
                        return true;
                    default:
                        return false;
                }
            }
        });

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        py_music_function = py.getModule("video_hosting").get("get_music_hosting_link");

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    startAudioPlayback();
                } else {
                    pauseAudioPlayback();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OverLapApp.super.onDestroy();




            }
        });






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestOverlayPermission();
        } else {
            showOverlay();
            // Check if the app was launched from a shared text
            Intent intent = getIntent();
            if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

                fetchHosting_Music_Link(sharedText);
                finish();
            }

        }

        // SeekBar change listener to seek to a specific position when the user drags the SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });


    }

    // Update SeekBar progress periodically
    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    updateSeekBar();
                }
            }
        }, 1000); // Update every 1 second
    }






    private void startAudioPlayback() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playButton.setImageResource(R.drawable.pause);
            isPlaying = true;
        }
    }

    private void pauseAudioPlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setImageResource(R.drawable.ic_play);;
            isPlaying = false;
        }
    }

    private void fetchHosting_Music_Link(final String videoLink) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = py_music_function.call(videoLink);

                final String hostingLink = result.toString();





                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        if (!hostingLink.isEmpty()) {



                            try {
                                mediaPlayer.setDataSource(hostingLink);
                                mediaPlayer.prepareAsync();

                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        // Media player is prepared, start audio playback
                                        LinearLayout linearLayout = overlayView.findViewById(R.id.linearLayout2);
                                        linearLayout.setVisibility(View.VISIBLE);
                                        ProgressBar progressBar = overlayView.findViewById(R.id.over_progress);
                                        progressBar.setVisibility(View.GONE);



                                        startAudioPlayback();
                                        updateSeekBar();
                                    }
                                });



                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        } else {

                        }
                    }
                });
            }
        }).start();
    }




    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
    }

    private void updateOverlayView() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.updateViewLayout(overlayView, params);
        }
    }

    private void showOverlay() {
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.addView(overlayView, params);
        }
    }





    // ... other methods and lifecycle callbacks ...



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                showOverlay();
            } else {
                // Handle permission denial
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
