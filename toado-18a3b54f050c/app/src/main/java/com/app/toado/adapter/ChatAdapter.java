package com.app.toado.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.R;
import com.app.toado.activity.userprofile.UserProfileAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.model.DistanceUser;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USERS_CHATS;

/**
 * Created by Silent Knight on 09-03-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    String imgurl;
    ArrayList<DistanceUser> list = new ArrayList<>();
    private Context context;
    private UserSettingsSharedPref userSettingsSharedPref;

    public ChatAdapter(ArrayList<DistanceUser> list, Context context) {
        this.list = list;
        userSettingsSharedPref = new UserSettingsSharedPref(context);
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance,recentMsg,timestamp;
        RelativeLayout lay;
        ImageView userImage,recentImage;
        ImageView showOnline;
        public MyViewHolder(View itemView) {
            super(itemView);
            // mContext = getApplicationContext();
            // mResources = getResources();
            //mUserPic=(ImageView)itemView.findViewById(R.id.userPic);

            name = (TextView) itemView.findViewById(R.id.name);

            lay=(RelativeLayout) itemView.findViewById(R.id.distlay);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
            recentMsg=(TextView)itemView.findViewById(R.id.recentMsg);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
            recentImage=(ImageView)itemView.findViewById(R.id.recentImage);
            showOnline=(ImageView)itemView.findViewById(R.id.showOnline);
        }
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_firebase_chat_list, parent, false);



        return new ChatAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatAdapter.MyViewHolder holder, final int position) {
        final DistanceUser distanceUser = list.get(position);
        holder.name.setText(distanceUser.getName());

//        System.out.println(distanceUser.getName() + "holder vals" + distanceUser.getKey());


        Glide.with(context).load(distanceUser.getProfilePicUrl()).dontAnimate()
                .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.userImage);
        //  String imgurl = distanceUser.getProfilePic();
        UserSession us = new UserSession(context);


        FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("online").getValue().toString().equals("true"))
                    {
                        holder.showOnline.setBackgroundResource(R.drawable.show_online);
                        //  holder.online.setColorFilter(ContextCompat.getColor(context, R.color.light_green));

                    }
                    else{
                        holder.showOnline.setBackgroundResource(R.drawable.not_online);

                    }
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


        FirebaseDatabase.getInstance().getReference().child("message").child("null").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                s=dataSnapshot.child("text").getValue().toString();

                if(s.startsWith("/file")){

                    holder.recentMsg.setText(s.substring(s.lastIndexOf("/")+1));
                    holder.recentImage.setVisibility(View.VISIBLE);
                }
                else if(s.startsWith("Name"))
                {
                    holder.recentMsg.setText("contact");
                    holder.recentImage.setVisibility(View.VISIBLE);


                }
                else if(s.startsWith("https://firebasestorage"))
                {
                    holder.recentMsg.setText("Image");
                    holder.recentImage.setVisibility(View.VISIBLE);
                }
                else {
                    holder.recentMsg.setText(s);
                }


                String timeString=dataSnapshot.child("timestamp").getValue().toString();

               String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(Long.valueOf(timeString)));
                String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
                if (today.equals(time)) {
                    holder.timestamp.setText(new SimpleDateFormat("HH:mm").format(new Date(Long.valueOf(timeString))));
                } else {
                     holder.timestamp.setText(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(timeString))));
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

        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                System.out.println(list.size() + " posclicked " + pos + "distance user adapter clicked item");
                Intent in = new Intent(context, FirebaseChat.class);
                in.putExtra("otherusername",distanceUser.getName());
                in.putExtra("otheruserkey",distanceUser.getKey());

                in.putExtra("profiletype", distanceUser.getProfilePicUrl());
                in.putExtra("keyval", list.get(pos).getKey());
                System.out.println("distance");
                in.putExtra("distance", String.valueOf(list.get(pos).getDist()));
                context.startActivity(in);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }





}
