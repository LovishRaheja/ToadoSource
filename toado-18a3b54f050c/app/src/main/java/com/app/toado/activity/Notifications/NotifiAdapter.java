package com.app.toado.activity.Notifications;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;

import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;


import java.util.ArrayList;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 28-02-2018.
 */

public class NotifiAdapter extends RecyclerView.Adapter<NotifiAdapter.MyViewHolder> {
    ArrayList<NotifiDetails> list = new ArrayList<>();
    private Activity context;
    private Context contx;
    UserSession us;


    public NotifiAdapter(ArrayList<NotifiDetails> list, Activity context, Context contx) {
        this.context = context;
        this.list = list;
        this.contx=contx;


    }

    public NotifiAdapter() {
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvtitle);

            profilePic=(ImageView)itemView.findViewById(R.id.profilePic);
        }

    }

    @Override
    public NotifiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);

        return new NotifiAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotifiAdapter.MyViewHolder holder, int position) {


        final NotifiDetails phn = list.get(position);
        holder.title.setText(phn.getOtherusrname().toString());
       // Toast.makeText(context, phn.getProfpicurl(), Toast.LENGTH_SHORT).show();

       /**DatabaseReference retreiveDetails = DBREF_USER_PROFILES.child(phn.getOtherusrkey());
        retreiveDetails.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
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

        Glide.with(context).load(phn.getProfpicurl()).dontAnimate()
                .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.profilePic);

        us = new UserSession(context);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
