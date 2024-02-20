package com.nitin.videodownloaderpro.Whatsapp_Status;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;


import com.nitin.videodownloaderpro.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class WhatsappStatus extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 10001;
    private static final int REQUEST_CODE_SAVE_MEDIA = 20001;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PERMISSION_GRANTED_KEY = "permission_granted";

    private List<Uri> mediaFiles = new ArrayList<>();
    private Uri selectedFolderUri;
    private MediaAdapter mediaAdapter;
    private RecyclerView recyclerView;
    private List<Uri> status = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_status);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mediaAdapter = new MediaAdapter(this, mediaFiles);
        recyclerView.setAdapter(mediaAdapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {


            //  Request storage permission if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        openDocumentTree();

    }

    private void openDocumentTree() {
        // Check if the selected folder URI is already stored in SharedPreferences
        // Accessing data from SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

// Retrieving a String value (for example, the selected folder URI)
        String selectedFolderUriString = settings.getString("selected_folder_uri", null);


        if (selectedFolderUriString != null) {
            // If the URI is available in SharedPreferences, use it to fetch data directly
            selectedFolderUri = Uri.parse(selectedFolderUriString);
            fetchMediaData();
        }else {
            // If document tree access is not available, open the document tree
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            // Optionally, specify a URI for the directory that should be opened in the system file picker when it loads.

            // Encode the URI before using it
            // String encodedUriString = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fdocument/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
            String encodedUriString = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
            Uri waStatusUri = Uri.parse(Uri.encode(encodedUriString));

            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, waStatusUri);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Go to Whatsapp Status Folder and Click Use this folder button.")
                    .setPositiveButton("Go", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button
                            startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
                            Toast.makeText(WhatsappStatus.this, "Click to Use this folder", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Cancel" button, do nothing
                            dialog.dismiss();
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();






        }
    }

    private void fetchMediaData() {

        DocumentFile fileDoc = DocumentFile.fromTreeUri(WhatsappStatus.this, selectedFolderUri);

        for (DocumentFile file : fileDoc.listFiles()) {
            mediaFiles.add(file.getUri());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mediaAdapter = new MediaAdapter(this, mediaFiles);
            recyclerView.setAdapter(mediaAdapter);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openDocumentTree();
        } else {
            Toast.makeText(this, "Permission denied. Cannot proceed.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE && resultCode == RESULT_OK && data != null) {
            selectedFolderUri = data.getData();
            if (selectedFolderUri != null) {
                
                getContentResolver().takePersistableUriPermission(selectedFolderUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Save the selected folder URI in SharedPreferences
                saveSelectedFolderUri(selectedFolderUri);
                
                fetchMediaData();
 


            }
        } else if (requestCode == REQUEST_CODE_SAVE_MEDIA && resultCode == RESULT_OK && data != null) {
            // Handle the result of save operation if needed
            // For example, show a success message
            Toast.makeText(this, "Media saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSelectedFolderUri(Uri folderUri) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("selected_folder_uri", folderUri.toString());
        editor.apply();
    }

}