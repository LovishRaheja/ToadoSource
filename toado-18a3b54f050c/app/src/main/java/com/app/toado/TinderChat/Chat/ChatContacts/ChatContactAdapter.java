package com.app.toado.TinderChat.Chat.ChatContacts;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.activity.Invite.SelectUser;
import com.app.toado.activity.Invite.SelectUserAdapter;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 25-05-2018.
 */

public class ChatContactAdapter extends RecyclerView.Adapter<ChatContactAdapter.MyContactListViewHolder> {

    List<SelectContact> mainInfo;
    private ArrayList<SelectContact> arraylist;
    Context context;
    String list="";
    String otheruserkey;
    private String userkey;
    private UserSession session;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    String chatId;



    public ChatContactAdapter(Context context, List<SelectContact> mainInfo,String otheruserkey) {
        this.mainInfo = mainInfo;
        this.context = context;
        this.arraylist = new ArrayList<SelectContact>();
        this.arraylist.addAll(mainInfo);
        this.otheruserkey=otheruserkey;
    }

    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewUserImage;
        TextView textViewShowName;
        TextView textViewPhoneNumber;
        CheckBox checkBoxSelectItem;
        LinearLayout mainLayout;

        public MyContactListViewHolder(View itemView) {
            super(itemView);

            textViewShowName = (TextView) itemView.findViewById(R.id.name);
            textViewPhoneNumber = (TextView) itemView.findViewById(R.id.no);
            mainLayout=(LinearLayout)itemView.findViewById(R.id.mainLayout);
            session = new UserSession(context);
            mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
            mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(otheruserkey).child("ChatId");
            mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        chatId = dataSnapshot.getValue().toString();
                        mDatabaseChat = mDatabaseChat.child(chatId);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public ChatContactAdapter.MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_contact, parent, false);
        ChatContactAdapter.MyContactListViewHolder holder = new ChatContactAdapter.MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatContactAdapter.MyContactListViewHolder holder, final int position) {
        holder.textViewShowName.setText(mainInfo.get(position).getName());
        holder.textViewPhoneNumber.setText(mainInfo.get(position).getPhone());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg="Name"+mainInfo.get(position).getName()+"Number"+mainInfo.get(position).getPhone();

                if (msg.length() > 0) {

                    DatabaseReference newMessageDb = mDatabaseChat.push();

                    Map newMessage = new HashMap();
                    newMessage.put("createdByUser", session.getUserKey());
                    newMessage.put("text", msg.toString());
                    newMessage.put("timestamp",System.currentTimeMillis());

                    newMessageDb.setValue(newMessage);
                }

                Intent i=new Intent(context, ChatActivity.class);
                //i.putExtra("name",contact.name);
                //i.putExtra("number",contact.numbers.get(0).number);
                i.putExtra("matchId",otheruserkey);
                context.startActivity(i);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mainInfo.size();
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mainInfo.clear();
        if (charText.length() == 0) {
            mainInfo.addAll(arraylist);
        } else {
            for (SelectContact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mainInfo.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
