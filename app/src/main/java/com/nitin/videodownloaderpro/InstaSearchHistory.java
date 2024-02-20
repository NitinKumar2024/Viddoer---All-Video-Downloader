package com.nitin.videodownloaderpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitin.videodownloaderpro.Adapter.ContactAdapter;
import com.nitin.videodownloaderpro.Models.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InstaSearchHistory extends AppCompatActivity {

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_search_history);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(InstaSearchHistory.this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        List<Contact> contactList = new ArrayList<>();
        // Add sample data
       // contactList.add(new Contact(R.drawable.nitin, "John"));


//        ContactAdapter adapter = new ContactAdapter(contactList);
//        recyclerView.setAdapter(adapter);

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


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Insta_Users/" + deviceInfo + "/"+ androidId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        if (snapshot.exists()) {
                            String email = userSnapshot.child("username").getValue(String.class);
                            if (email != null) { // add null check here

                                String times = userSnapshot.child("time").getValue(String.class);
                                String profile_url = userSnapshot.child("image").getValue(String.class);
                                contactList.add(new Contact(profile_url, email, times));
                                progressDialog.dismiss();

                            } else {
                                Toast.makeText(InstaSearchHistory.this, "Null Error", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(InstaSearchHistory.this, "Empty Error", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }
                else {
                    Toast.makeText(InstaSearchHistory.this, "Wrong error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                ContactAdapter adapter = new ContactAdapter(contactList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error here
                Toast.makeText(InstaSearchHistory.this, "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }
}