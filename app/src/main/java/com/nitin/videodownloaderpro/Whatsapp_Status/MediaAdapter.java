package com.nitin.videodownloaderpro.Whatsapp_Status;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nitin.videodownloaderpro.ImageViewPlayer;
import com.nitin.videodownloaderpro.R;
import com.nitin.videodownloaderpro.VideoPlayerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private List<Uri> mediaFiles;
    private List<Uri> media;
    private Context context;

    public MediaAdapter(Context context, List<Uri> mediaFiles) {
        this.context = context;
        this.mediaFiles = mediaFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsapp_item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri mediaUri = mediaFiles.get(position);
        String mediaType = context.getContentResolver().getType(mediaUri);

        if (mediaType != null) {
            if (mediaType.startsWith("image")) {
                // If it's an image file, load it using Glide
                holder.imageView.setVisibility(View.VISIBLE);
                holder.videoView.setVisibility(View.GONE);
                holder.video_button.setVisibility(View.GONE);
                holder.button.setVisibility(View.VISIBLE);

                Glide.with(context).load(mediaUri).into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start the VideoPlayerActivity for video playback
                        Intent videoIntent = new Intent(view.getContext(), ImageViewPlayer.class);
                        videoIntent.putExtra("mediaFilePath", mediaUri.toString());

                        view.getContext().startActivity(videoIntent);


                    }
                });


                // Assuming you have a saveButton instance in your code
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Generate a unique filename based on the current timestamp
                        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                        final String fileName = timestamp + ".jpg"; // Unique filename

                        // Use the public "Movies" directory for downloads
                        //   File videoDir = new File(Environment.DIRECTORY_DOCUMENTS + File.separator + "Viddoer");
                        //  File videoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

                        File youtubeVideoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Viddoer");

// Create a new File object for the Youtube_Video directory.
                        File videoDir = new File(youtubeVideoDir, "Viddoer_Whatsapp");
                        videoDir.mkdirs(); // Ensure the directory exists


                        // Implement the logic to download the media file to the specified directory
                        // For example, you can use InputStream and OutputStream to copy the file
                        try {
                            InputStream inputStream = context.getContentResolver().openInputStream(mediaUri);
                            File outputFile = new File(videoDir, fileName);

                            OutputStream outputStream = new FileOutputStream(outputFile);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }

                            // Close streams
                            outputStream.close();
                            inputStream.close();

                            // Show a success message or handle the download completion as needed
                            Toast.makeText(context, "File downloaded successfully!", Toast.LENGTH_SHORT).show();

                            // Refresh the gallery to display the downloaded image
                          //  MediaScannerConnection.scanFile(context, new String[]{outputFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);

                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle any errors that occur during the download process
                            Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show();
                        }
                    }


                });










            } else if (mediaType.startsWith("video")) {
                // If it's a video file, set it to the VideoView
                holder.imageView.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.VISIBLE);
                holder.button.setVisibility(View.GONE);
                holder.video_button.setVisibility(View.VISIBLE);

                Glide.with(context).load(mediaUri).into(holder.videoView);

                holder.videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Start the VideoPlayerActivity for video playback
                        Intent videoIntent = new Intent(view.getContext(), VideoPlayerActivity.class);
                        videoIntent.putExtra("mediaFilePath", mediaUri.toString());

                        view.getContext().startActivity(videoIntent);
                    }
                });


                // Assuming you have a saveButton instance in your code
                holder.video_button.setOnClickListener(new View.OnClickListener() {
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
                        File videoDir = new File(youtubeVideoDir, "Viddoer_Whatsapp");
                        videoDir.mkdirs(); // Ensure the directory exists


                        // Implement the logic to download the media file to the specified directory
                        // For example, you can use InputStream and OutputStream to copy the file
                        try {
                            InputStream inputStream = context.getContentResolver().openInputStream(mediaUri);
                            File outputFile = new File(videoDir, fileName);

                            OutputStream outputStream = new FileOutputStream(outputFile);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }

                            // Close streams
                            outputStream.close();
                            inputStream.close();

                            // Show a success message or handle the download completion as needed
                            Toast.makeText(context, "File downloaded successfully!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle any errors that occur during the download process
                            Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show();
                        }
                    }


                });



            } else {
                // Handle other file types or unknown types
                holder.imageView.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.GONE);
                holder.button.setVisibility(View.GONE);
                holder.video_button.setVisibility(View.GONE);
            }
        }


    }



    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView videoView;
        Button button, video_button, share_video, share_image;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            videoView = itemView.findViewById(R.id.videoView);
            button = itemView.findViewById(R.id.save_btn);
            video_button = itemView.findViewById(R.id.video_save_btn);

        }
    }
}