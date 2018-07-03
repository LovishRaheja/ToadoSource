package com.app.toado.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.app.toado.R;
import com.app.toado.helper.CircleTransform;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.app.toado.activity.userprofile.UserProfileAct;
import com.app.toado.model.DistanceUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;


public class DistanceUserAdapter extends RecyclerView.Adapter<DistanceUserAdapter.MyViewHolder> {

    String imgurl;
    ArrayList<DistanceUser> list = new ArrayList<>();
    private Context context;
    private UserSettingsSharedPref userSettingsSharedPref;
    String mykey;


    public DistanceUserAdapter(ArrayList<DistanceUser> list, Context context) {
        this.list = list;
        userSettingsSharedPref = new UserSettingsSharedPref(context);
        this.context = context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;
        RelativeLayout lay;
        ImageView userImage;
        ImageView online;
        public MyViewHolder(View itemView) {
            super(itemView);
           // mContext = getApplicationContext();
           // mResources = getResources();
            //mUserPic=(ImageView)itemView.findViewById(R.id.userPic);

            name = (TextView) itemView.findViewById(R.id.name);
            distance = (TextView) itemView.findViewById(R.id.distance);
            lay=(RelativeLayout) itemView.findViewById(R.id.distlay);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
            online=(ImageView)itemView.findViewById(R.id.showOnline);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_distance_user_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DistanceUserAdapter.MyViewHolder holder, final int position) {
        DistanceUser distanceUser = list.get(position);
        holder.name.setText(distanceUser.getName());
        holder.distance.setText(distanceUser.getDist() + " " + userSettingsSharedPref.getisDistancePrefSet() + " away from you");
//        System.out.println(distanceUser.getName() + "holder vals" + distanceUser.getKey());
        holder.distance.setText(distanceUser.getDist() + " " + userSettingsSharedPref.getUnit().substring(0,1).toUpperCase()+userSettingsSharedPref.getUnit().substring(1).toLowerCase());



        Glide.with(context).load(distanceUser.getProfilePicUrl()).bitmapTransform(new RoundedCornersTransformation(getApplicationContext(),15,15)).error(R.drawable.person).into(holder.userImage);
      //  String imgurl = distanceUser.getProfilePic();
        UserSession us = new UserSession(context);

        FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(distanceUser.getKey()).child("online").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (dataSnapshot.exists()){

                    if (connected) {
                        holder.online.setImageResource(R.drawable.show_online);

                    }
                    /**  if(dataSnapshot.getValue().equals("true"))
                     {
                     holder.showOnline.setImageResource(R.drawable.show_online);
                     // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                     }*/else{
                        holder.online.setImageResource(R.drawable.not_online);
                    }


                    //  getRecentMsg(chatId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    /**    FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("online").getValue().toString().equals("true"))
                    {
                        holder.online.setBackgroundResource(R.drawable.show_online);
                      //  holder.online.setColorFilter(ContextCompat.getColor(context, R.color.light_green));

                    }
                    else{
                        holder.online.setBackgroundResource(R.drawable.not_online);

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
*/


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
                Intent in = new Intent(context, UserProfileAct.class);
                in.putExtra("profiletype", "otherprofile");
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
