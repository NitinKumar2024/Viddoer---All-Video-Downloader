package com.nitin.videodownloaderpro.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitin.videodownloaderpro.AudioPlayerActivity;
import com.nitin.videodownloaderpro.Models.MusicModel;
import com.nitin.videodownloaderpro.R;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {

    Context context;
    List<MusicModel> musicModels;

    public MusicAdapter(Context context, List<MusicModel> musicModels) {
        this.context = context;
        this.musicModels = musicModels;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(context).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textView.setText(musicModels.get(position).getTitle());

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AudioPlayerActivity.class);
                intent.putExtra("mediaFilePath", musicModels.get(position).getMusic_url());
                intent.putExtra("fileName", musicModels.get(position).getTitle());
             //   intent.putExtra("position", musicModels.get(position).getMusic_url());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line to set the flag
                v.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return musicModels.size();
    }
}
