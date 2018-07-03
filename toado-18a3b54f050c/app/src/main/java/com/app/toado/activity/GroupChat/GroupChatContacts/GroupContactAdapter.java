package com.app.toado.activity.GroupChat.GroupChatContacts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatContactAdapter;
import com.app.toado.TinderChat.Chat.ChatContacts.SelectContact;
import com.app.toado.activity.GroupChat.GroupChatActivity;
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

/**
 * Created by Silent Knight on 03-07-2018.
 */

public class GroupContactAdapter extends RecyclerView.Adapter<GroupContactAdapter.MyContactListViewHolder> {

    List<SelectGroupContact> mainInfo;
    private ArrayList<SelectGroupContact> arraylist;
    Context context;
    String list="";
    String groupId;
    private String userkey;
    private UserSession session;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    String chatId;



    public GroupContactAdapter(Context context, List<SelectGroupContact> mainInfo,String groupId) {
        this.mainInfo = mainInfo;
        this.context = context;
        this.arraylist = new ArrayList<SelectGroupContact>();
        this.arraylist.addAll(mainInfo);
        this.groupId=groupId;
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
            mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(groupId);

        }
    }

    @Override
    public GroupContactAdapter.MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_contact, parent, false);
        GroupContactAdapter.MyContactListViewHolder holder = new GroupContactAdapter.MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(GroupContactAdapter.MyContactListViewHolder holder, final int position) {
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

                Intent i=new Intent(context, GroupChatActivity.class);
                //i.putExtra("name",contact.name);
                //i.putExtra("number",contact.numbers.get(0).number);
                i.putExtra("groupId",groupId);
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
            for (SelectGroupContact wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mainInfo.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}

