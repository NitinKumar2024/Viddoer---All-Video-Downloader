package com.nitin.videodownloaderpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class VideoHistory extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_CODE = 123;

    private RecyclerView recyclerView;
    private MediaAdapter adapter;
    private List<MediaItem> mediaItems;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_history);

        recyclerView = findViewById(R.id.recyclerViewDownloadHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Populate mediaItems with video and audio files from the gallery.
        mediaItems = retrieveMediaItemsFromGallery(); // Initialize the instance variable
        adapter = new MediaAdapter(mediaItems); // Initialize the adapter
        recyclerView.setAdapter(adapter);


//        // Check if you have permissions
//        if (hasPermissions()) {
//            // You already have permissions
//            recyclerView.setAdapter(adapter);
//        } else {
//            // Request permissions
//            requestPermissions();
//            recyclerView.setAdapter(adapter);
//        }
    }




    private List<MediaItem> retrieveMediaItemsFromGallery() {
        List<MediaItem> mediaItems = new ArrayList<>();

        // Define the columns you want to retrieve from MediaStore for videos
        String[] videoProjection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA


        };

        // Retrieve videos
        Cursor videoCursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                null
        );

        if (videoCursor != null) {
            int position = 0; // Initialize the position counter
            while (videoCursor.moveToNext()) {
                String fileName = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String filePath = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                mediaItems.add(new MediaItem(fileName, filePath, MediaItem.MediaType.VIDEO, position));
                position++; // Increment the position counter
            }
            videoCursor.close();
        }

        // Define the columns you want to retrieve from MediaStore for audio files
        String[] audioProjection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA

        };

        // Retrieve audio files
        Cursor audioCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioProjection,
                null,
                null,
                null
        );

        if (audioCursor != null) {
            int position = 0; // Initialize the position counter
            while (audioCursor.moveToNext()) {
                String fileName = audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String filePath = audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                mediaItems.add(new MediaItem(fileName, filePath, MediaItem.MediaType.AUDIO, position));
                position++; // Increment the position counter
            }
            audioCursor.close();
        }

        return mediaItems;
    }


    // Check if you have necessary permissions
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                },
                REQUEST_PERMISSIONS_CODE);
    }

    // Handle the result of permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                recyclerView.setAdapter(adapter);
                // Permissions granted
            } else {
                // Permissions denied
                // Handle this case, perhaps by showing a message to the user

                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}