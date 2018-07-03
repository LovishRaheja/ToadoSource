package com.app.toado.fragments.sharedItems;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Silent Knight on 06-06-2018.
 */

public class SharedLinkAdapter  extends RecyclerView.Adapter<SharedLinkAdapter.MyViewHolder> {


    private final Context context;
    private List<SharedLinksObject> chatList;

    String chatId;
    String matchId;


    public SharedLinkAdapter(List<SharedLinksObject> matchesList, Context context,String matchId,String chatId) {
        this.chatList = matchesList;
        this.context = context;
        this.chatId=chatId;
        this.matchId=matchId;


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.docName);


        }

    }

    @Override
    public SharedLinkAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_links, parent, false);
        return new SharedLinkAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SharedLinkAdapter.MyViewHolder holder, final int position) {



        holder.name.setText(chatList.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

}
