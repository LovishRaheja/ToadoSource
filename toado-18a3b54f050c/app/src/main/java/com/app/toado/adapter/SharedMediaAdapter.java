package com.app.toado.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.Toast;

import com.app.toado.R;

import com.app.toado.TinderChat.Chat.ChatObject;
import com.app.toado.fragments.sharedItems.SharedMediaObject;
import com.app.toado.fragments.sharedItems.ShowSharedMedia;
import com.app.toado.helper.CircleTransform;

import com.bumptech.glide.Glide;

import java.util.List;


public class SharedMediaAdapter extends RecyclerView.Adapter<SharedMediaAdapter.MyViewHolder> {

    private final Context context;
    private List<SharedMediaObject> chatList;

    String chatId;
    String matchId;


    public SharedMediaAdapter(List<SharedMediaObject> matchesList, Context context,String matchId,String chatId) {
        this.chatList = matchesList;
        this.context = context;
        this.chatId=chatId;
        this.matchId=matchId;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgProfile = (ImageView) itemView.findViewById(R.id.shared_media_row_image);

        }

    }

    @Override
    public SharedMediaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shared_media, parent, false);
        return new SharedMediaAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {



        final String timestamp=chatList.get(position).getTimestamp();
        Glide.with(context).load(chatList.get(position).getMessage()).error(R.drawable.nouser).into(holder.imgProfile);


        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context, ShowSharedMedia.class);
                i.putExtra("image",chatList.get(position).getMessage());
                i.putExtra("user",chatList.get(position).getCurrentUser());
                i.putExtra("timestamp",chatList.get(holder.getAdapterPosition()).getTimestamp().toString());
                i.putExtra("matchId",matchId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}
