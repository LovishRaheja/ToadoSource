package com.app.toado.TinderChat.ForwardChat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;

import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.TinderChat.Matches.MatchesViewHolders;
import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 22-05-2018.
 */

public class ForwardChatAdapter extends RecyclerView.Adapter<ForwardChatAdapter.MyViewHolder> {
    private List<ForwardChatObject> matchesList;
    private Context context;
    UserSession session;
    String msg;
    DatabaseReference mDatabaseUser, mDatabaseChat;



    public ForwardChatAdapter(List<ForwardChatObject> matchesList, Context context,String msg){
        this.matchesList = matchesList;
        this.context = context;
        this.msg=msg;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        public TextView mMatchId, mMatchName,myId,msg;
        public ImageView mMatchImage, showonline;
        DatabaseReference mDatabaseUser, mDatabaseChat;
        RelativeLayout forwardLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.docName);
            mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
            mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
            forwardLayout=(RelativeLayout)itemView.findViewById(R.id.forwardLayout);



            mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
            myId=(TextView)itemView.findViewById(R.id.myId);
            msg=(TextView)itemView.findViewById(R.id.msg);




        }

    }

    private void getChatId(){



    }
    @Override
    public ForwardChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forward, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ForwardChatAdapter.MyViewHolder rcv = new ForwardChatAdapter.MyViewHolder(layoutView);
        session=new UserSession(context);


        return rcv;
    }

    @Override
    public void onBindViewHolder(final ForwardChatAdapter.MyViewHolder holder, final int position) {

        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.myId.setText(session.getUserKey());
        holder.msg.setText(msg);
      //  mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(matchesList.get(position).getUserId()).child("ChatId");
       // mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        holder.forwardLayout.setTag(position);
        holder.forwardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(matchesList.get(holder.getAdapterPosition()).getUserId()).child("ChatId");
                mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

                mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String chatId = dataSnapshot.getValue().toString();
                            mDatabaseChat = mDatabaseChat.child(chatId);

                            DatabaseReference newMessageDb = mDatabaseChat.push();

                            Map newMessage = new HashMap();
                            newMessage.put("createdByUser",session.getUserKey());
                            newMessage.put("text", msg);
                            newMessage.put("timestamp",System.currentTimeMillis());

                            newMessageDb.setValue(newMessage);

                            Intent i=new Intent(context, ChatActivity.class);
                            i.putExtra("matchId",matchesList.get(position).getUserId());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });


        holder.mMatchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).dontAnimate()
                    .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.mMatchImage);
        }




    }



 /**   private void sendMessage() {



        DatabaseReference newMessageDb = mDatabaseChat.push();

        Map newMessage = new HashMap();
        newMessage.put("createdByUser",msg);
        newMessage.put("text", msg);
        newMessage.put("timestamp",System.currentTimeMillis());

        newMessageDb.setValue(newMessage);


    }
*/

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}