package com.nitin.videodownloaderpro;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private List<MediaItem> mediaItems;

    public MediaAdapter(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = (viewType == 0) ? R.layout.item_video : R.layout.item_audio;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final MediaItem item = mediaItems.get(position);
        holder.bind(item);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getMediaType() == MediaItem.MediaType.VIDEO) {

                    // Start the VideoPlayerActivity for video playback
                    Intent videoIntent = new Intent(view.getContext(), VideoPlayerActivity.class);
                    videoIntent.putExtra("mediaFilePath", item.getFilePath());
                    view.getContext().startActivity(videoIntent);
                } else if (item.getMediaType() == MediaItem.MediaType.AUDIO) {
                    // Debug log to check if this branch is executed
                    Toast.makeText(view.getContext(), "Audio", Toast.LENGTH_SHORT).show();



                    // Start the AudioPlayerActivity for audio playback
                    Intent audioIntent = new Intent(view.getContext(), AudioPlayerActivity.class);
                    audioIntent.putExtra("mediaFilePath", item.getFilePath());
                    String file_name = holder.fileNameTextView.getText().toString();
                    // Pass the list of audio items and the current position
              //      Intent intent = audioIntent.putParcelableArrayListExtra("audioItems", mediaItems);
                    audioIntent.putExtra("position", item.getPosition());
                    // Populate mediaItems with video and audio files from the gallery.
                    mediaItems = retrieveMediaItemsFromGallery(view); // Initialize the instance variable
                    audioIntent.putExtra("song_list", mediaItems.toString());
                    // Pass the file name as an extra
                    audioIntent.putExtra("fileName", file_name);
                    view.getContext().startActivity(audioIntent);
                } else {
                    // Debug log to check if this branch is executed
                    Log.d("MediaAdapter", "Error");

                    Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.fileNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getMediaType() == MediaItem.MediaType.VIDEO) {

                    // Start the VideoPlayerActivity for video playback
                    Intent videoIntent = new Intent(view.getContext(), VideoPlayerActivity.class);
                    videoIntent.putExtra("mediaFilePath", item.getFilePath());
                    view.getContext().startActivity(videoIntent);
                } else if (item.getMediaType() == MediaItem.MediaType.AUDIO) {
                    // Debug log to check if this branch is executed
                    Toast.makeText(view.getContext(), "Audio", Toast.LENGTH_SHORT).show();



                    // Start the AudioPlayerActivity for audio playback
                    Intent audioIntent = new Intent(view.getContext(), AudioPlayerActivity.class);
                    audioIntent.putExtra("mediaFilePath", item.getFilePath());
                    String file_name = holder.fileNameTextView.getText().toString();
                    // Pass the list of audio items and the current position
                    //      Intent intent = audioIntent.putParcelableArrayListExtra("audioItems", mediaItems);
                    audioIntent.putExtra("position", item.getPosition());
                    // Populate mediaItems with video and audio files from the gallery.
                    mediaItems = retrieveMediaItemsFromGallery(view); // Initialize the instance variable
                    audioIntent.putExtra("song_list", mediaItems.toString());
                    // Pass the file name as an extra
                    audioIntent.putExtra("fileName", file_name);
                    view.getContext().startActivity(audioIntent);
                } else {
                    // Debug log to check if this branch is executed
                    Log.d("MediaAdapter", "Error");

                    Toast.makeText(view.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.share_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getMediaType() == MediaItem.MediaType.VIDEO) {
                    // Share the video
                    shareVideo(view.getContext(), item.getFilePath());
                } else if (item.getMediaType() == MediaItem.MediaType.AUDIO) {
                    // Share the audio (you can implement this part as needed)
                    shareAudio(view.getContext(), item.getFilePath());
                } else {
                    // Handle other media types or errors
                    Log.d("MediaAdapter", "Error: Unsupported media type");
                    Toast.makeText(view.getContext(), "Error: Unsupported media type", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.rename_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Rename File");

                // Create an EditText view for user input
                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFileName = input.getText().toString().trim();
                        if (!TextUtils.isEmpty(newFileName)) {
                            // Perform the renaming action here
                            if (item.getMediaType() == MediaItem.MediaType.VIDEO) {
                                // Rename video file
                                renameVideoFile(view.getContext(), item.getFilePath(), newFileName, position);
                            } else if (item.getMediaType() == MediaItem.MediaType.AUDIO) {
                                // Rename audio file
                                renameAudioFile(view.getContext(), item.getFilePath(), newFileName, position);
                            }
                        } else {
                            Toast.makeText(view.getContext(), "Please enter a valid name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        holder.fileNameTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete this file?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked the "Delete" button
                                deleteMediaItem(position, view);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked the "Cancel" button, do nothing
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true; // Consume the long-click event
            }
        });






    }

    private List<MediaItem> retrieveMediaItemsFromGallery(View view) {
        List<MediaItem> mediaItems = new ArrayList<>();

        // Define the columns you want to retrieve from MediaStore for videos
        String[] videoProjection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA

        };

        // Retrieve videos
        Cursor videoCursor = view.getContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                null
        );

        if (videoCursor != null) {
            int position = 0; // Initialize the position counter
            while (videoCursor.moveToNext()) {
                String fileName = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String filePath = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                mediaItems.add(new MediaItem(fileName, filePath, MediaItem.MediaType.VIDEO, position));
                position++; // Increment the position counter
            }
            videoCursor.close();
        }

        // Define the columns you want to retrieve from MediaStore for audio files
        String[] audioProjection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA

        };

        // Retrieve audio files
        Cursor audioCursor = view.getContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioProjection,
                null,
                null,
                null
        );

        if (audioCursor != null) {
            int position = 0; // Initialize the position counter
            while (audioCursor.moveToNext()) {
                String fileName = audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String filePath = audioCursor.getString(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                mediaItems.add(new MediaItem(fileName, filePath, MediaItem.MediaType.AUDIO, position));
                position++; // Increment the position counter
            }
            audioCursor.close();
        }

        return mediaItems;
    }

    private void deleteMediaItem(int position, View view) {
        if (position >= 0 && position < mediaItems.size()) {
            MediaItem mediaItem = mediaItems.get(position);
            String filePath = mediaItem.getFilePath();

            // Remove the item from the list
            mediaItems.remove(position);

            // Notify the adapter that the item has been removed
            notifyDataSetChanged();


            // Optionally, delete the file from storage
            if (deleteFileFromStorage(filePath)) {
                Toast.makeText(view.getContext(), "File deleted successfully", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(view.getContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Implement a method to delete the file from storage
    private boolean deleteFileFromStorage(String filePath) {
        File fileToDelete = new File(filePath);

        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                return true; // File deleted successfully
            } else {
                return false; // Failed to delete file
            }
        } else {
            return true; // File does not exist, consider it as deleted
        }
    }


    private void renameVideoFile(Context context, String oldFilePath, String newFileName, int position) {
        File oldFile = new File(oldFilePath);

        if (oldFile.exists()) {
            String directoryPath = oldFile.getParentFile().getAbsolutePath();
            String newFilePath = directoryPath + File.separator + newFileName + ".mp4"; // Change the file extension if needed

            File newFile = new File(newFilePath);

            if (oldFile.renameTo(newFile)) {
                // The file has been successfully renamed

                // Update the mediaItems list at the specified position
                if (position >= 0 && position < mediaItems.size()) {
                    MediaItem mediaItem = mediaItems.get(position);
                    mediaItem.setFileName(newFileName); // Update the file name in the MediaItem
                    mediaItem.setFilePath(newFilePath); // Update the file path in the MediaItem
                }

                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                Toast.makeText(context, "File renamed successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to rename the file
                Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // File does not exist
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
        }
    }


    private void renameAudioFile(Context context, String oldFilePath, String newFileName, int position) {
        File oldFile = new File(oldFilePath);

        if (oldFile.exists()) {
            String directoryPath = oldFile.getParentFile().getAbsolutePath();
            String newFilePath = directoryPath + File.separator + newFileName + ".mp3"; // Change the file extension if needed

            File newFile = new File(newFilePath);

            if (oldFile.renameTo(newFile)) {
                // The file has been successfully renamed

                // Update the mediaItems list at the specified position
                if (position >= 0 && position < mediaItems.size()) {
                    MediaItem mediaItem = mediaItems.get(position);
                    mediaItem.setFileName(newFileName); // Update the file name in the MediaItem
                    mediaItem.setFilePath(newFilePath); // Update the file path in the MediaItem
                }

                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                Toast.makeText(context, "File renamed successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Failed to rename the file
                Toast.makeText(context, "Failed to rename file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // File does not exist
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
        }
    }



    private void shareAudio(Context context, String audioFilePath) {
        File audioFile = new File(audioFilePath);

        if (audioFile.exists()) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", audioFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission

            context.startActivity(Intent.createChooser(shareIntent, "Share Audio"));
        } else {
            Toast.makeText(context, "Audio file not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareVideo(Context context, String audioFilePath) {
        File audioFile = new File(audioFilePath);

        if (audioFile.exists()) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", audioFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("Video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant read permission

            context.startActivity(Intent.createChooser(shareIntent, "Share Audio"));
        } else {
            Toast.makeText(context, "Audio file not found.", Toast.LENGTH_SHORT).show();
        }
    }

    public class LocalMediaItemProvider {
        public List<String> getFilePaths(String directoryPath) {
            List<String> filePaths = new ArrayList<>();
            File directory = new File(directoryPath);

            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        // Add the absolute path of the file to the list
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }

            return filePaths;
        }
    }



    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        // Return an integer representing the view type.
        MediaItem.MediaType mediaType = mediaItems.get(position).getMediaType();
        return (mediaType == MediaItem.MediaType.VIDEO) ? 0 : 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fileNameTextView;
        private ImageView share_icon, rename_icon;
        private RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);

            share_icon = itemView.findViewById(R.id.icon_share);
            rename_icon = itemView.findViewById(R.id.icon_rename);
            relativeLayout = itemView.findViewById(R.id.relative_all_layout);
        }

        public void bind(MediaItem item) {
            fileNameTextView.setText(item.getFileName());

            // Handle item click here (e.g., open the media file).
        }}




}
