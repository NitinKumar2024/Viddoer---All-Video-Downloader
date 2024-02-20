package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

public class GameTicTac extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_tic_tac);

        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);


        // Load the HTML file from assets folder into WebView
        try {
            InputStream inputStream = getAssets().open("tic_tac_toe_game.html");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String htmlContent = new String(buffer);
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}