package com.app.toado.TinderChat.StarredMessages;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.ForwardChat.ForwardChat;
import com.app.toado.adapter.SharedMediaAdapter;
import com.app.toado.fragments.sharedItems.SharedMediaObject;
import com.app.toado.fragments.sharedItems.ShowSharedMedia;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.data.DataHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Silent Knight on 11-06-2018.
 */

public class StarMessageAdapter  extends RecyclerView.Adapter<StarMessageAdapter.MyViewHolder> {
    private final Context context;
    private List<StarObject> chatList;

    String mykey;
    String matchId;


    public StarMessageAdapter(List<StarObject> matchesList, Context context,String mykey) {
        this.chatList = matchesList;
        this.context = context;
        this.mykey=mykey;



    }
    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView text,sender,reciever,textTime;
        ImageView userImage;
        LinearLayout starLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.text);
            sender=(TextView)itemView.findViewById(R.id.sender);
            reciever=(TextView)itemView.findViewById(R.id.reciever);
            textTime=(TextView)itemView.findViewById(R.id.textTime);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
            starLayout=(LinearLayout)itemView.findViewById(R.id.starLayout);
        }

    }


    @Override
    public StarMessageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_star_messages, parent, false);
        return new StarMessageAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final StarMessageAdapter.MyViewHolder holder, final int position) {

        holder.text.setText(chatList.get(position).getMessage());
        String timeString=chatList.get(position).getTimestamp().toString();
        //holder.conTime.setText(timeString);
        String a1=new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp())));
        String today = new SimpleDateFormat("MMM d").format(new Date(System.currentTimeMillis()));
        if (today.equals(a1)) {

            holder.textTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString)))+", Today");
        }
        else{
            holder.textTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString)))+", "+new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp()))));
        }


        if(chatList.get(position).getSender().equals(mykey))
        {
            holder.sender.setText("You");
            DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.reciever.setText(dataSnapshot.child(chatList.get(position).getReciever()).child("name").getValue().toString());
                    Glide.with(context).load(dataSnapshot.child(chatList.get(position).getSender()).child("profpicurl").getValue().toString()).dontAnimate()
                            .transform(new CircleTransform(context)).into(holder.userImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    holder.sender.setText(dataSnapshot.child(chatList.get(position).getSender()).child("name").getValue().toString());
                    Glide.with(context).load(dataSnapshot.child(chatList.get(position).getSender()).child("profpicurl").getValue().toString()).dontAnimate()
                            .transform(new CircleTransform(context)).error(R.drawable.nouser).into(holder.userImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.reciever.setText("You");
        }


        holder.starLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatList.get(position).getSender().equals(mykey))
                {
                    Intent i=new Intent(context,ChatActivity.class);
                    i.putExtra("matchId",chatList.get(position).getReciever());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                else{
                    Intent i=new Intent(context,ChatActivity.class);
                    i.putExtra("matchId",chatList.get(position).getSender());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }

            }
        });

        holder.starLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select any option");

// add a list
                String[] animals = {"Unstar","Delete", "Forward"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:

                                Query unstarQuery =DBREF_USER_PROFILES.child(mykey).child("Starred");
                                unstarQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                                Toast.makeText(context, String.valueOf(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                break;

                            case 2:
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

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}
