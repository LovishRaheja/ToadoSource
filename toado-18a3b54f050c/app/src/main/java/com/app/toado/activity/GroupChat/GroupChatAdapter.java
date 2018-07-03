package com.app.toado.activity.GroupChat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatContacts.ShowContact;
import com.app.toado.TinderChat.Chat.ChatObject;
import com.app.toado.TinderChat.Chat.ChatViewHolders;
import com.app.toado.TinderChat.ForwardChat.ForwardChat;
import com.app.toado.fragments.groupchat.GroupAdapterFragment;
import com.app.toado.fragments.groupchat.GroupObject;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Silent Knight on 18-06-2018.
 */

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MyContactListViewHolder>  {
    private List<GroupChatObject> chatList;
    private ArrayList<GroupChatObject> arraylist;
    private Context context;
    String chatId,mykey;


    String chId;
    public GroupChatAdapter(List<GroupChatObject> matchesList, Context context,String chatId,String mykey) {
        this.chatList = matchesList;
        this.context = context;
        this.chatId=chatId;

        this.mykey=mykey;
        this.arraylist = new ArrayList<GroupChatObject>();
        this.arraylist.addAll(chatList);
    }

    public class MyContactListViewHolder extends RecyclerView.ViewHolder {

        TextView name;


        ImageView tick,locTick,docTick,conTick;

        public TextView mMessage,textTime;
        public LinearLayout mContainer;


        LinearLayout locLayout;
        ImageView chatMap;
        TextView locTime;
        LinearLayout locLinear;


        ImageView docImage;
        TextView docText;
        LinearLayout docLinear,docLayout;
        TextView docTime;


        LinearLayout conLayout,conLinear;
        ImageView conImage;
        TextView conName,conNumber,conTime;


        public MyContactListViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);

            mMessage = itemView.findViewById(R.id.message);
            mContainer = itemView.findViewById(R.id.container);
            textTime=itemView.findViewById(R.id.textTime);
            tick=(ImageView)itemView.findViewById(R.id.tick);


            locLayout=(LinearLayout)itemView.findViewById(R.id.locLayout);
            chatMap=(ImageView)itemView.findViewById(R.id.chatMap);
            locTime=(TextView)itemView.findViewById(R.id.locTime);
            locLinear=(LinearLayout)itemView.findViewById(R.id.locLinear);
            locTick=(ImageView)itemView.findViewById(R.id.locTick);


            docImage=(ImageView)itemView.findViewById(R.id.docImage);
            docText=(TextView)itemView.findViewById(R.id.docName);
            docLinear=(LinearLayout)itemView.findViewById(R.id.docLinear);
            docLayout=(LinearLayout)itemView.findViewById(R.id.docLayout);
            docTime=(TextView)itemView.findViewById(R.id.docTime);
            docTick=(ImageView)itemView.findViewById(R.id.docTick);


            conLayout=(LinearLayout)itemView.findViewById(R.id.conLayout);
            conLinear=(LinearLayout)itemView.findViewById(R.id.conLinear);
            conImage=(ImageView)itemView.findViewById(R.id.conImage);
            conName=(TextView)itemView.findViewById(R.id.conName);
            conNumber=(TextView)itemView.findViewById(R.id.conNumber);
            conTime=(TextView)itemView.findViewById(R.id.conTime);
            conTick=(ImageView)itemView.findViewById(R.id.conTick);

        }
    }

    @Override
    public GroupChatAdapter.MyContactListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_chat, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        GroupChatAdapter.MyContactListViewHolder holder = new GroupChatAdapter.MyContactListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupChatAdapter.MyContactListViewHolder holder, final int position) {

        if(chatList.get(position).getMessage().startsWith("Latitude")){

            holder.mContainer.setVisibility(View.GONE);
            holder.docLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.VISIBLE);
            holder.conLayout.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
            String timeString=chatList.get(position).getTimestamp().toString();
            //holder.conTime.setText(timeString);
            holder.locTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));
            holder.locLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  String latitude=consersation.getListMessageData().get(position).text;
                    // String longitude=consersation.getListMessageData().get(position).text;
                    String latitude=chatList.get(position).getMessage().substring(8,chatList.get(position).getMessage().lastIndexOf("Longitude")-1);
                    String longitude=chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("Longitude")+9);
                    // Toast.makeText(context, latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(latitude), Double.valueOf(longitude));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });


            holder.locLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select any option");

