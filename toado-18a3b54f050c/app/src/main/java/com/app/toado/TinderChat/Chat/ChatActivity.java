package com.app.toado.TinderChat.Chat;

import android.animation.ObjectAnimator;
import android.app.Activity;

import com.app.toado.activity.GroupChat.GroupChatContacts.GroupChatContact;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.app.toado.Contact.SendContact;
import com.app.toado.FirebaseChat.AddCaptionChatPhoto;
import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.FirebaseChat.Message;
import com.app.toado.FirebaseChat.ShareLocation.MapsActivity;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatContacts;
import com.app.toado.activity.ToadoAppCompatActivity;
import com.app.toado.activity.chat.CamActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.activity.userdetail.UserDetail;
import com.app.toado.helper.CallHelper;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;


import org.jivesoftware.smack.chat.Chat;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.path;
import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class ChatActivity extends ToadoAppCompatActivity  {
    private static final int AUDIO_SELECT = 44;
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    Boolean bolkeypad = false;
    private EditText mSendEditText;
    ImageView attachment;
    LinearLayout layoutToAdd, layoutToAdd2;
    ImageView back;
    ImageView iconProfile;
    private String otherusername;
    private String profpic;
    private String otheruserkey,distance;
    TextView tvTitle,lastSeen;
    ImageButton imgCall,imgVideo;
    private SinchCallService callserv;
    boolean mServiceBound = false;
    private ImageButton takephoto, imgdocs1, imgdocs2;
    private ImageButton takephoto2;
    ImageButton galleryattach, galleryattach2;
    ImageButton songAttach,songAttach2;
    ImageButton locAttach,locAttach2;
    ImageButton contAttach,contAttach2;
    TextView blockText;
    RelativeLayout mainLayout;
    TextView date;

    ImageView showonline;

    private int PICK_DOCS = 44;
    boolean clicked;
    private int MULTIPLE_IMAGE_SELECT = 111;

    private FloatingActionButton mSendButton;
    private String cusrrentUserID;
    private UserSession session;
    private MarshmallowPermissions marshmallowPermissions;
    LinearLayout userTitleLayout;
    LinearLayout sendMsgLayout;
    TextView tvDistance;
    SearchView search;

    private String currentUserID, matchId, chatId;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinderchat);


        marshmallowPermissions = new MarshmallowPermissions(this);
        clicked = false;
        String mImageUri="noImage";
        mainLayout=(RelativeLayout)findViewById(R.id.mainLayout);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mImageUri = preferences.getString("image", null);
        mainLayout.setBackgroundResource(R.drawable.back);
        if(mImageUri!=null){
            if(mImageUri.equals("image")){
                mainLayout.setBackgroundResource(R.drawable.back);
            }
            else {
                File f = new File(getRealPathFromURI(Uri.parse(mImageUri)));
                Drawable d = Drawable.createFromPath(f.getAbsolutePath());
                mainLayout.setBackground(d);
            }
        }
        else
        {
            mainLayout.setBackgroundResource(R.drawable.back);
        }

        final Intent intentData = getIntent();
         matchId = intentData.getStringExtra("matchId");
        session = new UserSession(this);
        cusrrentUserID = session.getUserKey();



        showonline=(ImageView)findViewById(R.id.showOnline);
        lastSeen=(TextView)findViewById(R.id.lastSeen);
        blockText=(TextView)findViewById(R.id.blockText);
        sendMsgLayout=(LinearLayout)findViewById(R.id.relcomment);
        tvDistance=(TextView)findViewById(R.id.tvDistance);




        date=(TextView)findViewById(R.id.date);


        DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(session.getUserKey()).child("Blocked").child(matchId).exists())
                {
                    sendMsgLayout.setVisibility(View.GONE);
                    blockText.setVisibility(View.VISIBLE);


                }
                else if(dataSnapshot.child(matchId).child("Blocked").child(session.getUserKey()).exists())
                {
                    sendMsgLayout.setVisibility(View.GONE);
                    blockText.setVisibility(View.GONE);
                    Glide.with(ChatActivity.this).load(R.drawable.nouser).dontAnimate()
                            .transform(new CircleTransform(ChatActivity.this)).into(iconProfile);
                    tvDistance.setVisibility(View.INVISIBLE);


                }
                else{
                    sendMsgLayout.setVisibility(View.VISIBLE);
                    blockText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("lastseen").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    lastSeen.setText("Last seen at "+dataSnapshot.getValue().toString().substring(4,16));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



       FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").child(matchId).child("online").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if (dataSnapshot.exists()){

                    if (connected) {
                        showonline.setImageResource(R.drawable.show_online);
                        lastSeen.setVisibility(View.GONE);

                    }
                    else{
                       showonline.setImageResource(R.drawable.not_online);
                        lastSeen.setVisibility(View.VISIBLE);
                    }


                    //  getRecentMsg(chatId);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        final DatabaseReference matchDb2 = DBREF_USER_PROFILES.child(matchId).child("name");
        matchDb2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   otherusername=dataSnapshot.getValue().toString();
                    tvTitle.setText(otherusername);
                }

                // String key = matchDb.getKey();
                //  Toast.makeText(MatchesActivity.this, key, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference matchDb = DBREF_USER_PROFILES.child(matchId).child("profpicurl");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                  profpic=dataSnapshot.getValue().toString();
                    Glide.with(ChatActivity.this).load(profpic).dontAnimate()
                            .transform(new CircleTransform(ChatActivity.this)).into(iconProfile);
                }

                // String key = matchDb.getKey();
                //  Toast.makeText(MatchesActivity.this, key, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();





        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //mRecyclerView.setNestedScrollingEnabled(false);
       // mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);

       // Toast.makeText(ChatActivity.this, String.valueOf(getDataSetChat().size()), Toast.LENGTH_SHORT).show();
        mChatAdapter = new ChatAdapter(getDataSetChat(), ChatActivity.this,matchId,chatId,session.getUserKey());
        String a="a";
        mRecyclerView.setAdapter(mChatAdapter);

        //mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());


        mDatabaseChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                   // mRecyclerView.smoothScrollToPosition((int) dataSnapshot.getChildrenCount());
                 //   mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());



                    mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                mRecyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());

                                    }
                                }, 100);
                            }
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        search = (SearchView)findViewById(R.id.searchView);
       // search.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.LIGHTEN);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String stext) {
                mChatAdapter.filter(stext);
                mChatAdapter.notifyDataSetChanged();
                return false;
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       // String size=prefs.getString("size",);
       // Toast.makeText(ChatActivity.this, String.valueOf(size), Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        //Toast.makeText(ChatActivity.this, listSize(), Toast.LENGTH_SHORT).show();
        //mRecyclerView.scrollToPosition(getDataSetChat().size()-2);


       // mRecyclerView.smoothScrollToPosition(getDataSetChat().size() - 1);
       // mChatLayoutManager.smoothScrollToPosition(mRecyclerView,null,resultsChat.size()-2 );



        mSendEditText = findViewById(R.id.message);
        mSendButton = findViewById(R.id.send);
        attachment=(ImageView)findViewById(R.id.attachment);
        layoutToAdd = (LinearLayout) findViewById(R.id.attachmentpopup);
        layoutToAdd2 = (LinearLayout) findViewById(R.id.attachmentpopup2);
        back=(ImageView)findViewById(R.id.imgback);
        iconProfile=(ImageView)findViewById(R.id.icon_profile);
       tvTitle=(TextView)findViewById(R.id.tvTitle);
        imgCall=(ImageButton)findViewById(R.id.imgcall);
        imgVideo=(ImageButton)findViewById(R.id.imgvideo);
        userTitleLayout=(LinearLayout)findViewById(R.id.user_title_lay);
        imgdocs1 = (ImageButton) findViewById(R.id.buttondocs);
        imgdocs2 = (ImageButton) findViewById(R.id.buttondocs2);
        takephoto = (ImageButton) findViewById(R.id.takephoto);
        takephoto2 = (ImageButton) findViewById(R.id.takephoto2);
        galleryattach = (ImageButton) findViewById(R.id.galleryattach);
        galleryattach2 = (ImageButton) findViewById(R.id.galleryattach2);
        songAttach = (ImageButton) findViewById(R.id.songAttach);
        songAttach2 = (ImageButton) findViewById(R.id.songAttach2);
        locAttach = (ImageButton) findViewById(R.id.locAttach);
        locAttach2 = (ImageButton) findViewById(R.id.locAttach2);
        contAttach = (ImageButton) findViewById(R.id.contAttach);
        contAttach2 = (ImageButton) findViewById(R.id.contAttach2);


        //tvTitle.setText(otherusername);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        userTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), UserDetail.class);
                intent.putExtra("UserName", otherusername);
                intent.putExtra("UserKey", matchId);
                intent.putExtra("UserPicture", profpic);
                intent.putExtra("ChatId",chatId);
                startActivity(intent);

            }
        });


        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutToAdd.getVisibility() == View.VISIBLE || layoutToAdd2.getVisibility() == View.VISIBLE) {
                    layoutToAdd.setVisibility(View.GONE);
                    layoutToAdd2.setVisibility(View.GONE);
                } else {
                    // Log.d(TAG, "bol keypad" + bolkeypad);

                    if (bolkeypad) {
                        layoutToAdd.setVisibility(View.GONE);
                        layoutToAdd2.setVisibility(View.VISIBLE);
                    } else {
                        layoutToAdd.setVisibility(View.VISIBLE);
                        layoutToAdd2.setVisibility(View.GONE);
                    }
                }
            }
        });


        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallHelper.callbtnClicked(getSinchServiceInterface(), marshmallowPermissions, session.getUserKey(), matchId, otherusername, profpic, ChatActivity.this);
            }
        });

        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallHelper.vidcallbtnClicked(getSinchServiceInterface(), marshmallowPermissions, session.getUserKey(), matchId, otherusername, profpic, ChatActivity.this);
            }
        });


        imgdocs1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocs();
            }
        });
        imgdocs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDocs();
            }
        });

        takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        takephoto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        galleryattach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.d(TAG, "Multiple images called " + MULTIPLE_IMAGE_SELECT);
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), MULTIPLE_IMAGE_SELECT);
                layoutToAdd.setVisibility(View.GONE);
                layoutToAdd2.setVisibility(View.GONE);
            }
        });

        galleryattach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Multiple images called 2 " + MULTIPLE_IMAGE_SELECT);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Images"), MULTIPLE_IMAGE_SELECT);
                layoutToAdd.setVisibility(View.GONE);
                layoutToAdd2.setVisibility(View.GONE);
            }
        });


        contAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
               // SharedPreferences.Editor editor = prefs.edit();
             //k   editor.putString("otheruserkey",matchId);
                Intent i=new Intent(ChatActivity.this, GroupChatContact.class);
                i.putExtra("otheruserkey",matchId);
                startActivity(i);
            }
        });
        contAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ChatActivity.this, GroupChatContact.class);
                startActivity(i);
            }
        });

        locAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(FirebaseChat.this, "Location Not Sending", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(ChatActivity.this, MapsActivity.class);
                i.putExtra("UserKey", matchId);
                startActivity(i);


            }
        });

        locAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Location Not Sending", Toast.LENGTH_SHORT).show();

                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(ChatActivity.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivity(intent);

                } catch (GooglePlayServicesRepairableException e) {
                    // Log.e(LOG_TAG, e.toString(), e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Log.e(LOG_TAG, e.toString(), e);
                }

            }
        });

        songAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(pickAudioIntent, "Select Images"), AUDIO_SELECT);
                layoutToAdd.setVisibility(View.GONE);
                layoutToAdd2.setVisibility(View.GONE);
            }
        });

        songAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(Intent.createChooser(pickAudioIntent, "Select Images"), AUDIO_SELECT);
                layoutToAdd.setVisibility(View.GONE);
                layoutToAdd2.setVisibility(View.GONE);
            }
        });
        SharedPreferences prefs3 = PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs3.getBoolean("checked",true))
        {
            mSendEditText.setSingleLine();
            mSendEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
            mSendEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_GO) {

                        String content = mSendEditText.getText().toString().trim();
                        if (content.length() > 0) {
                            DatabaseReference newMessageDb = mDatabaseChat.push();

                            Map newMessage = new HashMap();
                            newMessage.put("createdByUser", session.getUserKey());
                            newMessage.put("text", content);
                            newMessage.put("timestamp",System.currentTimeMillis());

                            newMessageDb.setValue(newMessage);
                        }
                        handled = true;
                    }
                    return handled;
                }
            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());

    }
    private void sendMessage() {
        final String sendMessageText = mSendEditText.getText().toString();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", session.getUserKey());
            newMessage.put("text", sendMessageText);
            newMessage.put("timestamp",System.currentTimeMillis());

            newMessageDb.setValue(newMessage);

            FirebaseDatabase.getInstance().getReference().child("Fcmtokens").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String token=dataSnapshot.child(session.getUserKey()).child("token").getValue().toString();
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("matchId",matchId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent contentIntent = PendingIntent.getActivity(ChatActivity.this, (int) (Math.random() * 100), intent, 0);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(ChatActivity.this, token)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                            R.mipmap.ic_launcher))
                                    .setContentTitle("Message")
                                    .setContentText(sendMessageText)
                                    .setSound(defaultSoundUri)
                                    .setAutoCancel(true)
                                    .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                                    .setContentIntent(contentIntent);


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0, notificationBuilder.build());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            });




        }
        mSendEditText.setText(null);
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                    //mChatLayoutManager.scrollToPosition(getDataSetChat().size()-1);
                    //mRecyclerView.scrollToPosition(getDataSetChat().size()-10);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void pickDocs() {

        if (marshmallowPermissions.checkPermissionForReadStorage()) {


            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType("pdf/*");
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_DOCS);
            //  Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
            //  intent.setType("text/plain");
            //  startActivityForResult(intent, PICK_DOCS);
            try {

            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog

            }
        } else {
            marshmallowPermissions.requestPermissionForReadExternalStorage();
        }

        /**Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         intent.setType("text/plain");
         startActivityForResult(intent, 7);
         */
        layoutToAdd.setVisibility(View.GONE);
        clicked = false;

    }

    public void takePhoto() {
        //  Log.d(TAG, "take photo case2");
        layoutToAdd.setVisibility(View.GONE);
        layoutToAdd2.setVisibility(View.GONE);
        Intent in = new Intent(this, CamActivity.class);
        startImageComment(in);
        layoutToAdd.setVisibility(View.GONE);
        layoutToAdd2.setVisibility(View.GONE);
    }


    private void startImageComment(Intent intent) {
        //Log.d(TAG, "image comment sending");
        intent.putExtra("username", otherusername);
        intent.putExtra("otheruserkey", matchId);
        intent.putExtra("mykey", session.getUserKey());
        startActivity(intent);
    }



    @Override
    public void onServiceConnected() {

        try {
            callserv = getSinchServiceInterface().getService();
            getSinchServiceInterface().startClient(session.getUserKey());
            mServiceBound = true;
        } catch (NullPointerException e) {
            //getSinchServiceInterface() in doStuff below throw null pointer error.
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceBound = false;
    }

    private void getChatMessages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    String timestamp=null;

                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(dataSnapshot.child("timestamp").getValue()!=null){
                        timestamp = dataSnapshot.child("timestamp").getValue().toString();
                    }


                    if(message!=null && createdByUser!=null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(session.getUserKey())){
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message,timestamp, currentUserBoolean);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();

                    }
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
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ChatActivity.this, MainAct.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MULTIPLE_IMAGE_SELECT) {


            final Uri imageUri = data.getData();

            String path=ChatActivity.getPath(getApplication(),imageUri);
            if (imageUri.toString().contains("image")) {
                Intent i=new Intent(ChatActivity.this,AddCaptionChatPhoto.class);
                i.putExtra("imageUrl",imageUri.toString());
                i.putExtra("mykey",session.getUserKey());
                i.putExtra("otheruserkey",matchId);
                startActivity(i);
                //handle image

            }else{

                // Bitmap bmThumbnail= ThumbnailUtils.createVideoThumbnail(imageUri.toString(), MediaStore.Images.Thumbnails.MICRO_KIND);
                //imageview_micro.setImageBitmap(bmThumbnail);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("videoUrl",imageUri.toString());
                Intent i=new Intent(ChatActivity.this,AddCaptionChatPhoto.class);
                i.putExtra("imageUrl",imageUri.toString());
                i.putExtra("path",path);
                i.putExtra("mykey",session.getUserKey());
                i.putExtra("otheruserkey",matchId);
                startActivity(i);




        }
        }
        else if(requestCode==AUDIO_SELECT)
        {
            if(resultCode==Activity.RESULT_OK)
            {

                final Uri imageUri = data.getData();

                String path=ChatActivity.getPath(getApplication(),imageUri);
                File file = new File(path);
                String p=file.getAbsolutePath();

               // String finalPath=p.substring(0,p.indexOf("/",p.indexOf("/")+1))+"/emulated/0"+p.substring(p.indexOf("/",p.indexOf("/")+3));
                if(p.length()>0){
                    DatabaseReference newMessageDb = mDatabaseChat.push();

                    Map newMessage = new HashMap();
                    newMessage.put("createdByUser", session.getUserKey());

                    newMessage.put("text", p);

                    newMessage.put("timestamp",System.currentTimeMillis());

                    newMessageDb.setValue(newMessage);
                }


            }
        }
       else if(requestCode==PICK_DOCS)
        {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                String uriString = uri.toString();

                File myFile = new File(uriString);

                final String path = myFile.getAbsolutePath();
                //path=path.substring(path.lastIndexOf("/")+1);


                if(path.length()>0){
                    DatabaseReference newMessageDb = mDatabaseChat.push();

                    Map newMessage = new HashMap();
                    newMessage.put("createdByUser", session.getUserKey());
                    newMessage.put("text", path);
                    newMessage.put("timestamp",System.currentTimeMillis());

                    newMessageDb.setValue(newMessage);
                }
                mSendEditText.setText(null);




            }else{
                Log.e("data","cance");
            }
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String ItemName = intent.getStringExtra("size");
           // String qty = intent.getStringExtra("quantity");
          //  Toast.makeText(ChatActivity.this,ItemName ,Toast.LENGTH_SHORT).show();
            //mChatLayoutManager.scrollToPosition(Integer.valueOf(ItemName));
          //  mRecyclerView.scrollToPosition(Integer.valueOf(ItemName));
            //ObjectAnimator.ofInt(mRecyclerView, "scrollY",  Integer.valueOf(ItemName)).setDuration(0).start();
          mRecyclerView.smoothScrollBy(0,Integer.valueOf(ItemName));
            int i=0;

            mRecyclerView.smoothScrollToPosition(Integer.valueOf(ItemName));


          /**  i=Integer.valueOf(ItemName);
            if(i==Integer.valueOf(ItemName))
            {
                mRecyclerView.stopScroll();
                mRecyclerView.setNestedScrollingEnabled(false);
            }*/


           // mRecyclerView.stopScroll();
           // mRecyclerView.scrollTo(0,Integer.valueOf(ItemName));

        }
    };

    private static String getPath(Application application, Uri imageUri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(application, imageUri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(imageUri)) {
                final String docId = DocumentsContract.getDocumentId(imageUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(imageUri)) {

                final String id = DocumentsContract.getDocumentId(imageUri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(application, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(imageUri)) {
                final String docId = DocumentsContract.getDocumentId(imageUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(application, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();

            return getDataColumn(application, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }

        return null;
    }



    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     *
     *
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }




     ArrayList<ChatObject> resultsChat = new ArrayList<ChatObject>();
     List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

     String listSize()
    {
        return String.valueOf(getDataSetChat().size());
    }




}
