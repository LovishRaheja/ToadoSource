package com.app.toado.activity.userdetail;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Chat.ChatObject;
import com.app.toado.activity.SharedItems.SharedItemsActivity;
import com.app.toado.activity.ToadoAppCompatActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.activity.register.OtpAct;
import com.app.toado.helper.CallHelper;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.helper.MyXMPP2;
import com.app.toado.model.MobileKey;
import com.app.toado.services.LocServ;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by aksha on 9/8/2017.
 */

public class UserDetail extends ToadoAppCompatActivity {

    private String mUserName;
    private String mUserKey;
    private String mUserPicture;
    private ImageView sendProfile;
    ImageButton imgCall,imgVideo;

    UserSession session;
    private SinchCallService callserv;
    boolean mServiceBound = false;
    TextView block;
    String mykey;
    private ImageView mUserImageVIew;
    private TextView mUserNameTextView;
    private ImageView mSettingsButton;
    TextView mediaCount;
    int count=5;
    TextView phoneNumber;

    private RelativeLayout media_title_container;
    LinearLayout blockLayout;
    ImageView back;
    private MarshmallowPermissions marshmallowPermissions;

    DatabaseReference mDatabaseUser, mDatabaseChat;
    private String currentUserID, matchId, chatId,ChId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detial);


        session=new UserSession(this);
        mykey=session.getUserKey();
        marshmallowPermissions = new MarshmallowPermissions(this);

        mUserName = getIntent().getStringExtra("UserName");
        mUserKey = getIntent().getStringExtra("UserKey");
        mUserPicture = getIntent().getStringExtra("UserPicture");
        ChId=getIntent().getStringExtra("ChatId");


        mediaCount=(TextView)findViewById(R.id.mediaCount);

        sendProfile=(ImageView)findViewById(R.id.sendProfile);

        blockLayout=(LinearLayout)findViewById(R.id.blockLayout);
        mUserImageVIew = (ImageView) findViewById(R.id.user_detail_image);
        mUserNameTextView = (TextView) findViewById(R.id.user_detail_name);
        block=(TextView)findViewById(R.id.block);
        imgCall=(ImageButton)findViewById(R.id.imgcall);
        imgVideo=(ImageButton)findViewById(R.id.imgvideo);

        phoneNumber=(TextView)findViewById(R.id.phoneNumber);

        back=(ImageView)findViewById(R.id.imgback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Glide.with(getApplicationContext()).load(mUserPicture).into(mUserImageVIew);
        mUserNameTextView.setText(mUserName);


        mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(mUserName).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){


                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                        if(snap.getValue().toString().startsWith("/file"))
                        {
                            count++;
                        }
                        if(snap.getValue().toString().contains("chatImages"))
                        {
                            count++;
                        }
                        if(snap.getValue().toString().startsWith("Latitude"))
                        {
                            count++;
                        }
                        if(snap.getValue().toString().contains("Videos"))
                        {
                            count++;
                        }
                        if(snap.getValue().toString().contains("mp3"))
                        {
                            count++;
                        }
                        if(snap.getValue().toString().startsWith("Name"))
                        {
                            count++;
                        }
                    }

                  //  Toast.makeText(UserDetail.this, String.valueOf(dataSnapshot.getChildrenCount()), Toast.LENGTH_SHORT).show();
                    mediaCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));

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

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallHelper.callbtnClicked(getSinchServiceInterface(), marshmallowPermissions, mykey, mykey, mUserName, mUserPicture, UserDetail.this);
            }
        });

        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallHelper.vidcallbtnClicked(getSinchServiceInterface(), marshmallowPermissions, mykey, mykey, mUserName, mUserPicture, UserDetail.this);
            }
        });





        DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(mUserKey).child("Blocked").child(session.getUserKey()).exists())
                {


                    Glide.with(UserDetail.this).load(R.drawable.nouser).dontAnimate()
                            .transform(new CircleTransform(UserDetail.this)).into(mUserImageVIew);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        DatabaseReference retreiveDetails = DBREF_USER_PROFILES.child(mUserKey).getRef();
        retreiveDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                phoneNumber.setText(dataSnapshot.child("phone").getValue().toString());

              }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(mykey).child("Blocked").child(mUserKey).exists())
                {

                    block.setText("Unblock");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(block.getText().equals("Block")) {

            blockLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // if(block.getText().equals("Block")) {


                        //}
                            /**else if(block.getText().equals("Unblock")){

                             DBREF_USER_PROFILES.child(mykey).child("Blocked").child(mUserKey).removeValue();
                             block.setText("Block");

                             }*/


                            if(dataSnapshot.child(mykey).child("Blocked").child(mUserKey).exists())
                            {
                                DBREF_USER_PROFILES.child(mykey).child("Blocked").child(mUserKey).removeValue();
                                block.setText("Block");



                            }

                            else if(!dataSnapshot.child(mykey).child("Blocked").child(mUserKey).exists())
                            {
                                DBREF_USER_PROFILES.child(mykey).child("Blocked").child(mUserKey).setValue("Blocked");
                                block.setText("Unblock");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }




        /**else if(block.getText().equals("Unblock")) {
            blockLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBREF_USER_PROFILES.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // if(block.getText().equals("Block")) {
                            if (dataSnapshot.child("Blocked").child(mUserKey).exists()) {
                                DBREF_USER_PROFILES.child(mykey).child("Blocked").child(mUserKey).removeValue();
                                block.setText("Block");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });*/
       // }
        media_title_container = (RelativeLayout) findViewById(R.id.media_title_container);
        media_title_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SharedItemsActivity.class);
                intent.putExtra("OtherUserKey", mUserKey);
                intent.putExtra("chatId",ChId);
                startActivity(intent);
            }
        });

        sendProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(UserDetail.this,"Not opening",Toast.LENGTH_LONG).show();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/png");
                Uri uri = Uri.parse("android.resource://com.app.toado/"+R.drawable.lovish);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
               // shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello, This is test Sharing");
                startActivity(Intent.createChooser(shareIntent, "Send your image"));

            }
        });

        mSettingsButton = (ImageView) findViewById(R.id.detail_settings);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("User Detail Settings Clicked");
                final PopupMenu popup = new PopupMenu(getApplicationContext(), findViewById(R.id.detail_settings));
                popup.getMenuInflater()
                        .inflate(R.menu.menu_user_detail, popup.getMenu());
                popup.show(); //showing popup menu
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case (R.id.share):
                                Drawable mDrawable = mUserImageVIew.getDrawable().getCurrent();
                                Bitmap mBitmap = ((GlideBitmapDrawable) mDrawable).getBitmap();

                                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, mUserName, null);
                                Uri uri = Uri.parse(path);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/jpeg");
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(intent, "Share Image"));
                                break;
                            case (R.id.edit):
                                DatabaseReference retreiveDetails = DBREF_USER_PROFILES.child(mUserKey).getRef();
                                retreiveDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(dataSnapshot.child("phone").getValue().toString()));
                                        Cursor mcursor = getContentResolver().query(lookupUri,new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},null, null, null);
                                        long idPhone = 0;
                                        try {
                                            if (mcursor != null) {
                                                if (mcursor.moveToFirst()) {
                                                    idPhone = Long.valueOf(mcursor.getString(mcursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
                                                    Log.d("", "Contact id::" + idPhone);
                                                }
                                            }
                                        } finally {
                                            mcursor.close();
                                        }
                                        if (idPhone > 0) {
                                            Intent intent1 = new Intent(Intent.ACTION_EDIT);
                                            intent1.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idPhone));
                                            startActivity(intent1);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "contact not in list",
                                                    Toast.LENGTH_SHORT).show();}

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                break;
                        }
                        return true;
                    }
                });
            }
        });

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





    private void getChatMessages() {

    }





    @Override
    public void onServiceConnected() {

        try {
            callserv = getSinchServiceInterface().getService();
            getSinchServiceInterface().startClient(mykey);
            mServiceBound = true;
        } catch (NullPointerException e) {
            //getSinchServiceInterface() in doStuff below throw null pointer error.
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceBound = false;
    }
}
