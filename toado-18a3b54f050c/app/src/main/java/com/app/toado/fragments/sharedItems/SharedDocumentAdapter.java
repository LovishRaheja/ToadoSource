package com.app.toado.fragments.sharedItems;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.adapter.SharedMediaAdapter;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Silent Knight on 06-06-2018.
 */

public class SharedDocumentAdapter  extends RecyclerView.Adapter<SharedDocumentAdapter.MyViewHolder> {


    private final Context context;
    private List<SharedDocumentObject> chatList;

    String chatId;
    String matchId;


    public SharedDocumentAdapter(List<SharedDocumentObject> matchesList, Context context,String matchId,String chatId) {
        this.chatList = matchesList;
        this.context = context;
        this.chatId=chatId;
        this.matchId=matchId;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name,time;

        public MyViewHolder(View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.docName);
            time=(TextView)itemView.findViewById(R.id.docTime);

        }

    }

    @Override
    public SharedDocumentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_document_media, parent, false);
        return new SharedDocumentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SharedDocumentAdapter.MyViewHolder holder, final int position) {


        final String timestamp=chatList.get(position).getTimestamp();
        String today = new SimpleDateFormat("MMM d").format(new Date(System.currentTimeMillis()));
        if (today.equals(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(timestamp))))) {
            holder.time.setText("Today"+ ", " + new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timestamp))));
        } else
            holder.time.setText(new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(timestamp))) + ", " + new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timestamp))));


        holder.name.setText(chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("/")+1));

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
