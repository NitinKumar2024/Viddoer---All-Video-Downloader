package com.nitin.videodownloaderpro.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nitin.videodownloaderpro.Instagram_Details.InstaFetchUsername;
import com.nitin.videodownloaderpro.MainActivity;
import com.nitin.videodownloaderpro.Models.Contact;
import com.nitin.videodownloaderpro.R;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private Context context;

    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
      //  holder.contactImage.setImageResource(contact.getImageResource());


        Glide.with(holder.itemView.getContext()).load(contact.getImageResource()).placeholder(R.drawable.viddoer_logo).into(holder.contactImage);
        holder.contactName.setText(contact.getName());
        holder.time.setText(contact.getTime());



        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Username is successfully stored in the database
                Intent videoIntent = new Intent(v.getContext(), MainActivity.class);
                videoIntent.putExtra("username", contact.getName());

                v.getContext().startActivity(videoIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView contactName, time;
        LinearLayout linearLayout;


        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_name);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            time = itemView.findViewById(R.id.contact_time);
        }
    }
}
