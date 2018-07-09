package com.app.toado.TinderChat.Chat;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.Toast;



import com.app.toado.FirebaseChat.OpenChatPhoto;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatContacts.ShowContact;
import com.app.toado.TinderChat.ForwardChat.ForwardChat;
import com.app.toado.activity.Invite.SelectUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.start;
import static android.R.attr.version;
import static android.R.attr.width;
import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Silent Knight on 06-01-2018.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> chatList;
    private ArrayList<ChatObject> arraylist;
    private Context context;
    String chatId;
    String matchId,mykey;


    String chId;


    public ChatAdapter(List<ChatObject> matchesList, Context context,String matchId,String chatId,String mykey){
        this.chatList = matchesList;
        this.context = context;
        this.chatId=chatId;
        this.matchId=matchId;
        this.mykey=mykey;
        this.arraylist = new ArrayList<ChatObject>();
        this.arraylist.addAll(chatList);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutView);
        Intent intent = new Intent("custom-message");
        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
        intent.putExtra("size",String.valueOf(chatList.size()));

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

       // Toast.makeText(getApplicationContext(), String.valueOf(chatList.size()), Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, chatId, Toast.LENGTH_SHORT).show();

        return rcv;
    }


    @Override
    public void onBindViewHolder(final ChatViewHolders holder, final int position) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("size",String.valueOf(chatList.size()));



        if(position==0)
        {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(chatList.get(0).getTimestamp()))));

        }
        else if (position!=0) {
            String zero = new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(chatList.get(position).getTimestamp())));
            String one = new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(chatList.get(position - 1).getTimestamp())));
            if(zero.substring(0,2).equals(one.substring(0,2))) {
                if (Integer.valueOf(zero.substring(zero.length() - 2)) > Integer.valueOf(one.substring(zero.length() - 2))) {

                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(chatList.get(position).getTimestamp()))));
                } else
                    holder.date.setVisibility(View.GONE);

            }

            else{
                holder.date.setText(new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(chatList.get(position).getTimestamp()))));

            }
        }



      /**  for(int i=2;i<=chatList.size();i++)
        {
            if(!new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(i-1).getTimestamp()))).equals(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(i).getTimestamp()))))) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(i).getTimestamp()))));
            }else
                holder.date.setVisibility(View.GONE);
        }*/
