package com.nitin.videodownloaderpro.Instagram_Details;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nitin.videodownloaderpro.ImageViewPlayer;
import com.nitin.videodownloaderpro.R;
import com.nitin.videodownloaderpro.VideoPlayerActivity;
import com.nitin.videodownloaderpro.Whatsapp_Status.WhatsappStatus;
import com.nitin.videodownloaderpro.YoutubeVideo;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends ArrayAdapter<Post> {



    public PostAdapter(Context context, ArrayList<Post> posts) {
        super(context, R.layout.item_gridview, posts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder.imageView = convertView.findViewById(R.id.postImageView);
            viewHolder.captionTextView = convertView.findViewById(R.id.captionTextView);
            viewHolder.downloadButton = convertView.findViewById(R.id.downloadButton);
            viewHolder.progressBar = convertView.findViewById(R.id.progressBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progressBar.setVisibility(View.VISIBLE);


        viewHolder.captionTextView.setText(post.getCaption());
        viewHolder.captionTextView.setVisibility(View.INVISIBLE);


        if (post.getImageUrl() != null) {


            if (post.getImageUrl().startsWith("wrong")){
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.captionTextView.setVisibility(View.GONE);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.downloadButton.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Please Refresh Your Internet ", Toast.LENGTH_SHORT).show();
                openAlertBar();

            }


            else if (post.getImageUrl().contains(".mp4")) {
                //  Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.viddoer_logo).into(viewHolder.imageView);
                Glide.with(getContext()).load(post.getImageUrl()).placeholder(R.drawable.viddoer_logo).into(viewHolder.imageView);

                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start the VideoPlayerActivity for video playback
                        Intent videoIntent = new Intent(view.getContext(), VideoPlayerActivity.class);
                        videoIntent.putExtra("mediaFilePath", post.getImageUrl());

                        getContext().startActivity(videoIntent);

                    }
                });

                viewHolder.downloadButton.setVisibility(View.VISIBLE);
                viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Generate a unique filename based on the current timestamp
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        final String fileName = timestamp + ".mp4"; // Unique filename

                        // Use the public "Movies" directory for downloads
                        //   File videoDir = new File(Environment.DIRECTORY_DOCUMENTS + File.separator + "Viddoer");
                        //  File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

                        File youtubeVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");

// Create a new File object for the Youtube_Video directory.
                        File videoDir = new File(youtubeVideoDir, "Viddoer_Instagram");
                        videoDir.mkdirs(); // Ensure the directory exists

                        final File videoFile = new File(videoDir, fileName);




                        // Download the video to the specified directory
                        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(post.getImageUrl());
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationUri(Uri.fromFile(videoFile));
                        request.setDescription("Viddoer");
                        request.setTitle(fileName);
                        downloadManager.enqueue(request);


                    }
                });



            } else {

                viewHolder.downloadButton.setVisibility(View.GONE);

                // Picasso.get().load(post.getImageUrl()).placeholder(R.drawable.viddoer_logo).into(viewHolder.imageView);
                Glide.with(getContext()).load(post.getImageUrl()).placeholder(R.drawable.viddoer_logo).into(viewHolder.imageView);
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start the VideoPlayerActivity for video playback
                        Intent videoIntent = new Intent(view.getContext(), ImageViewPlayer.class);
                        videoIntent.putExtra("mediaFilePath", post.getImageUrl());

                        getContext().startActivity(videoIntent);
                        // Toast.makeText(getContext(), "Only you can see this profile", Toast.LENGTH_SHORT).show();
                    }
                });
            }



        }

        else {
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.captionTextView.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.downloadButton.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Please Refresh Your Internet ", Toast.LENGTH_SHORT).show();
            openAlertBar();

        }

        // Set click listener for the download button




        return convertView;
    }

    private void openAlertBar() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Please Refresh Your Internet then Try Again!")
                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private static class ViewHolder {
        ImageView imageView;
        TextView captionTextView;
        Button downloadButton;
        ProgressBar progressBar;
    }
}
