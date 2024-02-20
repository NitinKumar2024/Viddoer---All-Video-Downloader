package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nitin.videodownloaderpro.Instagram_Details.InstaFetchUsername;
import com.nitin.videodownloaderpro.Notification_music.MusicApp;
import com.nitin.videodownloaderpro.Notification_music.MusicNotificaion;
import com.nitin.videodownloaderpro.Whatsapp_Status.WhatsappStatus;


public class FrontApp extends AppCompatActivity {

    private CardView youtube, whatsapp, instagram, facebook;

    private GestureDetector gestureDetector;
    private TableLayout tableLayout;
    private ConstraintLayout constraintLayout;
    private Handler handler;
    private LinearLayout linearLayout;
    private TextView textView;

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;
    private View overlayView;
    private AdView mAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_app);

        youtube = findViewById(R.id.youtube);
        whatsapp = findViewById(R.id.whatsapp);
        instagram = findViewById(R.id.instagram);
        facebook = findViewById(R.id.facebook);
        constraintLayout = findViewById(R.id.background_color);
        linearLayout = findViewById(R.id.linearLayout_background);
        textView = findViewById(R.id.textView);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-8404134982147261/7245152310");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);






        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        handler = new Handler();

        // Start the automatic color change loop
        startColorChange();


        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Remove callbacks to stop the automatic color change
                handler.removeCallbacksAndMessages(null);
                return true;
            }
        });





        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontApp.this, YoutubeVideo.class);
                startActivity(intent);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Choose a directory using the system's file picker.
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
//                // Optionally, specify a URI for the directory that should be opened in
//                // the system file picker when it loads.
//
//                Uri wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
//                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri);
//                startActivityForResult(intent, 10001);
                Intent intent = new Intent(FrontApp.this, WhatsappStatus.class);
                startActivity(intent);

            }
        });
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontApp.this, InstaReelDownloader.class);
                startActivity(intent);
               // Toast.makeText(FrontApp.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FrontApp.this, MusicApp.class);
                startActivity(intent);

            }
        });



        // Initialize the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // In your activity's onCreate method
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);


        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
    }




    private void startColorChange() {
        // Define a Runnable to change the color
        Runnable colorChangeRunnable = new Runnable() {
            @Override
            public void run() {
                // Generate random color
                int randomColor = Color.rgb((int) (Math.random() * 256),
                        (int) (Math.random() * 256),
                        (int) (Math.random() * 256));

                // Set the background color of the mainView to the random color
                constraintLayout.setBackgroundColor(randomColor);

                // Call the runnable again after a specific delay (in milliseconds)
                handler.postDelayed(this, 10000); // Change color every 1 second (1000 milliseconds)
            }
        };

        // Post the initial color change runnable with a delay
        handler.postDelayed(colorChangeRunnable, 10000); // Start changing color after 1 second (1000 milliseconds)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks when the activity is destroyed
        handler.removeCallbacksAndMessages(null);
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
            float deltaY = e2.getY() - e1.getY();
            float deltaX = e2.getX() - e1.getX();

            if (Math.abs(deltaY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (deltaY < 0) {
                    // Swipe from top to bottom
                    // Handle this swipe if needed
                    Intent intent = new Intent(FrontApp.this, GameTicTac.class);
                    startActivity(intent);

                    return true;
                } else {
                    // Swipe from bottom to top
                    // Handle this swipe if needed
                    Intent intent = new Intent(FrontApp.this, VideoHistory.class);
                    startActivity(intent);
                    return true;
                }
            } else if (Math.abs(deltaX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (deltaX > 0) {
                    // Swipe right, navigate to another activity
                    Intent intent = new Intent(FrontApp.this, InstaFetchUsername.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else {
                    // Handle swipe left if needed
                    // Swipe right, navigate to another activity
                    Intent intent = new Intent(FrontApp.this, VideoHistory.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
            }

            return false;
        }
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
            Intent intent = new Intent(FrontApp.this, VideoHistory.class);
            startActivity(intent);
            return true;
        }  else if (id == R.id.action_nitin) {

            Intent intent = new Intent(FrontApp.this, AboutDeveloper.class);
            startActivity(intent);

            return true;

        }
        else if (id == R.id.instagram_hack) {

            Intent intent = new Intent(FrontApp.this, InstaFetchUsername.class);
            startActivity(intent);

            return true;

        }
        else if (id == R.id.help_user) {

            Intent intent = new Intent(FrontApp.this, HelpUser.class);
            startActivity(intent);

            return true;

        }
        else if (id == R.id.game_tic_tac) {

            Intent intent = new Intent(FrontApp.this, GameTicTac.class);
            startActivity(intent);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    // ... other activity methods ...


    }



