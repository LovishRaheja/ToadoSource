package com.app.toado.activity.Notifications;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.toado.R;
import com.app.toado.RivChat.data.FriendDB;
import com.app.toado.RivChat.data.StaticConfig;
import com.app.toado.RivChat.model.Friend;
import com.app.toado.RivChat.model.ListFriend;
import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;


/**
 * Created by Silent Knight on 27-02-2018.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    ArrayList<RequestDetails> list = new ArrayList<>();
    private Activity context;
    private Context contx;
   UserSession us;
    private ListFriend dataListFriend = null;
    private ArrayList<String> listFriendID = null;
    LovelyProgressDialog dialogWait;


    public RequestAdapter(ArrayList<RequestDetails> list, Activity context, Context contx) {
        this.context = context;
        this.list = list;
      this.contx=contx;


    }

    public RequestAdapter() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,desc;
        Button accept,reject;
        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvtitle);
            desc = (TextView) itemView.findViewById(R.id.desc);
            accept=(Button)itemView.findViewById(R.id.accept);
            reject=(Button)itemView.findViewById(R.id.reject);
            profilePic=(ImageView)itemView.findViewById(R.id.profilePic);
        }

    }

    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);
        dialogWait = new LovelyProgressDialog(context);

        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.MyViewHolder holder, int position) {
         //String topic= list.get(position);
        //holder.title.setText(topic);
        final RequestDetails phn = list.get(position);
        holder.title.setText(phn.getOtherusrname().toString());
        final String key=phn.getOtherusrkey().toString();
        us = new UserSession(context);


        Toast.makeText(context, us.getUserKey(), Toast.LENGTH_SHORT).show();

        Glide.with(context).load(phn.getProfpicurl()).dontAnimate()
                .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.profilePic);


        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBREF_USER_PROFILES.child(us.getUserKey()).child("Connections").child(key).child("Status").setValue("accepted");
                DBREF_USER_PROFILES.child(key).child("Friends").child(us.getUserKey()).child("Status").setValue("accepted");
                DBREF_USER_PROFILES.child(key).child("Friends").child(us.getUserKey()).child("Name").setValue(us.getUsername().toString());
                DBREF_USER_PROFILES.child(key).child("Friends").child(us.getUserKey()).child("ProfilePicUrl").setValue(us.getUserPic()).toString();
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.desc.setText(" is now connected with you");


                String chatKey = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                DBREF_USER_PROFILES.child(key).child("connections").child("matches").child(us.getUserKey()).child("ChatId").setValue(chatKey);
                DBREF_USER_PROFILES.child(us.getUserKey()).child("connections").child("matches").child(key).child("ChatId").setValue(chatKey);

            /*8    HashMap userMap = new HashMap();
                userMap.get(key);
                Friend user = new Friend();
                user.name = (String) userMap.get("name");

                user.avata = (String) userMap.get("profpicurl");
                user.id = key;
                user.idRoom = key.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + key).hashCode() + "" : "" + (key + StaticConfig.UID).hashCode();

                checkBeforAddFriend(key, user);*/






            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkBeforAddFriend(final String idFriend, Friend userInfo) {

            addFriend(idFriend, true);
            listFriendID.add(idFriend);
            dataListFriend.getListFriend().add(userInfo);
            FriendDB.getInstance(context).addFriend(userInfo);


    }

    private void addFriend(final String idFriend, boolean isIdFriend) {

                FirebaseDatabase.getInstance().getReference().child("friend/" + us.getUserKey()).push().setValue(idFriend)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addFriend(idFriend, false);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(context)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });

                FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(us.getUserKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addFriend(null, false);
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogWait.dismiss();
                                new LovelyInfoDialog(context)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setTitle("False")
                                        .setMessage("False to add friend success")
                                        .show();
                            }
                        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
