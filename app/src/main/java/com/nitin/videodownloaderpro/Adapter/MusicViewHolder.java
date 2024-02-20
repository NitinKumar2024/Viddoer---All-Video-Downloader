package com.nitin.videodownloaderpro.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nitin.videodownloaderpro.R;

public class MusicViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    public MusicViewHolder(@NonNull View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.music_title);
    }
}
