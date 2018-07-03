package com.app.toado.fragments.groupchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.activity.GroupChat.GroupChatActivity;
import com.app.toado.activity.Invite.SelectUser;
import com.app.toado.activity.Invite.SelectUserAdapter;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Silent Knight on 18-06-2018.
 */

public class GroupAdapterFragment extends RecyclerView.Adapter<GroupAdapterFragment.MyContactListViewHolder> {


    private List<GroupObject> matchesList;
    private Context context;
    public GroupAdapterFragment(List<GroupObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;

    }
    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        TextView groupName;
        RelativeLayout groupLayout;
        ImageView image;
        TextView recentMsg,timestamp;


        public MyContactListViewHolder(View itemView) {
            super(itemView);
            groupName=(TextView)itemView.findViewById(R.id.groupName);
            groupLayout=(RelativeLayout)itemView.findViewById(R.id.groupLayout);
            image=(ImageView)itemView.findViewById(R.id.image);
            recentMsg=(TextView)itemView.findViewById(R.id.recentMsg);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
        }
    }

    @Override
    public GroupAdapterFragment.MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        GroupAdapterFragment.MyContactListViewHolder holder = new GroupAdapterFragment.MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupAdapterFragment.MyContactListViewHolder holder, final int position) {

        holder.groupName.setText(matchesList.get(position).getName());

        FirebaseDatabase.getInstance().getReference().child("Chat").child(matchesList.get(position).getGroupId()).getRef().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists())
                {

                    String recent=dataSnapshot.child("text").getValue().toString();

                    if(recent.contains("docx")| recent.contains("pdf")|recent.contains("doc")){
                        holder.recentMsg.setText("Document");
                    }
                    else if(recent.contains("chatImages")){
                        holder.recentMsg.setText("Image");
                    }
                    else if(recent.startsWith("Latitude"))
                    {
                        holder.recentMsg.setText("Location");
                    }
                    else if(recent.contains("Videos")){
                        holder.recentMsg.setText("Video");
                    }
                    else if(recent.contains("mp3"))
                    {
                        holder.recentMsg.setText("Music");
                    }
                    else if(recent.startsWith("Name"))
                    {
                        holder.recentMsg.setText("Contact");
                    }
                    else
                        holder.recentMsg.setText(recent);
                    String timeString=dataSnapshot.child("timestamp").getValue().toString();
                    String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(Long.valueOf(timeString)));
                    String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
                    if (today.equals(time)) {
                        holder.timestamp.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));
                    } else {
                        holder.timestamp.setText(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(timeString))));
                    }

                    //  holder.recentMsg.setText();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, GroupChatActivity.class);
                i.putExtra("groupId",matchesList.get(position).getGroupId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        Glide.with(context).load(matchesList.get(position).getImage()).dontAnimate()
                .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }



}
