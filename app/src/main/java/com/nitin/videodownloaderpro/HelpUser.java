package com.nitin.videodownloaderpro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HelpUser extends AppCompatActivity {

    private ConstraintLayout all;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_user);

        all = findViewById(R.id.help_user);

        web("https://www.viddoer.com/contacts");

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HelpUser.this, "Only you can see this page.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void web(String url){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}