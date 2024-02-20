package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.DownloadManager;
import android.content.Context;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class YoutubeVideo extends AppCompatActivity {

    private RewardedAd rewardedAd;
    private final String TAG = "YoutubeVideo";

    private static final int REQUEST_PERMISSIONS_CODE = 123;

    private EditText editTextLink;
    private Button buttonFetch;
    private ProgressBar progressBar;
    private VideoView videoView;
  //  private Button buttonDownload;
    private Button buttonPaste;

    private Python py;
    private PyObject pyFunction, py_music_function, py_share_video, py_share_music, py_title_youtube, py_360, py_720, py_1080;
    private TextView hosting_texts, title_text;

    private MediaController mediaController;

    Context context;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_video);

        editTextLink = findViewById(R.id.editTextLink);
        buttonFetch = findViewById(R.id.buttonFetch);
        progressBar = findViewById(R.id.progressBar);
        videoView = findViewById(R.id.videoView);
      //  buttonDownload = findViewById(R.id.buttonDownload);
        hosting_texts = findViewById(R.id.hosting_text);
        buttonPaste = findViewById(R.id.buttonPaste);
        title_text = findViewById(R.id.title_text);
        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-8404134982147261/6580622117",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        pyFunction = py.getModule("video_hosting").get("get_video_hosting_link");
        py_music_function = py.getModule("video_hosting").get("get_music_hosting_link");
        py_share_video = py.getModule("video_hosting").get("get_video_hosting_link");
        py_share_music = py.getModule("video_hosting").get("get_music_hosting_link");
        py_title_youtube = py.getModule("video_hosting").get("get_title_youtube");
        py_360 = py.getModule("video_hosting").get("get_360p_hosting_link");
        py_720 = py.getModule("video_hosting").get("get_720p_hosting_link");
        py_1080 = py.getModule("video_hosting").get("get_1080p_hosting_link");

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLink = editTextLink.getText().toString().trim();
                if (!videoLink.isEmpty()) {
                 //  fetchHostingLink(videoLink);
                    Alert_bar(videoLink);
                }
                else {
                    Toast.makeText(YoutubeVideo.this, "Put Link First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteFromClipboard();
            }

            // Method to paste text from the clipboard into the EditText
            private void pasteFromClipboard() {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
                    CharSequence pasteData = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                    if (pasteData != null) {
                        editTextLink.setText(pasteData);
                    }
                }}
        });







        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {


            // Check if you have permissions
            if (hasPermissions()) {
                // You already have permissions

            } else {
                // Request permissions
                requestPermissions();

            }
        }
        else {


        }


        // Check if the app was launched from a shared text (e.g., YouTube video link)
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            handleSharedText(intent);
        }

       
    }

    // Check if you have necessary permissions
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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


                // Permissions granted
            } else {
                // Permissions denied
                // Handle this case, perhaps by showing a message to the user

                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void handleSharedText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && sharedText.startsWith("https://you")) {
            hosting_texts.setText(sharedText);

            editTextLink.setText(sharedText);

            Alert_bar(sharedText);

        } else {
            // Handle cases where the shared content is not a YouTube link
            // You can show a message or take appropriate action
            // For example, display a toast message
            Toast.makeText(this, "Shared content is not a YouTube link", Toast.LENGTH_SHORT).show();
        }



    }

    private void fetchHostingLink(final String videoLink) {
        progressBar.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
      //  buttonDownload.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = pyFunction.call(videoLink);

                final String hostingLink = result.toString();

                PyObject title_result = py_title_youtube.call(videoLink);

                final String caption_youtube = title_result.toString();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!hostingLink.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            hosting_texts.setText(hostingLink);
                            title_text.setText(caption_youtube);

                            // Display video and download button
                            videoView.setVisibility(View.VISIBLE);
                        //    buttonDownload.setVisibility(View.VISIBLE);
                            // Set the video URI and start playing
                            progressBar.setVisibility(View.GONE);
                       //     Alert_bar();
                            downloadVideo();
                            //   videoView.setVideoURI(Uri.parse(hostingLink));
                            //      videoView.setMediaController(new MediaController(YoutubeVideo.this));
                         //   videoView.start();
                        } else {
                            Toast.makeText(YoutubeVideo.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    private void fetch_360_HostingLink(final String videoLink) {
        progressBar.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        //  buttonDownload.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = py_360.call(videoLink);

                final String hostingLink = result.toString();

                PyObject title_result = py_title_youtube.call(videoLink);

                final String caption_youtube = title_result.toString();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!hostingLink.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            hosting_texts.setText(hostingLink);
                            title_text.setText(caption_youtube);

                            // Display video and download button
                            videoView.setVisibility(View.VISIBLE);
                            //    buttonDownload.setVisibility(View.VISIBLE);
                            // Set the video URI and start playing
                            progressBar.setVisibility(View.GONE);
                            downloadVideo();

                            //   videoView.setVideoURI(Uri.parse(hostingLink));
                            //      videoView.setMediaController(new MediaController(YoutubeVideo.this));
                            //   videoView.start();
                        } else {
                            Toast.makeText(YoutubeVideo.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
    private void fetch_720_HostingLink(final String videoLink) {
        progressBar.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        //  buttonDownload.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                PyObject result = py_720.call(videoLink);

                final String hostingLink = result.toString();

                PyObject title_result = py_title_youtube.call(videoLink);

                final String caption_youtube = title_result.toString();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!hostingLink.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            hosting_texts.setText(hostingLink);
                            title_text.setText(caption_youtube);

                            // Display video and download button
                            videoView.setVisibility(View.VISIBLE);
                            //    buttonDownload.setVisibility(View.VISIBLE);
                            // Set the video URI and start playing
                            progressBar.setVisibility(View.GONE);
                            downloadVideo();

                            //   videoView.setVideoURI(Uri.parse(hostingLink));
                            //      videoView.setMediaController(new MediaController(YoutubeVideo.this));
                            //   videoView.start();
                        } else {
                            Toast.makeText(YoutubeVideo.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }



    private boolean isVideoDownloading = false; // Add this boolean flag

    private void downloadVideo() {

        if (rewardedAd != null) {
            Activity activityContext = YoutubeVideo.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }


        progressBar.setVisibility(View.VISIBLE);
        String videoLink = editTextLink.getText().toString().trim();
        if (!videoLink.isEmpty()) {
            final String hostingLink = hosting_texts.getText().toString();
            String video_title = title_text.getText().toString();

            // Check if the hostingLink is valid
            if ("wrong".equals(hostingLink)) {
                Toast.makeText(YoutubeVideo.this, "Age restricted video", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                editTextLink.setText("");
                return;
            }

            // Rest of your code for downloading the video...

            // Generate a unique filename based on the current timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            final String fileName = timestamp + ".mp4"; // Unique filename

            // Use the public "Movies" directory for downloads
           // File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
          //  File videoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");
            // Create a new File object for the Viddoer directory.
            File youtubeVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");

// Create a new File object for the Youtube_Video directory.
            File videoDir = new File(youtubeVideoDir, "Viddoer_Youtube");
            videoDir.mkdirs(); // Ensure the directory exists


            // Check if the video is currently downloading
            if (isVideoDownloading) {
                Toast.makeText(YoutubeVideo.this, "Download is already in progress", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceModel = Build.MANUFACTURER;
            // Get device details
            String manufacturer = Build.MANUFACTURER;
            String brand = Build.BRAND;
            String model = Build.MODEL;
            String product = Build.PRODUCT;
            String device = Build.DEVICE;
            String board = Build.BOARD;
            String hardware = Build.HARDWARE;
            String versionRelease = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;


            // Generate a unique filename based on the current timestamp
            // Get current timestamp in a human-readable format
            String pattern = "d MMMM yyyy"; // Define the desired date format pattern
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            String formattedDate = dateFormat.format(new Date());

            String formattedTimestamp = new SimpleDateFormat("d MMMM yyyy h:mm a", Locale.getDefault()).format(new Date());


            // Concatenate device details into a single string
            String deviceInfo = "Manufacturer: " + manufacturer +
                    ", Brand: " + brand +
                    ", Model: " + model +
                    ", Product: " + product +
                    ", Device: " + device +
                    ", Board: " + board +
                    ", Hardware: " + hardware +
                    ", Android Version: " + versionRelease +
                    ", SDK Version: " + sdkVersion;


            // Get a reference to the "users" node in your database
            DatabaseReference usersReference = databaseReference.child("Youtube Users/" + deviceInfo + "/"+ androidId);

            // Generate a unique key for the new user
            String userId = usersReference.push().getKey();

            // Create a HashMap to store the username
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("Link", videoLink);
            userMap.put("Title", video_title);
            userMap.put("time", formattedTimestamp);
            userMap.put("Hosting Video", hostingLink);

            // Set the username in the database under the generated user ID
            usersReference.child(userId).setValue(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String title = video_title.toString() + ".mp4";
                                String fifteen_title = video_title.substring(0, Math.min(video_title.length(), 20)) + "... .mp4";


                                final File videoFile = new File(videoDir, fifteen_title);
                                final String videoFilePath = videoFile.getAbsolutePath();

                                // Download the video to the specified directory
                                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(hostingLink);
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                request.setDestinationUri(Uri.fromFile(videoFile));
                                request.setDescription("Viddoer");
                                request.setTitle(fifteen_title);
                                final long downloadId = downloadManager.enqueue(request);

                                // Show a toast message indicating the download has started
                                Toast.makeText(YoutubeVideo.this, "Download started", Toast.LENGTH_SHORT).show();
                                editTextLink.setText("");

                                // Set the flag to indicate that the download is in progress
                                isVideoDownloading = true;

                                // Register a BroadcastReceiver to listen for download completion
                                BroadcastReceiver receiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                                        if (receivedDownloadId == downloadId) {
                                            // Download is complete, update the UI
                                            // Display video
                                            videoView.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            videoView.setVideoURI(Uri.fromFile(videoFile));
                                            videoView.setMediaController(new MediaController(YoutubeVideo.this));
                                            videoView.start();

                                            // Set the flag to indicate that the download is complete
                                            isVideoDownloading = false;
                                        }
                                    }
                                };

                                // Register the BroadcastReceiver
                                registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                                //    Toast.makeText(InstaFetchUsername.this, "Username stored in Firebase", Toast.LENGTH_SHORT).show();
                            } else {

                                // Failed to store username
                                // Toast.makeText(InstaFetchUsername.this, "Failed to Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        }
    }










    public void writeIntoFile(Context context, String fileName, String content) throws IOException {
//        File appSpecificExternalStorageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File appSpecificInternalStorageDirectory = context.getFilesDir();
            File file = new File(appSpecificInternalStorageDirectory, fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(content.getBytes());
            fos.close();
        }


        public String readFromFile(String filePath) throws IOException {
            //        File appSpecificExternalStorageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            File appSpecificInternalStorageDirectory = context.getFilesDir();
            File file = new File(appSpecificInternalStorageDirectory, filePath);
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            fis.close();
            return builder.toString();
        }

    private void fetchHosting_Music_Link(final String videoLink) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = py_music_function.call(videoLink);

                final String hostingLink = result.toString();

                PyObject title_result = py_title_youtube.call(videoLink);

                final String caption_youtube = title_result.toString();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!hostingLink.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            hosting_texts.setText(hostingLink);

                            title_text.setText(caption_youtube);

                            progressBar.setVisibility(View.GONE);

                            // Download the music using hostingLink
                            downloadMusic(hostingLink, caption_youtube);
                        } else {
                            Toast.makeText(YoutubeVideo.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void downloadMusic(String hostingLink, String title_music) {

        if (rewardedAd != null) {
            Activity activityContext = YoutubeVideo.this;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }

        progressBar.setVisibility(View.VISIBLE);
        // Check if the hostingLink is valid
        if ("wrong".equals(hostingLink)) {
            Toast.makeText(YoutubeVideo.this, "Age restricted video", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            editTextLink.setText("");
            return;
        }

        // Rest of your code for downloading the video...

        // Generate a unique filename based on the current timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String fileName = timestamp + ".mp3"; // Unique filename

        // Use the public "Movies" directory for downloads
     //   File videoDir = new File(Environment.DIRECTORY_DOCUMENTS + File.separator + "Viddoer");
      //  File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        File youtubeVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");

// Create a new File object for the Youtube_Video directory.
        File videoDir = new File(youtubeVideoDir, "Viddoer_Youtube");
        videoDir.mkdirs(); // Ensure the directory exists



        // Check if the video is currently downloading
        if (isVideoDownloading) {
            Toast.makeText(YoutubeVideo.this, "Download is already in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceModel = Build.MANUFACTURER;
        // Get device details
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String device = Build.DEVICE;
        String board = Build.BOARD;
        String hardware = Build.HARDWARE;
        String versionRelease = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;


        // Generate a unique filename based on the current timestamp
        // Get current timestamp in a human-readable format
        String pattern = "d MMMM yyyy"; // Define the desired date format pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());

        String formattedTimestamp = new SimpleDateFormat("d MMMM yyyy h:mm a", Locale.getDefault()).format(new Date());


        // Concatenate device details into a single string
        String deviceInfo = "Manufacturer: " + manufacturer +
                ", Brand: " + brand +
                ", Model: " + model +
                ", Product: " + product +
                ", Device: " + device +
                ", Board: " + board +
                ", Hardware: " + hardware +
                ", Android Version: " + versionRelease +
                ", SDK Version: " + sdkVersion;


        // Get a reference to the "users" node in your database
        DatabaseReference usersReference = databaseReference.child("Youtube Users/" + deviceInfo + "/"+ androidId);

        // Generate a unique key for the new user
        String userId = usersReference.push().getKey();

        // Create a HashMap to store the username
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("Link", editTextLink.getText().toString());
        userMap.put("Title", title_music);
        userMap.put("time", formattedTimestamp);
        userMap.put("Hosting Video", hostingLink);

        // Set the username in the database under the generated user ID
        usersReference.child(userId).setValue(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            String title = title_music.toString() + ".mp3";
                            String fifteen_title = title_music.substring(0, Math.min(title_music.length(), 20)) + ".mp3";



                            final File videoFile = new File(videoDir, fifteen_title);
                            final String videoFilePath = videoFile.getAbsolutePath();

                            // Download the video to the specified directory
                            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(hostingLink);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationUri(Uri.fromFile(videoFile));
                            request.setDescription("Viddoer");
                            request.setTitle(fifteen_title);
                            final long downloadId = downloadManager.enqueue(request);

                            // Show a toast message indicating the download has started
                            Toast.makeText(YoutubeVideo.this, "Download started", Toast.LENGTH_SHORT).show();
                            editTextLink.setText("");

                            // Set the flag to indicate that the download is in progress
                            isVideoDownloading = true;

                            // Register a BroadcastReceiver to listen for download completion
                            BroadcastReceiver receiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                                    if (receivedDownloadId == downloadId) {
                                        // Download is complete, update the UI
                                        videoView.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        videoView.setVideoURI(Uri.fromFile(videoFile));
                                        videoView.setMediaController(new MediaController(YoutubeVideo.this));
                                        videoView.start();

                                        // Set the flag to indicate that the download is complete
                                        isVideoDownloading = false;
                                    }
                                }
                            };

                            // Register the BroadcastReceiver
                            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                            //    Toast.makeText(InstaFetchUsername.this, "Username stored in Firebase", Toast.LENGTH_SHORT).show();
                        } else {

                            // Failed to store username
                            // Toast.makeText(InstaFetchUsername.this, "Failed to Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }





    private void Alert_bar(final String videoLink){

        // Create a custom alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(YoutubeVideo.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(dialogView);

        // Get references to the buttons in the custom layout
        Button btnDownloadVideo = dialogView.findViewById(R.id.btnDownloadVideo);
        Button btnDownloadMusic = dialogView.findViewById(R.id.btnDownloadMusic);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        RadioGroup radioGroupOptions = dialogView.findViewById(R.id.radioGroupOptions);
        RadioButton radio360 = dialogView.findViewById(R.id.radio360);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioButton radio144 = dialogView.findViewById(R.id.radio144);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioButton radio240 = dialogView.findViewById(R.id.radio240);
        RadioButton radio720 = dialogView.findViewById(R.id.radio720);
        RadioButton radio1080 = dialogView.findViewById(R.id.radio1080);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioButton radio4k = dialogView.findViewById(R.id.radio4k);

        // Declare the dialog variable as final
        final AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        btnDownloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the "360p" radio button is selected
                if (radio360.isChecked()) {
                    // Handle the "Download Video" action
                    // Add your code here for downloading video
                    // Dismiss the dialog
                    fetch_360_HostingLink(videoLink);
                    radio144.setChecked(false);
                    dialog.dismiss();

                } else if (radio144.isChecked()) {

                    fetch_360_HostingLink(videoLink);
                    dialog.dismiss();

                }
                else if (radio240.isChecked()) {

                    fetch_360_HostingLink(videoLink);
                    dialog.dismiss();

                } else if (radio720.isChecked()) {

                    fetch_720_HostingLink(videoLink);
                    dialog.dismiss();

                }
                else if (radio1080.isChecked()) {

                    fetchHostingLink(videoLink);
                    dialog.dismiss();

                } else if (radio4k.isChecked()) {

                    fetchHostingLink(videoLink);
                    dialog.dismiss();

                } else {
                    // Notify the user to select the appropriate option
                    Toast.makeText(YoutubeVideo.this, "Please select any option", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDownloadMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Download Music" action
                // Add your code here
                // Dismiss the dialog
                String videoLink = editTextLink.getText().toString().trim();
                if (!videoLink.isEmpty()) {
                    fetchHosting_Music_Link(videoLink);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when "Cancel" is clicked
                dialog.dismiss();
            }
        });

        dialog.show();

        // Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // In your activity's onCreate method
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

    }

    // Override onCreateOptionsMenu to inflate the menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Override onOptionsItemSelected to handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Handle the search action
            Intent intent = new Intent(YoutubeVideo.this, VideoHistory.class);
            startActivity(intent);
            return true;
        }   else if (id == R.id.action_nitin) {

            Intent intent = new Intent(YoutubeVideo.this, AboutDeveloper.class);
            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);

    }




}