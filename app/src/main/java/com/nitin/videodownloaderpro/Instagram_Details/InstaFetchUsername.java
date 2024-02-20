package com.nitin.videodownloaderpro.Instagram_Details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nitin.videodownloaderpro.InstaSearchHistory;
import com.nitin.videodownloaderpro.MainActivity;
import com.nitin.videodownloaderpro.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class InstaFetchUsername extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private ImageView search;
    ProgressDialog progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_fetch_username);

        editText = findViewById(R.id.editTextLink);
        button = findViewById(R.id.buttonFetch);
        search = findViewById(R.id.search_imageView);
        progressBar = new ProgressDialog(InstaFetchUsername.this);
        progressBar.setTitle("Loading...");
        // Lock the screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editText.getText().toString();

                if (!username.isEmpty()) {
                    progressBar.show();
                    // Get a reference to the "users" node in your database
                    DatabaseReference usersReference = databaseReference.child("Insta_Users/" + deviceInfo + "/"+ androidId);

                    // Generate a unique key for the new user
                    String userId = usersReference.push().getKey();

                    // Create a HashMap to store the username
                    HashMap<String, Object> userMap = new HashMap<>();
//                    userMap.put("username", username);
//                    userMap.put("time", formattedTimestamp);

                    // Set the username in the database under the generated user ID
                    usersReference.child(userId).setValue(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Username is successfully stored in the database
                                        Intent videoIntent = new Intent(InstaFetchUsername.this, MainActivity.class);
                                        videoIntent.putExtra("username", username);

                                        startActivity(videoIntent);
                                        progressBar.dismiss();
                                    //    Toast.makeText(InstaFetchUsername.this, "Username stored in Firebase", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressBar.dismiss();
                                        // Failed to store username
                                        Toast.makeText(InstaFetchUsername.this, "Failed to Internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    progressBar.dismiss();
                    // Username is empty, show an error message
                    Toast.makeText(InstaFetchUsername.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InstaFetchUsername.this, InstaSearchHistory.class);
                startActivity(intent);
            }
        });
    }
}