/**
           String a1 = new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp())));

            if (a1.equals(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp()))))) {
                // a=Long.valueOf(chatList.get(position).getTimestamp());
                //  String timeString=String.valueOf(a);
                a1 = new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp())));
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(chatList.get(position).getTimestamp()))));
                //  String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(Long.valueOf(timeString)));
                String today = new SimpleDateFormat("MMM d").format(new Date(System.currentTimeMillis()));
                if (today.equals(a1)) {

                    holder.date.setText("Today");
                }


            } else {
                holder.date.setVisibility(View.GONE);
            }


*/



        if(chatList.get(position).getMessage().contains("docx") |chatList.get(position).getMessage().contains("txt") | chatList.get(position).getMessage().contains("pdf")|chatList.get(position).getMessage().contains("doc"))
        {

            holder.docLayout.setVisibility(View.VISIBLE);
            holder.mContainer.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
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


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
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
            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("online").getRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (dataSnapshot.exists()){

                        if (connected) {
                            holder.docTick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);

                        }
                        /**  if(dataSnapshot.getValue().equals("true"))
                         {
                         holder.showOnline.setImageResource(R.drawable.show_online);
                         // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                         }*/else{
                            holder.docTick.setImageResource( R.drawable.ic_check_black_24dp);
                        }


                        //  getRecentMsg(chatId);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }
        else if(chatList.get(position).getMessage().contains(".mp3")) {
            holder.docLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.VISIBLE);
            holder.songName.setText(chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("/") + 1, chatList.get(position).getMessage().lastIndexOf(".mp3") - 1));
            holder.tick.setVisibility(View.GONE);
            //Uri myUri=Uri.parse(chatList.get(position).getMessage());

                final File file = new File(chatList.get(position).getMessage());

                final Uri outputFileUri;
                if (version < 24) {

                    outputFileUri = Uri.fromFile(file);
                } else {

                    outputFileUri = FileProvider.getUriForFile(context, getApplicationContext().getPackageName() + ".provider",
                            file);
                }


                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                final StorageReference photoRef = storageRef.child("Songs").child(outputFileUri.getLastPathSegment());
                photoRef.putFile(outputFileUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        holder.songProgress.setVisibility(View.VISIBLE);
                        holder.songPer.setText(((int) progress) + "%");


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_SHORT).show();
                        holder.songProgress.setVisibility(View.GONE);

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        holder.songProgress.setVisibility(View.GONE);


                    }
                });
                holder.songLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (file.exists()) {
                            Intent playAudioIntent = new Intent(Intent.ACTION_VIEW);

                            playAudioIntent.setDataAndType(Uri.fromFile(new File(file.getAbsolutePath())), "audio/mp3");
                            context.startActivity(playAudioIntent);
                        }
                    }
                });

            holder.songLayout.setOnLongClickListener(new View.OnLongClickListener() {
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


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
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


        }

        else if(chatList.get(position).getMessage().contains("chatImages")){

            holder.docLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.VISIBLE);
            holder.conLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
            Glide.with(context).load(chatList.get(position).getMessage()).error(R.drawable.nouser).into(holder.imgchat);

            if(chatList.get(position).getMessage().contains("caption"))
                holder.caption.setText(chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("caption")+7));
            else
                holder.caption.setVisibility(View.GONE);

            holder.imgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(),OpenChatPhoto.class);
                    i.putExtra("image",chatList.get(position).getMessage());
                    i.putExtra("chatId",chatId);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(i);
                }
            });
            String timeString=chatList.get(position).getTimestamp().toString();
            //holder.conTime.setText(timeString);
            holder.imgTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));


            holder.imgLayout.setOnLongClickListener(new View.OnLongClickListener() {
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


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
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


                holder.imgTick.setImageResource( R.drawable.ic_check_black_24dp);



            }
            if(!isNetworkAvailable()){

                holder.imgTick.setVisibility(View.GONE);
            }
            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("online").getRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (dataSnapshot.exists()){

                        if (connected) {
                            holder.imgTick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);

                        }
                        /**  if(dataSnapshot.getValue().equals("true"))
                         {
                         holder.showOnline.setImageResource(R.drawable.show_online);
                         // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                         }*/else{
                            holder.imgTick.setImageResource( R.drawable.ic_check_black_24dp);
                        }


                        //  getRecentMsg(chatId);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });





        }
        else if(chatList.get(position).getMessage().startsWith("Latitude")){

            holder.docLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.VISIBLE);
            holder.tick.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
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


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
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
        else if(chatList.get(position).getMessage().contains("Videos")){

            holder.docLayout.setVisibility(View.GONE);
            holder.mContainer.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.VISIBLE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.tick.setVisibility(View.GONE);
            String url=chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("2F")+2,chatList.get(position).getMessage().lastIndexOf("2F")+7)+":"+chatList.get(position).getMessage().substring(chatList.get(position).getMessage().lastIndexOf("3A")+2,chatList.get(position).getMessage().lastIndexOf("3A")+8);


            String timeString=chatList.get(position).getTimestamp().toString();
            //holder.conTime.setText(timeString);
            holder.videoTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));

            final StorageReference islandRef = FirebaseStorage.getInstance().getReference().child("Videos/"+url);
            final File storagePath = new File(Environment.getExternalStorageDirectory(), "Toado/Videos");
            final File myFile = new File(storagePath,url+".mp4");

            if(!myFile.exists()) {

              holder.vDownload.setVisibility(View.VISIBLE);
            }else {
                holder.vDownload.setVisibility(View.GONE);
                holder.vProgress.setVisibility(View.GONE);
                holder.locLayout.setVisibility(View.GONE);
                 holder.vThumb.setVisibility(View.VISIBLE);
                BitmapPool bitmapPool = Glide.get(getApplicationContext()).getBitmapPool();
                int microSecond = 6000000;// 6th second as an example
                VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                Glide.with(getApplicationContext())
                        .load(myFile.getAbsolutePath())
                        .asBitmap()
                        // .override(50,50)// Example
                        .videoDecoder(fileDescriptorBitmapDecoder)
                        .into(holder.videoChat);
            }

            holder.videoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if(!storagePath.exists()) {
                        storagePath.mkdirs();
                    }


                    if(!myFile.exists())
                    {

                        holder.vDownload.setVisibility(View.VISIBLE);


                       holder.vDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                islandRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                       holder.vDownload.setVisibility(View.GONE);
                                        holder.vProgress.setVisibility(View.GONE);
                                       holder.vThumb.setVisibility(View.VISIBLE);


                                        BitmapPool bitmapPool = Glide.get(getApplicationContext()).getBitmapPool();
                                        int microSecond = 6000000;// 6th second as an example
                                        VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                                        FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                                        Glide.with(getApplicationContext())
                                                .load(myFile.getAbsolutePath())
                                                .asBitmap()
                                                // .override(50,50)// Example
                                                .videoDecoder(fileDescriptorBitmapDecoder)
                                                .into(holder.videoChat);
                                        Intent tostart = new Intent(Intent.ACTION_VIEW);
                                        tostart.setDataAndType(Uri.fromFile(new File(myFile.getAbsolutePath())),"video/mp4");
                                        tostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(tostart);

                                        // Toast.makeText(context, myFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                                        //System.out.println("kudos");


                                        // Local temp file has been created
                                    }
                                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                       holder.vDownload.setVisibility(View.GONE);
                                        holder.vProgress.setVisibility(View.VISIBLE);
                                        // Toast.makeText(context, "Doing", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Toast.makeText(context,"File Not downloaded",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });

                    }
                    else{
                        holder.vDownload.setVisibility(View.GONE);
                         holder.vProgress.setVisibility(View.GONE);
                        holder.vThumb.setVisibility(View.VISIBLE);
                        Intent tostart = new Intent(Intent.ACTION_VIEW);
                        //tostart.setDataAndType(Uri.fromFile(new File(myFile.getAbsolutePath())),"video/mp4");
                        tostart.setDataAndType(Uri.fromFile(new File(myFile.getAbsolutePath())),"video/mp4");
                        tostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(tostart);


                    }


                }
            });

            holder.videoLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select any option");

