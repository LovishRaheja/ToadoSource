package com.app.toado.TinderChat.ChatFragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.TinderChat.Matches.MatchesViewHolders;
import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 08-05-2018.
 */

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentViewHolders>{
    private List<ChatFragmentObject> matchesList;
    private Context context;
    UserSession session;
    String chatId;



    public ChatFragmentAdapter(List<ChatFragmentObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public ChatFragmentViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_tinder, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatFragmentViewHolders rcv = new ChatFragmentViewHolders(layoutView);
        session = new UserSession(context);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final ChatFragmentViewHolders holder, int position) {

        holder.mMatchId.setText(matchesList.get(position).getUserId());

        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).dontAnimate()
                    .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.mMatchImage);
        }

       // Toast.makeText(context, DBREF_USER_PROFILES.child("Usersession").child(matchesList.get(position).getUserId()).child("online")., Toast.LENGTH_SHORT).show();


        FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchesList.get(position).getUserId()).child("online").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (dataSnapshot.exists()){

                    if (connected) {
                        holder.showOnline.setImageResource(R.drawable.show_online);

                    }
                 /**  if(dataSnapshot.getValue().equals("true"))
                   {
                       holder.showOnline.setImageResource(R.drawable.show_online);
                      // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                   }*/else{
                       holder.showOnline.setImageResource(R.drawable.not_online);
                   }


                    //  getRecentMsg(chatId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(matchesList.get(position).getUserId()).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                     chatId = dataSnapshot.getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).getRef().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists())
                            {

                                String recent=dataSnapshot.child("text").getValue().toString();

                                if(recent.contains("docx")|recent.contains("txt")| recent.contains("pdf")|recent.contains("doc")){
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

                 //  getRecentMsg(chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


       /** FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId).getRef().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(context,"done", Toast.LENGTH_SHORT).show();

                  //  holder.recentMsg.setText();
                }
                else{
                    Toast.makeText(context,"no", Toast.LENGTH_SHORT).show();
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
        });*/
    }


    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}