package com.app.toado.activity.BlockedUsers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.TinderChat.Matches.MatchesViewHolders;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Silent Knight on 23-05-2018.
 */

public class BlockedAdapter extends RecyclerView.Adapter<BlockedViewHolders>{
    private List<BlockedObject> matchesList;
    private Context context;



    public BlockedAdapter(List<BlockedObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public BlockedViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blocked, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        BlockedViewHolders rcv = new BlockedViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final BlockedViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());

        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).dontAnimate()
                    .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.mMatchImage);
        }




    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }

}
