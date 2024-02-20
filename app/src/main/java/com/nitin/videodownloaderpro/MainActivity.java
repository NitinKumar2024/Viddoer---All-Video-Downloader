package com.nitin.videodownloaderpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitin.videodownloaderpro.Instagram_Details.InstaFetchUsername;
import com.nitin.videodownloaderpro.Instagram_Details.InstaStorySaver;
import com.nitin.videodownloaderpro.Instagram_Details.InstaVideoProfileDownloader;
import com.nitin.videodownloaderpro.Instagram_Details.Post;
import com.nitin.videodownloaderpro.Instagram_Details.PostAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private Python py;
    ProgressDialog progressDialog;
    private GestureDetector gestureDetector;
    private GridView postsGridView;
    private PostAdapter postAdapter;
    private ImageView imageView;
    private PyObject pyModule;
    private TextView usernameTextView, postTextView, followersTextView, followingsTextView, bioTextView;
    private CircleImageView profileImageView;
    private PyObject pyFunction, py_user_name, py_full_name, py_post, py_followers, py_followings, py_bio, py_post_downloader, py_insta_image;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize UI components
        usernameTextView = findViewById(R.id.usernameTextView);
        postTextView = findViewById(R.id.post);
        followersTextView = findViewById(R.id.followers);
        followingsTextView = findViewById(R.id.followings);
        bioTextView = findViewById(R.id.bioTextView);
        profileImageView = findViewById(R.id.profileImageView);
        postsGridView = findViewById(R.id.postsGridView);
        imageView = findViewById(R.id.help_imageView);



        // Obtain downloadedPosts from Python as described in your question

        ArrayList<Post> postsList = new ArrayList<>();


        postsGridView.setAdapter(postAdapter);


        progressDialog = new ProgressDialog(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlertBar();
            }
        });





        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String user_id = getIntent().getStringExtra("username");







        // Initialize Python and the module
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        pyFunction = py.getModule("insta_hacker").get("get_profile_pic_url");
        py_user_name = py.getModule("insta_hacker").get("get_username");
        py_bio = py.getModule("insta_hacker").get("get_bio");
        py_full_name = py.getModule("insta_hacker").get("get_full_name");
        py_followings = py.getModule("insta_hacker").get("get_following");
        py_followers = py.getModule("insta_hacker").get("get_followers");
        py_post = py.getModule("insta_hacker").get("get_posts");
        py_post_downloader = py.getModule("InstaPostDownloader").get("download_posts");
        py_insta_image = py.getModule("InstaPostDownloader").get("download_caption");






        // Get TabLayout reference from XML
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Set up TabItems with their respective IDs
        TabLayout.Tab tabPost = tabLayout.getTabAt(0);
        TabLayout.Tab tabVideo = tabLayout.getTabAt(1);


        // Set TabSelectedListeners if needed
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                if (tab == tabPost) {
                    // Handle All Post tab selection

                    Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                } else if (tab == tabVideo) {
                    // Handle Video tab selection
                    Intent intent = new Intent(MainActivity.this, InstaVideoProfileDownloader.class);
                    intent.putExtra("username", user_id);
                    startActivity(intent);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection
            }
        });









        user_name(user_id);
        full_name_(user_id);
        full_bio_(user_id);
        full_followers_(user_id);
        full_followings_(user_id);
        full_post_(user_id);
        download_all_post(user_id);





    }






    private void user_name(final String username) {
        progressDialog.setMessage("Profile Picture Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                PyObject result = pyFunction.call(username);


                final String profile_url = result.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        Glide.with(getApplicationContext()).load(profile_url).into(profileImageView);
                        progressDialog.dismiss();

                        profileImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, ImageViewPlayer.class);
                                intent.putExtra("mediaFilePath", profile_url);
                                startActivity(intent);
                            }
                        });

                        profileImageView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Are you sure you want to download this image?")
                                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User clicked the "Delete" button

                                                // Generate a unique filename based on the current timestamp
                                                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                                                final String fileName = timestamp + ".jpg"; // Unique filename


                                                // Create a new File object for the Viddoer directory.
                                                File youtubeVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");

                                                // Create a new File object for the Image directory.
                                                File videoDir = new File(youtubeVideoDir, "Instagram");
                                                videoDir.mkdirs(); // Ensure the directory exists

                                                final File videoFile = new File(videoDir, fileName);

                                                // Download the video to the specified directory
                                                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                                Uri uri = Uri.parse(profile_url);
                                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                                request.setDestinationUri(Uri.fromFile(videoFile));
                                                final long downloadId = downloadManager.enqueue(request);

                                                // Show a toast message indicating the download has started
                                                Toast.makeText(MainActivity.this, "Download started", Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User clicked the "Cancel" button, do nothing
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                return true; // Consume the long-click event
                            }
                        });

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
                        DatabaseReference usersReference = databaseReference.child("Insta_Users/" + deviceInfo + "/"+ androidId);

                        // Generate a unique key for the new user
                        String userId = usersReference.push().getKey();

                        // Create a HashMap to store the username
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("username", username);
                        userMap.put("time", formattedTimestamp);
                        userMap.put("image", profile_url);

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




                    }
                });
            }
        }).start();
    }
    private void full_name_(final String username) {
        progressDialog.setMessage("Username Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area\
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                PyObject full_name = py_full_name.call(username);

                final String full_names = full_name.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        usernameTextView.setText(full_names);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
    private void full_bio_(final String username) {
        progressDialog.setMessage("Bio Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area\
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                PyObject bio = py_bio.call(username);

                final String full_names = bio.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        bioTextView.setText(full_names);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
    private void full_post_(final String username) {
        progressDialog.setMessage("Post Count Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area\
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                PyObject post = py_post.call(username);

                final String post_count = post.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        postTextView.setText("Post: "+ post_count);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
    private void full_followers_(final String username) {
        progressDialog.setMessage("Followers Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area\
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                PyObject followers = py_followers.call(username);

                final String followers_count = followers.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        followersTextView.setText("Followers: "+ followers_count);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
    private void full_followings_(final String username) {
        progressDialog.setMessage("Following Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area\
        progressDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {


                PyObject followings = py_followings.call(username);

                final String followings_counts = followings.toString();




                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {

                        followingsTextView.setText("Followings: " + followings_counts);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }
    private void download_all_post(final String username) {
        progressDialog.setMessage("Profile Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                py_post_downloader = py.getModule("InstaPostDownloader").get("download_posts");
                final PyObject downloadedPosts = py_post_downloader.call(username);


                if (downloadedPosts != null && !downloadedPosts.isEmpty()) {
                    final ArrayList<Post> postsList = new ArrayList<>();

                    for (PyObject postObj : downloadedPosts.asList()) {
                        String image_uri = String.valueOf(postObj);

                        if (image_uri.startsWith("wrong")){

                            openAlertBar();

                        }
                        else {
                            // Create a new Post object for each combination of image_uri and caption
                            Post post = new Post("image", "caption", image_uri, image_uri);
                            postsList.add(post);

                        }


                    }




                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            PostAdapter postAdapter = new PostAdapter(MainActivity.this, postsList);
                            GridView postsGridView = findViewById(R.id.postsGridView);
                            postsGridView.setAdapter(postAdapter);
                        }
                    });
                }  // Handle the case where downloadedPosts is null or empty


                progressDialog.dismiss();
            }
        }).start();


    }
    private void openAlertBar() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("If You Face Any Error, So Please Refresh Your Internet then Try Again!")
                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Glide.clear(rlPageCoverImg);

    }}