package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutDeveloper extends AppCompatActivity {

    private ImageView imageViewInstagram;
    private ImageView imageViewYouTube;
    private ImageView imageViewGitHub;
    private ImageView imageViewFacebook;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_developer);

        // Initialize ImageView references
        imageViewInstagram = findViewById(R.id.imageViewInstagram);
        imageViewYouTube = findViewById(R.id.imageViewYouTube);
        imageViewGitHub = findViewById(R.id.imageViewGitHub);
        imageViewFacebook = findViewById(R.id.imageViewFacebook);

        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView imageViewDeveloper = findViewById(R.id.imageViewDeveloper);


        // Set click listeners for social media icons
        imageViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://www.instagram.com/nitin_kumar_2023/");
            }
        });

        imageViewYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://www.youtube.com/@adventureswithnitin/");
            }
        });

        imageViewGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://github.com/nitinkumar2024/");
            }
        });

        imageViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //    Toast.makeText(AboutDeveloper.this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                openSocialMediaLink("https://www.facebook.com/profile.php?id=100090531908069&mibextid=ZbWKwL");
            }
        });
    }

    // Function to open a social media link in a web browser
    private void openSocialMediaLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}