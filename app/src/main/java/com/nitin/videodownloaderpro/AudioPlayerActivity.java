package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nitin.videodownloaderpro.Models.MusicModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AudioPlayerActivity extends AppCompatActivity {

    private ImageView playButton, previous, next;

    private MediaPlayer mediaPlayer, next_media;
    private boolean isPlaying = false;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private TextView textView;
    Thread updateSeek;
    ArrayList<File> songs;
    int position;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private View overlayView;
    private TextView overlayText;
    private WindowManager.LayoutParams params;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        overlayView = LayoutInflater.from(this).inflate(R.layout.activity_audio_player, null);
        playButton =  findViewById(R.id.play);
        textView =  findViewById(R.id.textView);
        previous =  findViewById(R.id.previous);
        next =  findViewById(R.id.next);
        ImageView imageView = findViewById(R.id.imageView2);
        ImageView setting =  findViewById(R.id.help_imageView);
        LinearLayout linearLayout =  findViewById(R.id.all_display);

        seekBar =  findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        next_media = new MediaPlayer();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        textView.setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        updateOverlayView();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });

        // Set the audio file path
        String mediaFilePath = getIntent().getStringExtra("mediaFilePath");
        String mediaFileName = getIntent().getStringExtra("fileName");


        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> retrievedPostUrlSet = sharedPreferences.getStringSet("postUrls", new HashSet<>());
        List<String> song = new ArrayList<>(retrievedPostUrlSet);
        List<String> songs = new ArrayList<>();
        List<String> title = new ArrayList<>();

        for (String all_links: song){
            String first_title = all_links.substring(0, all_links.indexOf("https"));
            String second_link = all_links.substring(all_links.indexOf("https"));
            songs.add(second_link);
            title.add(first_title);


        }

        if (songs.contains(mediaFilePath)){
            position = songs.indexOf(mediaFilePath);


            previous.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);

//            setting.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (linearLayout.getVisibility() == View.VISIBLE) {
//                        linearLayout.setVisibility(View.GONE);
//                        seekBar.setVisibility(View.GONE);
//                        textView.setVisibility(View.GONE);
//                        imageView.setVisibility(View.GONE);
//                    } else {
//                        linearLayout.setVisibility(View.VISIBLE);
//                        seekBar.setVisibility(View.VISIBLE);
//                        textView.setVisibility(View.VISIBLE);
//                    }
//                }
//            });

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                requestOverlayPermission();
//            } else {
//                showOverlay();
//
//
//            }
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();





        try {

            textView.setText(mediaFileName);
            textView.setSelected(true);
            mediaPlayer.setDataSource(mediaFilePath);
            mediaPlayer.prepareAsync(); // Prepare asynchronously

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Media player is prepared, start audio playback
                    startAudioPlayback();
                    updateSeekBar();
//                    try {
//                        next_media.setDataSource(songs.get(position + 1));
//
//                        next_media.prepareAsync();
//                        next_media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//
//                            }
//                        });
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if(position!=songs.size()-1){
                        position = position + 1;
                    }
                    else{
                        position = 0;
                    }
                    Uri uri = Uri.parse(songs.get(position).toString());
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.pause);
                    seekBar.setMax(mediaPlayer.getDuration());

                    String music_title = title.get(position);
                    textView.setText(music_title);

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=0){
                    position = position - 1;
                }
                else{
                    position = songs.size() - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());

                String music_title = title.get(position);
                textView.setText(music_title);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                playButton.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                String music_title = title.get(position);
                textView.setText(music_title);

            }
        });

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



        // ...



    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
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
        handler.removeCallbacksAndMessages(null); // Remove all callbacks and messages
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}