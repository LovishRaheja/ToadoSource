package com.app.toado.activity.GroupChat.GroupDetailRecycler;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * Created by Silent Knight on 20-06-2018.
 */

public class GroupDetailAdapter extends RecyclerView.Adapter<GroupDetailAdapter.MyContactListViewHolder> {
    private List<GroupDetailObject> matchesList;
    private Context context;
    String groupId;
    public GroupDetailAdapter(List<GroupDetailObject> matchesList, Context context,String groupId) {

        this.matchesList = matchesList;
        this.context = context;
        this.groupId=groupId;
    }



    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name,admin;

        public MyContactListViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.MatchName);
            image=(ImageView)itemView.findViewById(R.id.MatchImage);
            admin=(TextView)itemView.findViewById(R.id.admin);
        }
    }

    @Override
    public GroupDetailAdapter.MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_detail, null, false);
        GroupDetailAdapter.MyContactListViewHolder holder = new GroupDetailAdapter.MyContactListViewHolder(v);
        return holder;


    }

    @Override
    public void onBindViewHolder(final GroupDetailAdapter.MyContactListViewHolder holder, final int position) {



        holder.name.setText(matchesList.get(position).getName());
        FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue().toString().equals(matchesList.get(position).getUserId()))
                {
                    holder.admin.setVisibility(View.VISIBLE);
                }
                else{
                    holder.admin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).dontAnimate().transform(new CircleTransform(context)).into(holder.image);

    }


    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }

}
