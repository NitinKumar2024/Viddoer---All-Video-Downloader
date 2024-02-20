package com.nitin.videodownloaderpro.Notification_music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nitin.videodownloaderpro.Adapter.MusicAdapter;
import com.nitin.videodownloaderpro.InstaReelDownloader;
import com.nitin.videodownloaderpro.Models.MusicModel;
import com.nitin.videodownloaderpro.R;
import com.nitin.videodownloaderpro.YoutubeVideo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MusicApp extends AppCompatActivity {

    private Python py;
    private PyObject pyFunction;
    private EditText editTextLink;
    private Button buttonFetch;
    private ProgressBar progressBar;
    private VideoView videoView;
    //  private Button buttonDownload;
    private Button buttonPaste;

    private TextView hosting_texts, title_text;

    private RewardedAd rewardedAd;
    private final String TAG = "YoutubeVideo";



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_app);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.music_recycler);

        List<MusicModel> musicModels = new ArrayList<MusicModel>();
        // Initialize Python and the module
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        pyFunction = py.getModule("video_hosting").get("download_playlist_audio");

        editTextLink = findViewById(R.id.editTextLink);
        buttonFetch = findViewById(R.id.buttonFetch);
        progressBar = findViewById(R.id.progressBar);
        videoView = findViewById(R.id.videoView);
        //  buttonDownload = findViewById(R.id.buttonDownload);
        hosting_texts = findViewById(R.id.hosting_text);
        buttonPaste = findViewById(R.id.buttonPaste);
        title_text = findViewById(R.id.title_text);

        ImageView imageView = findViewById(R.id.help_imageView);
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






        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setVisibility(View.GONE);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Set<String> retrievedPostUrlSet = sharedPreferences.getStringSet("postUrls", new HashSet<>());
                List<String> retrievedPostUrls = new ArrayList<>(retrievedPostUrlSet);

                for (String all_links: retrievedPostUrls){
                    String first_title = all_links.substring(0, all_links.indexOf("https"));
                    String second_link = all_links.substring(all_links.indexOf("https"));


                    musicModels.add(new MusicModel(first_title, second_link));
                }
                RecyclerView recyclerView = findViewById(R.id.music_recycler);

                recyclerView.setLayoutManager(new LinearLayoutManager(MusicApp.this));

                LinearLayout linearLayout = findViewById(R.id.all_display);
                linearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                recyclerView.setAdapter(new MusicAdapter(getApplicationContext(), musicModels));

            }
        });

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoLink = editTextLink.getText().toString().trim();
                if (!videoLink.isEmpty()) {
                    //  fetchHostingLink(videoLink);
                    download_all_post(videoLink);

                }
                else {
                    Toast.makeText(MusicApp.this, "Put Link First", Toast.LENGTH_SHORT).show();
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




        // Check if the app was launched from a shared text
        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            editTextLink.setText(sharedText);

            buttonFetch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download_all_post(sharedText);

                }
            });



        }

    }

    private void download_all_post(final String username) {

        if (rewardedAd != null) {
            Activity activityContext = MusicApp.this;
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



        new Thread(new Runnable() {
            @Override
            public void run() {

                final PyObject downloadedPosts = pyFunction.call(username);
                List<MusicModel> musicModels = new ArrayList<MusicModel>();






                if (downloadedPosts != null && !downloadedPosts.isEmpty()) {


                    for (PyObject postObj : downloadedPosts.asList()) {



                        String image_uri = String.valueOf(postObj);


                        if (image_uri.startsWith("wrong")) {

                            Toast.makeText(MusicApp.this, "By", Toast.LENGTH_SHORT).show();

                        } else {
                            // Create a new Post object for each combination of image_uri and caption

                            String first_title = image_uri.substring(0, image_uri.indexOf("https"));
                            String second_link = image_uri.substring(image_uri.indexOf("https"));


                            musicModels.add(new MusicModel(first_title, second_link));


                        }


                    }

                    Set<String> postUrlSet = new HashSet<>();
                    for (PyObject postObj : downloadedPosts.asList()) {
                        String postUrl = String.valueOf(postObj); // Replace with the actual method to get the post URL
                        postUrlSet.add(postUrl);
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet("postUrls", postUrlSet);
                    editor.apply();


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {

                            RecyclerView recyclerView = findViewById(R.id.music_recycler);

                            recyclerView.setLayoutManager(new LinearLayoutManager(MusicApp.this));

                            LinearLayout linearLayout = findViewById(R.id.all_display);
                            linearLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            recyclerView.setAdapter(new MusicAdapter(getApplicationContext(), musicModels));





                        }
                    });
                }  // Handle the case where downloadedPosts is null or empty


            }
        }).start();

    }
}