// add a list
                    String[] animals = {"Delete", "Forward"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case 0:
                                    Toast.makeText(context,"Message Not Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:
                                    Intent i=new Intent(getApplicationContext(), ForwardChat.class);
                                    i.putExtra("msg",chatList.get(position).getMessage());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    break;
                            }
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });


            if(isNetworkAvailable())
            {


                holder.locTick.setImageResource( R.drawable.ic_check_black_24dp);


                if(!  holder.locTick.getDrawable().equals(R.drawable.ic_check_black_24dp))
                {
                    holder.locTick.setImageResource( R.drawable.ic_check_black_24dp);
                }
            }
            if(!isNetworkAvailable()){

                holder.locTick.setVisibility(View.GONE);
            }



            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.child("online").getValue().toString().equals("true"))
                        {
                            holder.locTick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);
                        }}

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
        }


        else if(chatList.get(position).getMessage().startsWith("Name")){

            holder.docLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.VISIBLE);
            holder.tick.setVisibility(View.GONE);

            holder.conName.setText(chatList.get(position).getMessage().substring(4,chatList.get(position).getMessage().lastIndexOf("r")-5));
            holder.conNumber.setText(chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("r")+1));
            final String name=chatList.get(position).getMessage().substring(4,chatList.get(position).getMessage().lastIndexOf("r")-5);
            final String number=chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("r")+1);
            String timeString=chatList.get(position).getTimestamp().toString();
            holder.conLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, ShowContact.class);
                    i.putExtra("name",name);
                    i.putExtra("number",number);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
            //holder.conTime.setText(timeString);
            holder.conTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));




            holder.conLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select any option");

// add a list
                    String[] animals = {"Delete", "Forward"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case 0:

                                    Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:




                                    Intent i=new Intent(getApplicationContext(), ForwardChat.class);
                                    i.putExtra("msg",chatList.get(position).getMessage());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    break;
                            }
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });


            if(isNetworkAvailable())
            {


                holder.conTick.setImageResource( R.drawable.ic_check_black_24dp);



            }
            if(!isNetworkAvailable()){

                holder.conTick.setVisibility(View.GONE);
            }
        }



        else if(chatList.get(position).getMessage().contains("docx")| chatList.get(position).getMessage().contains("pdf")|chatList.get(position).getMessage().contains("doc"))
        {

            holder.docLayout.setVisibility(View.VISIBLE);
            holder.mContainer.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
            holder.docText.setText(chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("/")+1));
            holder.docLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File targetFile = new File(chatList.get(position).getMessage().substring(6));
                    Uri targetUri = Uri.fromFile(targetFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(targetUri, "text/plain");
                    context.startActivity(intent);
                }
            });

            String timeString=chatList.get(position).getTimestamp().toString();
            //holder.conTime.setText(timeString);
            holder.docTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));



            holder.docLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select any option");

// add a list
                    String[] animals = {"Delete", "Forward"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case 0:

                                    Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:




                                    Intent i=new Intent(getApplicationContext(), ForwardChat.class);
                                    i.putExtra("msg",chatList.get(position).getMessage());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    break;
                            }
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });


            if(isNetworkAvailable())
            {


                holder.docTick.setImageResource( R.drawable.ic_check_black_24dp);



            }
            if(!isNetworkAvailable()){

                holder.docTick.setVisibility(View.GONE);
            }



        }


        else{

            holder.locLayout.setVisibility(View.GONE);
            holder.docLayout.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.mMessage.setText(chatList.get(position).getMessage());

            String timeString = chatList.get(position).getTimestamp().toString();
            holder.textTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(Long.valueOf(timeString)));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            DBREF_USER_PROFILES.child(chatList.get(position).getCreatedByUser()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.name.setText(dataSnapshot.getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if(isNetworkAvailable())
            {
                holder.tick.setImageResource( R.drawable.ic_check_black_24dp);
            }



        }
        if(chatList.get(position).getCurrentUser())
        {
            holder.mContainer.setGravity(Gravity.RIGHT|Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#404040"));
            holder.mMessage.setBackgroundResource(R.drawable.chatbubbleout);


            holder.locLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.chatMap.setBackgroundResource(R.drawable.chatbubbleout);
            holder.locTime.setTextColor(Color.parseColor("#404040"));
            holder.locLinear.setBackgroundResource(R.drawable.chatbubbleout);



            holder.docLinear.setGravity(Gravity.RIGHT|Gravity.END);
            holder.docText.setTextColor(Color.parseColor("#404040"));
            holder.docLinear.setBackgroundResource(R.drawable.chatbubbleout);

            holder.conLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.conNumber.setTextColor(Color.parseColor("#404040"));
            holder.conName.setTextColor(Color.parseColor("#404040"));
            holder.conLinear.setBackgroundResource(R.drawable.chatbubbleout);

        }
        else{
            holder.name.setVisibility(View.VISIBLE);
            holder.tick.setVisibility(View.GONE);
            holder.textTime.setGravity(Gravity.BOTTOM);
            setMargins(holder.textTime, 0, 50, 0,0 );
            holder.mContainer.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mMessage.setBackgroundResource(R.drawable.chat_bubble);

            holder.locLayout.setGravity((Gravity.START));
            holder.chatMap.setBackgroundResource(R.drawable.chat_bubble);
            holder.locTime.setTextColor(Color.parseColor("#FFFFFF"));
            holder.locLinear.setBackgroundResource(R.drawable.chat_bubble);



            holder.docLayout.setGravity((Gravity.START));
            holder.docText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.docLinear.setBackgroundResource(R.drawable.chat_bubble);

            holder.conLayout.setGravity((Gravity.START));
            holder.conNumber.setTextColor(Color.parseColor("#FFFFFF"));
            holder.conName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.conLinear.setBackgroundResource(R.drawable.chat_bubble);


        }


    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }

}
