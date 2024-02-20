package com.nitin.videodownloaderpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.nitin.videodownloaderpro.Instagram_Details.InstaFetchUsername;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class InstaReelDownloader extends AppCompatActivity {

    private RewardedAd rewardedAd;
    private final String TAG = "YoutubeVideo";

    private static final int REQUEST_PERMISSIONS_CODE = 123;

    private EditText editTextLink;
    private Button buttonFetch;
    private ProgressBar progressBar;
    private VideoView videoView;
    //  private Button buttonDownload;
    private Button buttonPaste;
    private PyObject pyFunction;

    private TextView hosting_texts, title_text;
    private Python py;

    private GestureDetector gestureDetector;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_reel_downloader);



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
        pyFunction = py.getModule("video_hosting").get("downloader");

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());


        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLink = editTextLink.getText().toString().trim();
                if (!videoLink.isEmpty()) {

                    fetchHostingLink(videoLink);

                }

                else {
                    Toast.makeText(InstaReelDownloader.this, "Put Link First", Toast.LENGTH_SHORT).show();
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

        // Check if the app was launched from a shared text
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {

            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && sharedText.startsWith("https://www.instagram.com")) {

                hosting_texts.setText(sharedText);

                editTextLink.setText(sharedText);
                buttonFetch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchHostingLink(sharedText);

                    }
                });



            } else {
                // Handle cases where the shared content is not a YouTube link
                // You can show a message or take appropriate action
                // For example, display a toast message
                Toast.makeText(this, "Shared content is not a Instagram link", Toast.LENGTH_SHORT).show();
            }

        }

    }


    // Check if you have necessary permissions
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
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
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float deltaX = e2.getX() - e1.getX();
            if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (deltaX > 0) {
                    // Swipe right, navigate to another activity
                    Intent intent = new Intent(InstaReelDownloader.this, VideoHistory.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else {
                    // Handle swipe left if needed
                    // Swipe right, navigate to another activity
                    Intent intent = new Intent(InstaReelDownloader.this, InstaFetchUsername.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                return true;
            }
            return false;
        }
    }


    private void handleSharedText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null && sharedText.startsWith("https://www.instagram.com")) {

            hosting_texts.setText(sharedText);

            editTextLink.setText(sharedText);

            fetchHostingLink(sharedText);

        } else {
            // Handle cases where the shared content is not a YouTube link
            // You can show a message or take appropriate action
            // For example, display a toast message
            Toast.makeText(this, "Shared content is not a Instagram link", Toast.LENGTH_SHORT).show();
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

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (!hostingLink.isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);

                            // Display video and download button
                            videoView.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);

                            downloadVideo(hostingLink);

                        } else {
                            Toast.makeText(InstaReelDownloader.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private boolean isVideoDownloading = false; // Add this boolean flag

    private void downloadVideo(final String hosting_video_link) {

        if (rewardedAd != null) {
            Activity activityContext = InstaReelDownloader.this;
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

            // Check if the hostingLink is valid
            if ("wrong".equals(hosting_video_link)) {
                Toast.makeText(InstaReelDownloader.this, "Age restricted video", Toast.LENGTH_SHORT).show();
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
            File videoDir = new File(youtubeVideoDir, "Viddoer_Instagram");
            videoDir.mkdirs(); // Ensure the directory exists

            final File videoFile = new File(videoDir, fileName);
            final String videoFilePath = videoFile.getAbsolutePath();

            // Check if the video is currently downloading
            if (isVideoDownloading) {
                Toast.makeText(InstaReelDownloader.this, "Download is already in progress", Toast.LENGTH_SHORT).show();
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
            DatabaseReference usersReference = databaseReference.child("Instagram Reels Users/" + deviceInfo + "/"+ androidId);

            // Generate a unique key for the new user
            String userId = usersReference.push().getKey();

            // Create a HashMap to store the username
            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("Link", editTextLink.getText().toString());
            userMap.put("time", formattedTimestamp);
            userMap.put("Hosting Video", hosting_video_link);

            // Set the username in the database under the generated user ID
            usersReference.child(userId).setValue(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                //    Toast.makeText(InstaFetchUsername.this, "Username stored in Firebase", Toast.LENGTH_SHORT).show();
                            } else {

                                // Failed to store username
                                // Toast.makeText(InstaFetchUsername.this, "Failed to Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            // Download the video to the specified directory
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(hosting_video_link);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationUri(Uri.fromFile(videoFile));
            request.setDescription("Viddoer");
            request.setTitle(fileName);
            final long downloadId = downloadManager.enqueue(request);

            // Show a toast message indicating the download has started
            Toast.makeText(InstaReelDownloader.this, "Download started", Toast.LENGTH_SHORT).show();
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
                        progressBar.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.fromFile(videoFile));
                        videoView.setMediaController(new MediaController(InstaReelDownloader.this));
                        videoView.start();

                        // Set the flag to indicate that the download is complete
                        isVideoDownloading = false;
                    }
                }
            };

            // Register the BroadcastReceiver
            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }


    // Initialize the Toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // In your activity's onCreate method
    getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);

}

    // Override onCreateOptionsMenu to inflate the menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insta_menu, menu);
        return true;
    }

    // Override onOptionsItemSelected to handle menu item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_history) {
            // Handle the search action
            Intent intent = new Intent(InstaReelDownloader.this, VideoHistory.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.instagram_hack) {
            // Handle the settings action
            Intent intent = new Intent(InstaReelDownloader.this, InstaFetchUsername.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



}