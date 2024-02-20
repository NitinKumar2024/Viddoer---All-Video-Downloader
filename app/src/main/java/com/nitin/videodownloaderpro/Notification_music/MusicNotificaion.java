package com.nitin.videodownloaderpro.Notification_music;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.nitin.videodownloaderpro.Instagram_Details.Post;
import com.nitin.videodownloaderpro.Instagram_Details.PostAdapter;
import com.nitin.videodownloaderpro.MainActivity;
import com.nitin.videodownloaderpro.Notification_music.Services.OnClearFromRecentService;
import com.nitin.videodownloaderpro.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicNotificaion extends AppCompatActivity implements Playable{

    ImageButton play;
    TextView title;

    NotificationManager notificationManager;

    List<Track> tracks;

    int position = 0;
    boolean isPlaying = false;


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_notificaion);
        play = findViewById(R.id.play);
        title = findViewById(R.id.title);




        popluateTracks();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    onTrackPause();
                } else {
                    onTrackPlay();
                }
            }
        });
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "Nitin", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    //populate list with tracks
    private void popluateTracks(){

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> retrievedPostUrlSet = sharedPreferences.getStringSet("postUrls", new HashSet<>());
        List<String> song = new ArrayList<>(retrievedPostUrlSet);
        List<String> songs = new ArrayList<>();
        List<String> title = new ArrayList<>();

        for (String all_links: song){
            String first_title = all_links.substring(0, all_links.indexOf("https"));
            String second_link = all_links.substring(all_links.indexOf("https"));


            tracks = new ArrayList<>();

            tracks.add(new Track(first_title, second_link, R.drawable.t1));

        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (isPlaying){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    @Override
    public void onTrackPrevious() {

        position--;
        CreateNotification.createNotification(MusicNotificaion.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size()-1);
        title.setText(tracks.get(position).getTitle());

    }

    @Override
    public void onTrackPlay() {

        CreateNotification.createNotification(MusicNotificaion.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size()-1);
        play.setImageResource(R.drawable.ic_pause_black_24dp);
        title.setText(tracks.get(position).getTitle());
        isPlaying = true;

    }

    @Override
    public void onTrackPause() {

        CreateNotification.createNotification(MusicNotificaion.this, tracks.get(position),
                R.drawable.ic_play_arrow_black_24dp, position, tracks.size()-1);
        play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        title.setText(tracks.get(position).getTitle());
        isPlaying = false;

    }

    @Override
    public void onTrackNext() {
        position++;
        if (position >= tracks.size()) {
            // If we reach the end, loop back to the first track
            position = 0;
        }
        CreateNotification.createNotification(MusicNotificaion.this, tracks.get(position),
                R.drawable.ic_pause_black_24dp, position, tracks.size() - 1);
        title.setText(tracks.get(position).getTitle());



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);
    }
}
