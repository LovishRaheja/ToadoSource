package com.app.toado.TinderChat.Matches;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

/**
 * Created by Silent Knight on 05-01-2018.
 */

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders>{
    private List<MatchesObject> matchesList;
    private Context context;



    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final MatchesViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());

        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).dontAnimate()
                    .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.mMatchImage);
        }



        FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchesList.get(position).getUserId()).child("online").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (dataSnapshot.exists()){

                    if (connected) {
                        holder.showonline.setImageResource(R.drawable.show_online);

                    }
                    /**  if(dataSnapshot.getValue().equals("true"))
                     {
                     holder.showOnline.setImageResource(R.drawable.show_online);
                     // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                     }*/else{
                        holder.showonline.setImageResource(R.drawable.not_online);
                    }


                    //  getRecentMsg(chatId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}