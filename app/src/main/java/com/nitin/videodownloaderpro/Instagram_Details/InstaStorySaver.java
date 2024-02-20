package com.nitin.videodownloaderpro.Instagram_Details;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.GridView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.nitin.videodownloaderpro.R;

import java.util.ArrayList;
import java.util.Collections;

public class InstaStorySaver extends AppCompatActivity {

    private PyObject pyFunction;
    private Python py;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_story_saver);
        String user_id = getIntent().getStringExtra("username");





        progressDialog = new ProgressDialog(this);

        // Initialize Python and the module
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
        pyFunction = py.getModule("InstaPostDownloader").get("status");


        download_all_post(user_id);

    }


    private void download_all_post(final String username) {
        progressDialog.setMessage("Profile Loading..."); // Set your custom message here
        progressDialog.setCancelable(false); // Set whether the dialog can be canceled by tapping outside of the dialog area
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                final PyObject downloadedPosts = pyFunction.call(username);

                if (downloadedPosts != null && !downloadedPosts.isEmpty()) {
                    final ArrayList<Post> postsList = new ArrayList<>();

                    for (PyObject postObj : downloadedPosts.asList()) {
                        String image_uri = String.valueOf(postObj);


                        if (image_uri.contains("wrong")){

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

                            // Remove all occurrences of null from the list
                            postsList.removeAll(Collections.singleton(null));

                            PostAdapter postAdapter = new PostAdapter(InstaStorySaver.this, postsList);
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
        builder.setTitle("Please Refresh Your Internet then Try Again!")
                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}