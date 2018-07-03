package com.app.toado.TinderChat.ForwardChat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 22-05-2018.
 */

public class ForwardChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId, mMatchName,myId,msg;
    public ImageView mMatchImage, showonline;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    String ms;


    public ForwardChatViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);



        myId=(TextView)itemView.findViewById(R.id.myId);
        msg=(TextView)itemView.findViewById(R.id.msg);
        mDatabaseUser =DBREF_USER_PROFILES.child(myId.getText().toString()).child("connections").child("matches").child(mMatchId.getText().toString()).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();


    }
    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View view) {


        getChatId();


        sendMessage();

        /** Intent intent = new Intent(view.getContext(), ChatActivity.class);
         Bundle b = new Bundle();
         b.putString("matchId", mMatchId.getText().toString());
         b.putString("name", mMatchName.getText().toString());

         intent.putExtras(b);
         view.getContext().startActivity(intent);*/
    }


    private void sendMessage() {



            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", myId.getText().toString());
            newMessage.put("text", msg);
            newMessage.put("timestamp",System.currentTimeMillis());

            newMessageDb.setValue(newMessage);


    }



}