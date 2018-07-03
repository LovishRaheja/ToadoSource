package com.app.toado.activity.GroupChat;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.AddCaptionChatPhoto;
import com.app.toado.FirebaseChat.ShareLocation.MapsActivity;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Chat.ChatAdapter;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatContacts;
import com.app.toado.TinderChat.Chat.ChatObject;
import com.app.toado.activity.GroupChat.GroupChatContacts.GroupChatContact;
import com.app.toado.activity.chat.CamActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
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
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;


public class GroupChatActivity extends AppCompatActivity {

    ImageView back;
    LinearLayout topBar;
    String a;
    TextView groupName;
    ImageView iconprofile;
    String groupId;
    private RecyclerView mRecyclerView;
    GroupChatAdapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private FloatingActionButton mSendButton;
    private String cusrrentUserID;
    private UserSession session;
    private MarshmallowPermissions marshmallowPermissions;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    LinearLayout layoutToAdd, layoutToAdd2;
    ImageView attachment;
    Boolean bolkeypad = false;
    private ImageButton takephoto, imgdocs1, imgdocs2;
    private ImageButton takephoto2;
    ImageButton galleryattach, galleryattach2;
    ImageButton songAttach,songAttach2;
    ImageButton locAttach,locAttach2;
    ImageButton contAttach,contAttach2;
    boolean clicked;
    private int PICK_DOCS = 44;
    private int MULTIPLE_IMAGE_SELECT = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        marshmallowPermissions = new MarshmallowPermissions(this);
        session=new UserSession(this);

        clicked = false;
        mSendEditText = (EditText)findViewById(R.id.typeComment);
        mSendButton =(FloatingActionButton) findViewById(R.id.sendButton);
        attachment=(ImageView)findViewById(R.id.attachment);
        layoutToAdd = (LinearLayout) findViewById(R.id.attachmentpopup);
        layoutToAdd2 = (LinearLayout) findViewById(R.id.attachmentpopup2);
        back=(ImageView)findViewById(R.id.imgback);
        topBar=(LinearLayout)findViewById(R.id.user_title_lay);
        groupName=(TextView)findViewById(R.id.groupName);



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

        iconprofile=(ImageView)findViewById(R.id.icon_profile);
        groupId=getIntent().getStringExtra("groupId");
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

        FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupName.setText(dataSnapshot.child("name").getValue().toString());
                Glide.with(GroupChatActivity.this).load(dataSnapshot.child("image").getValue().toString()).dontAnimate()
                        .transform(new CircleTransform(GroupChatActivity.this)).error(R.drawable.nouser).into(iconprofile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GroupChatActivity.this,GroupDetail.class);
                i.putExtra("groupId",groupId);
                startActivity(i);
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
                Intent i=new Intent(GroupChatActivity.this, GroupChatContact.class);
                i.putExtra("otheruserkey",groupId);
                startActivity(i);
            }
        });
        contAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GroupChatActivity.this, ChatContacts.class);
                startActivity(i);
            }
        });

        locAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(FirebaseChat.this, "Location Not Sending", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(GroupChatActivity.this, GroupShareLocation.class);
                i.putExtra("UserKey", groupId);
                startActivity(i);


            }
        });


        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(groupId);

        getChatMessages();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerChat);
        mChatLayoutManager = new LinearLayoutManager(GroupChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);

        mChatAdapter = new GroupChatAdapter(getDataSetChat(), GroupChatActivity.this,groupId,session.getUserKey());


        mRecyclerView.setAdapter(mChatAdapter);

        FirebaseDatabase.getInstance().getReference().child("Chat").child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    mRecyclerView.smoothScrollToPosition((int) dataSnapshot.getChildrenCount());
                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
                    mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v,
                                                   int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            if (bottom < oldBottom) {
                                mRecyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerView.smoothScrollToPosition((int) dataSnapshot.getChildrenCount());

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



        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
finish();
        return;
    }

    private void pickDocs() {

        if (marshmallowPermissions.checkPermissionForReadStorage()) {


            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            //intent.setType("pdf/*");
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_DOCS);

        } else {
            marshmallowPermissions.requestPermissionForReadExternalStorage();
        }
        layoutToAdd.setVisibility(View.GONE);
        clicked = false;

    }

    public void takePhoto() {
        //  Log.d(TAG, "take photo case2");
        layoutToAdd.setVisibility(View.GONE);
        layoutToAdd2.setVisibility(View.GONE);
        Intent in = new Intent(this, CamActivity.class);
       // in.putExtra("username", otherusername);
       // in.putExtra("otheruserkey", matchId);
        in.putExtra("mykey", session.getUserKey());
        startActivity(in);

    }


    private void startImageComment(Intent intent) {
        //Log.d(TAG, "image comment sending");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MULTIPLE_IMAGE_SELECT) {


            final Uri imageUri = data.getData();

            String path=GroupChatActivity.getPath(getApplication(),imageUri);
            if (imageUri.toString().contains("image")) {
                Intent i=new Intent(GroupChatActivity.this,AddCaptionChatPhoto.class);

                startActivity(i);
                //handle image

            }else{

                // Bitmap bmThumbnail= ThumbnailUtils.createVideoThumbnail(imageUri.toString(), MediaStore.Images.Thumbnails.MICRO_KIND);
                //imageview_micro.setImageBitmap(bmThumbnail);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("videoUrl",imageUri.toString());
                Intent i=new Intent(GroupChatActivity.this,AddCaptionChatPhoto.class);
                startActivity(i);




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



    private void sendMessage() {
        final String sendMessageText = mSendEditText.getText().toString();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", session.getUserKey());
            newMessage.put("text", sendMessageText);
            newMessage.put("timestamp",System.currentTimeMillis());

            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
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
                        GroupChatObject newMessage = new GroupChatObject(createdByUser,message,timestamp, currentUserBoolean);
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




    ArrayList<GroupChatObject> resultsChat = new ArrayList<GroupChatObject>();
    List<GroupChatObject> getDataSetChat() {
        return resultsChat;
    }

}