// add a list
                    String[] animals = {"Delete", "Forward","Star"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {

                                case 0:


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case 1:




                                    Intent i=new Intent(getApplicationContext(), ForwardChat.class);
                                    i.putExtra("msg",chatList.get(position).getMessage());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    break;

                                case 2:
                                    if(chatList.get(position).getCurrentUser()){
                                        DatabaseReference newMessageDb = DBREF_USER_PROFILES.child(mykey).child("Starred").push();

                                        Map newMessage = new HashMap();
                                        newMessage.put("createdByUser",mykey);
                                        newMessage.put("text", chatList.get(position).getMessage());
                                        newMessage.put("timestamp",chatList.get(position).getTimestamp());

                                        newMessageDb.setValue(newMessage);
                                    }
                                    else {

                                        DatabaseReference newMessageDb = DBREF_USER_PROFILES.child(mykey).child("Starred").push();

                                        Map newMessage = new HashMap();
                                        newMessage.put("createdByUser",matchId);
                                        newMessage.put("text", chatList.get(position).getMessage());
                                        newMessage.put("timestamp",chatList.get(position).getTimestamp());

                                        newMessageDb.setValue(newMessage);



                                }

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


                holder.videoTick.setImageResource( R.drawable.ic_check_black_24dp);


                if(!  holder.videoTick.getDrawable().equals(R.drawable.ic_check_black_24dp))
                {
                    holder.videoTick.setImageResource( R.drawable.ic_check_black_24dp);
                }
            }
            if(!isNetworkAvailable()){

                holder.videoTick.setVisibility(View.GONE);
            }



            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.exists())
                    {
                        if(dataSnapshot.child("online").getValue().toString().equals("true"))
                        {
                            holder.videoTick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);
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
            holder.videoLayout.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
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


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                    //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
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
            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("online").getRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (dataSnapshot.exists()){

                        if (connected) {
                            holder.conTick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);

                        }
                        /**  if(dataSnapshot.getValue().equals("true"))
                         {
                         holder.showOnline.setImageResource(R.drawable.show_online);
                         // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                         }*/else{
                            holder.conTick.setImageResource( R.drawable.ic_check_black_24dp);
                        }


                        //  getRecentMsg(chatId);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        else {
            holder.docLayout.setVisibility(View.GONE);
            holder.imgLayout.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.songLayout.setVisibility(View.GONE);
            holder.locLayout.setVisibility(View.GONE);
            holder.conLayout.setVisibility(View.GONE);
            holder.mMessage.setText(chatList.get(position).getMessage());



            holder.mMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select any option");

// add a list
                    String[] animals = {"Copy", "Delete", "Forward","Star"};
                    builder.setItems(animals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ClipboardManager _clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    _clipboard.setText(chatList.get(position).getMessage());
                                    Toast.makeText(context, "Message Copied", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:


                                    DBREF_USER_PROFILES.child(mykey).child("connections").child("matches").child(matchId).child("ChatId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            chId=dataSnapshot.getValue().toString();






                                            FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).getRef().orderByKey().limitToFirst(1).addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
                                            Query deleteQuery = FirebaseDatabase.getInstance().getReference().child("Chat").child(dataSnapshot.getValue().toString()).orderByChild("text").limitToFirst(1);
                                            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot Snapshot) {
                                                  //  Snapshot.child("text").getRef().removeValue();

                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                            //Toast.makeText(context, dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    //Toast.makeText(context, chId, Toast.LENGTH_SHORT).show();
                            /*8
                                    */Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                    break;

                                case 2:




                                    Intent i=new Intent(getApplicationContext(), ForwardChat.class);
                                    i.putExtra("msg",chatList.get(position).getMessage());
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(i);
                                    break;

                                case 3:
                                    if(chatList.get(position).getCurrentUser()){
                                        DatabaseReference newMessageDb = DBREF_USER_PROFILES.child(mykey).child("Starred").push();

                                        Map newMessage = new HashMap();
                                        newMessage.put("createdByUser",mykey);
                                        newMessage.put("createdForUser",matchId);
                                        newMessage.put("text", chatList.get(position).getMessage());
                                        newMessage.put("timestamp",chatList.get(position).getTimestamp());
                                        newMessage.put("position",String.valueOf(holder.getAdapterPosition()));

                                        newMessageDb.setValue(newMessage);
                                    }
                                    else {

                                        DatabaseReference newMessageDb = DBREF_USER_PROFILES.child(mykey).child("Starred").push();

                                        Map newMessage = new HashMap();
                                        newMessage.put("createdByUser",matchId);
                                        newMessage.put("createdForUser",mykey);
                                        newMessage.put("text", chatList.get(position).getMessage());
                                        newMessage.put("timestamp",chatList.get(position).getTimestamp());
                                        newMessage.put("position",String.valueOf(holder.getAdapterPosition()));
                                        newMessageDb.setValue(newMessage);

                                    }

                                    break;
                            }
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });



            if(!chatList.get(position).getMessage().contains("firebase"))
            {

                holder.mMessage.setTextColor(Color.BLUE);
                holder.mMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse(chatList.get(position).getMessage()));
                        context.startActivity(myWebLink);
                    }
                });

            }



            String timeString=chatList.get(position).getTimestamp().toString();
            holder.textTime.setText(new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timeString))));
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(Long.valueOf(timeString)));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            //if (today.equals(time)) {
               //
            //} else {
             //  holder.textTime.setText(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(timeString))));
           // }
            if(isNetworkAvailable())
            {


                holder.tick.setImageResource( R.drawable.ic_check_black_24dp);



            }
            if(!isNetworkAvailable()){

                 holder.tick.setVisibility(View.GONE);
            }

            FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("online").getRef().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = dataSnapshot.getValue(Boolean.class);
                    if (dataSnapshot.exists()){

                        if (connected) {
                            holder.tick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);

                        }
                        /**  if(dataSnapshot.getValue().equals("true"))
                         {
                         holder.showOnline.setImageResource(R.drawable.show_online);
                         // Toast.makeText(context, "true", Toast.LENGTH_SHORT).show();

                         }*/else{
                            holder.tick.setImageResource( R.drawable.ic_check_black_24dp);
                        }


                        //  getRecentMsg(chatId);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }



        if(chatList.get(position).getCurrentUser()){
            holder.mContainer.setGravity(Gravity.RIGHT|Gravity.END);
            holder.mMessage.setTextColor(Color.parseColor("#404040"));
            holder.mMessage.setBackgroundResource(R.drawable.chatbubbleout);

            holder.docLinear.setGravity(Gravity.RIGHT|Gravity.END);
            holder.docText.setTextColor(Color.parseColor("#404040"));
            holder.docLinear.setBackgroundResource(R.drawable.chatbubbleout);

            holder.imgLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.caption.setTextColor(Color.parseColor("#404040"));
            holder.imgLinear.setBackgroundResource(R.drawable.chatbubbleout);
            holder.imgTime.setTextColor(Color.parseColor("#404040"));

            holder.videoLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.videoCaption.setTextColor(Color.parseColor("#404040"));
            holder.videoLinear.setBackgroundResource(R.drawable.chatbubbleout);
            holder.videoTime.setTextColor(Color.parseColor("#404040"));

            holder.locLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.chatMap.setBackgroundResource(R.drawable.chatbubbleout);
            holder.locTime.setTextColor(Color.parseColor("#404040"));
            holder.locLinear.setBackgroundResource(R.drawable.chatbubbleout);


            holder.songLinear.setGravity(Gravity.RIGHT|Gravity.END);
            holder.songName.setTextColor(Color.parseColor("#404040"));
            holder.songLinear.setBackgroundResource(R.drawable.chatbubbleout);

            holder.conLayout.setGravity(Gravity.RIGHT|Gravity.END);
            holder.conNumber.setTextColor(Color.parseColor("#404040"));
            holder.conName.setTextColor(Color.parseColor("#404040"));
            holder.conLinear.setBackgroundResource(R.drawable.chatbubbleout);
        }else{

            holder.tick.setVisibility(View.GONE);
            holder.docText.setVisibility(View.GONE);
            holder.imgTick.setVisibility(View.GONE);
            holder.videoTick.setVisibility(View.GONE);
            holder.locTick.setVisibility(View.GONE);
            holder.conTick.setVisibility(View.GONE);


            holder.mContainer.setGravity(Gravity.START);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mMessage.setBackgroundResource(R.drawable.chat_bubble);

            holder.docLayout.setGravity((Gravity.START));
            holder.docText.setTextColor(Color.parseColor("#FFFFFF"));
            holder.docLinear.setBackgroundResource(R.drawable.chat_bubble);

            holder.imgLayout.setGravity((Gravity.START));
            holder.caption.setTextColor(Color.parseColor("#FFFFFF"));
            holder.imgLinear.setBackgroundResource(R.drawable.chat_bubble);
            holder.imgTime.setTextColor(Color.parseColor("#FFFFFF"));

            holder.videoLayout.setGravity((Gravity.START));
           holder.videoLinear.setGravity(Gravity.START);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            holder.videoTime.setTextColor(Color.parseColor("#FFFFFF"));


            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);//in my case
            holder.videoLinear.setLayoutParams(layoutParams);
            holder.videoCaption.setTextColor(Color.parseColor("#FFFFFF"));
            holder.videoLinear.setBackgroundResource(R.drawable.chat_bubble);

            holder.locLayout.setGravity((Gravity.START));
            holder.chatMap.setBackgroundResource(R.drawable.chat_bubble);
            holder.locTime.setTextColor(Color.parseColor("#FFFFFF"));
            holder.locLinear.setBackgroundResource(R.drawable.chat_bubble);

            holder.songLinear.setGravity((Gravity.START));
            holder.songName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.songLinear.setBackgroundResource(R.drawable.chat_bubble);


            holder.conLayout.setGravity((Gravity.START));
            holder.conNumber.setTextColor(Color.parseColor("#FFFFFF"));
            holder.conName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.conLinear.setBackgroundResource(R.drawable.chat_bubble);

        }

    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        chatList.clear();
        if (charText.length() == 0) {
            chatList.addAll(arraylist);
        } else {
            for (ChatObject wp : arraylist) {
                if (wp.getMessage().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    chatList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
