package com.app.toado.FirebaseChat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.adapter.ToadoAdapter;
import com.app.toado.helper.CircleTransform;
import com.app.toado.model.DistanceUser;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Silent Knight on 11-04-2018.
 */

public class ForwardAdapter extends RecyclerView.Adapter<ForwardAdapter.MyViewHolder> {

    String imgurl;
    ArrayList<DistanceUser> list = new ArrayList<>();
    private Context context;
    private UserSettingsSharedPref userSettingsSharedPref;

    public ForwardAdapter(ArrayList<DistanceUser> list, Context context,String msg) {
        this.list = list;
        userSettingsSharedPref = new UserSettingsSharedPref(context);
        this.context = context;
        this.imgurl=msg;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;
        RelativeLayout lay;
        ImageView userImage;
        public MyViewHolder(View itemView) {
            super(itemView);
            // mContext = getApplicationContext();
            // mResources = getResources();
            //mUserPic=(ImageView)itemView.findViewById(R.id.userPic);

            name = (TextView) itemView.findViewById(R.id.name);

            lay=(RelativeLayout) itemView.findViewById(R.id.distlay);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
        }
    }

    @Override
    public ForwardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_forward_chat, parent, false);



        return new ForwardAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ForwardAdapter.MyViewHolder holder, final int position) {
        final DistanceUser distanceUser = list.get(position);
        holder.name.setText(distanceUser.getName());

//        System.out.println(distanceUser.getName() + "holder vals" + distanceUser.getKey());


        Glide.with(context).load(distanceUser.getProfilePicUrl()).dontAnimate()
                .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.userImage);
        //  String imgurl = distanceUser.getProfilePic();
        UserSession us = new UserSession(context);
        //usrkey = us.getUserKey();
        /** DBREF_USER_PROFILES.child(us.getUserKey()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
        System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
        User u = User.parse(dataSnapshot);
        imgurl = u.getProfpicurl();
        Glide.with(context).load(imgurl).into(holder.userImage);

        } else
        System.out.println("no snapshot exists userprof act");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
        });
         */

        // Glide.with(context).load(imgurl).error(R.drawable.lovish).into(holder.userImage);
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
