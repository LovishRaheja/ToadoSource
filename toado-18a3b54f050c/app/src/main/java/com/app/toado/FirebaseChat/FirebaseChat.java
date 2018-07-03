package com.app.toado.FirebaseChat;

import android.app.Activity;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.Contact.SendContact;
import com.app.toado.FirebaseChat.ShareLocation.MapsActivity;
import com.app.toado.Manifest;
import com.app.toado.R;
import com.app.toado.activity.ToadoAppCompatActivity;
import com.app.toado.activity.chat.CamActivity;
import com.app.toado.activity.userdetail.UserDetail;
import com.app.toado.helper.CallHelper;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;

import static android.support.v7.appcompat.R.attr.title;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

public class FirebaseChat extends ToadoAppCompatActivity{

    private RecyclerView recyclerChat;
    private ListMessageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    RelativeLayout chatLay;
    List<String> imagesEncodedList;
    ArrayList<String> multipleImagesPathList;
    Message newMessage;
    HashMap mapMessage;
    boolean click;
    LinearLayout longClickBar;
    private int PICK_DOCS = 44;
    private int PLACE_PICKER_REQUEST=100;
    boolean clicked;
    private int MULTIPLE_IMAGE_SELECT = 111;
    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    UserSession usrsess;
    Boolean bolkeypad = false;
    public String mykey = "nokey";
    private String roomId;
    private String otherusername;
    private String profpic;
    private String otheruserkey,distance;
    private Consersation consersation;
    EditText textbox;
    String username;
    ImageView attachment,back,mSettings;
    LinearLayout layoutToAdd, layoutToAdd2;
    private ImageButton takephoto, imgdocs1, imgdocs2;
    private ImageButton takephoto2;
    ImageButton galleryattach, galleryattach2;
    ImageButton songAttach,songAttach2;
    ImageButton locAttach,locAttach2;
    ImageButton contAttach,contAttach2;
    ImageButton imgCall,imgVideo;
    private SinchCallService callserv;
    boolean mServiceBound = false;
    ImageView iconProfile;
    LinearLayout userTitleLayout;
    LinearLayout sendMsgLayout;
    TextView blockText;
    Double latitude,longitude;
    ImageButton chatSearch;
    RelativeLayout searchLayout;
    EditText searchRecycler;
    ImageView cancelSearch;
    TextView tvDistance;

    boolean longClick=false;
    private MarshmallowPermissions marshmallowPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_chat);




        usrsess = new UserSession(this);
        mykey = usrsess.getUserKey();
        Intent intentData = getIntent();
        username=usrsess.getUsername();
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        otheruserkey = intentData.getStringExtra("otheruserkey");
        otherusername = intentData.getStringExtra("otherusername");
        profpic = intentData.getStringExtra("profiletype");
        distance=intentData.getStringExtra("distance");

        tvDistance=(TextView)findViewById(R.id.tvDistance);
        tvDistance.setText(distance+" miles away");

      /*  Bundle extras = getIntent().getExtras();
        if (extras != null) {
             latitude = extras.getDouble("latitude");
             longitude = extras.getDouble("longitude");
        }*/
       // longitude=intentData.getStringExtra("longitude");
       // latitude=intentData.getStringExtra("latitude");
       // Toast.makeText(FirebaseChat.this, latitude.toString()+"\n"+longitude.toString(), Toast.LENGTH_SHORT).show();


        consersation = new Consersation();
        marshmallowPermissions = new MarshmallowPermissions(this);
        clicked = false;
       // Intent getToUserProfileIntent  = new Intent(getApplicationContext(), ListMessageAdapter.class);
       // getToUserProfileIntent .putExtra(otheruserkey,"otheruserkey");
       // startActivity(getToUserProfileIntent );

        textbox=(EditText)findViewById(R.id.typeComment);
        TextView tvTitle=(TextView)findViewById(R.id.tvTitle);
        attachment=(ImageView)findViewById(R.id.attachment);
        layoutToAdd = (LinearLayout) findViewById(R.id.attachmentpopup);
        layoutToAdd2 = (LinearLayout) findViewById(R.id.attachmentpopup2);
        chatLay=(RelativeLayout)findViewById(R.id.chatlay);
        back=(ImageView)findViewById(R.id.imgback);
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
        iconProfile=(ImageView)findViewById(R.id.icon_profile);
        userTitleLayout=(LinearLayout)findViewById(R.id.user_title_lay);
        sendMsgLayout=(LinearLayout)findViewById(R.id.relcomment);
        blockText=(TextView)findViewById(R.id.blockText);

        chatSearch=(ImageButton)findViewById(R.id.chatSearch);
        searchLayout=(RelativeLayout)findViewById(R.id.searchlayout);
        searchRecycler=(EditText)findViewById(R.id.searchRecycler);
        cancelSearch=(ImageView)findViewById(R.id.cancelSearch);
        longClickBar=(LinearLayout)findViewById(R.id.longClick);
        searchRecycler.setSingleLine();
        searchRecycler.setImeOptions(EditorInfo.IME_ACTION_SEARCH);


        searchRecycler.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {



                    final String searchtext = searchRecycler.getText().toString();

                    DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
                    Query query = mFirebaseDatabaseReference.child("message").child("null").getRef().orderByChild("text").equalTo(searchtext);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(DataSnapshot dataSnapshot) {
                                                                 if(dataSnapshot.exists()) {
                                                                     Toast.makeText(FirebaseChat.this, "Search Done", Toast.LENGTH_SHORT).show();
                                                                 }
                                                                 else{
                                                                     Toast.makeText(FirebaseChat.this, "Not Found", Toast.LENGTH_SHORT).show();
                                                                 }
                                                             }

                                                             @Override
                                                             public void onCancelled(DatabaseError databaseError) {
                                                                 Toast.makeText(FirebaseChat.this, "Search Cancelled", Toast.LENGTH_SHORT).show();
                                                             }
                                                         });


                            // Toast.makeText(FirebaseChat.this,"Searching in Progress",Toast.LENGTH_LONG).show();
                            searchRecycler.setSingleLine();
                    searchRecycler.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                }
                return false;
            }
        });

        chatSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs.getBoolean("checked",true))
        {
            textbox.setSingleLine();
            textbox.setImeOptions(EditorInfo.IME_ACTION_GO);
            textbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_GO) {

                        String content = textbox.getText().toString().trim();
                        if (content.length() > 0) {
                            textbox.setText("");
                            Message newMessage = new Message();
                            newMessage.text = content;
                            newMessage.idSender = mykey;
                            newMessage.idReceiver = otheruserkey;
                            newMessage.timestamp = System.currentTimeMillis();

                            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                        }
                        handled = true;
                    }
                    return handled;
                }
            });
        }




   /**     DBREF_USER_PROFILES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(mykey).child("Blocked").child(otheruserkey).exists())
                {
                    sendMsgLayout.setVisibility(View.GONE);
                    blockText.setVisibility(View.VISIBLE);

                }
               else if(dataSnapshot.child(otheruserkey()).child("Blocked").child(mykey).exists())
                {
                    sendMsgLayout.setVisibility(View.GONE);
                    blockText.setVisibility(View.VISIBLE);
                    blockText.setText("You have been blocked by the user. You can't send messages to the user");
                }
                else{
                    sendMsgLayout.setVisibility(View.VISIBLE);
                    blockText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/


        userTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), UserDetail.class);
                intent.putExtra("UserName", otherusername);
                intent.putExtra("UserKey", otheruserkey);
                intent.putExtra("UserPicture", profpic);
                startActivity(intent);

            }
        });

        Glide.with(FirebaseChat.this).load(profpic).dontAnimate()
                .transform(new CircleTransform(FirebaseChat.this)).error(R.drawable.nouser).into(iconProfile);

        imgCall=(ImageButton)findViewById(R.id.imgcall);
        imgVideo=(ImageButton)findViewById(R.id.imgvideo);





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });


        FloatingActionButton sendButton=(FloatingActionButton)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String content = textbox.getText().toString().trim();
                if (content.length() > 0) {
                    textbox.setText("");
                    Message newMessage = new Message();
                    newMessage.text = content;
                    newMessage.idSender = mykey;
                    newMessage.idReceiver = otheruserkey;
                    newMessage.timestamp = System.currentTimeMillis();

                    FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                }

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

        songAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivity(pickAudioIntent);
            }
        });

        songAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickAudioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivity(pickAudioIntent);
            }
        });

        locAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(FirebaseChat.this, "Location Not Sending", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(FirebaseChat.this, MapsActivity.class);
                i.putExtra("UserKey", otheruserkey);
                startActivity(i);
               /** try {
                   /** PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(FirebaseChat.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivity(intent);

                } catch (GooglePlayServicesRepairableException e) {
                    // Log.e(LOG_TAG, e.toString(), e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Log.e(LOG_TAG, e.toString(), e);
                }*/

            }
        });

        locAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FirebaseChat.this, "Location Not Sending", Toast.LENGTH_SHORT).show();

                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(FirebaseChat.this);
                    // Start the intent by requesting a result,
                    // identified by a request code.
                    startActivity(intent);

                } catch (GooglePlayServicesRepairableException e) {
                   // Log.e(LOG_TAG, e.toString(), e);
                } catch (GooglePlayServicesNotAvailableException e) {
                   // Log.e(LOG_TAG, e.toString(), e);
                }
                /** try {
                 PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                 startActivityForResult(builder.build(FirebaseChat.this), PLACE_PICKER_REQUEST);
                 } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                 e.printStackTrace();
                 }*/
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



        mSettings = (ImageButton) findViewById(R.id.imgsettings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(getApplicationContext(), findViewById(R.id.imgsettings));
                popup.getMenuInflater()
                        .inflate(R.menu.menu_chat, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case (R.id.menu_contact):
                                Intent intent = new Intent(getApplicationContext(), UserDetail.class);
                                intent.putExtra("UserName", otherusername);
                                intent.putExtra("UserKey", otheruserkey);
                                intent.putExtra("UserPicture", profpic);
                                startActivity(intent);
                                break;
                            case (R.id.menu_media):
                                break;
                            case (R.id.menu_email):
                               // selectAll();
                                break;
                            case (R.id.menu_wallpaper):
                               // changeWallpaper();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        contAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FirebaseChat.this, SendContact.class);
                startActivity(i);
            }
        });
        contAttach2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FirebaseChat.this, SendContact.class);
                startActivity(i);
            }
        });



        tvTitle.setText(otherusername);

            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation,otheruserkey);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                       mapMessage = (HashMap) dataSnapshot.getValue();
                        newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
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
            recyclerChat.setAdapter(adapter);



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

    @Override
    protected void onResume() {
        super.onResume();



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean data = false;


       if(!data)
        {
            longClickBar.setVisibility(View.GONE);
        }
        else if(data){

           if(prefs.getBoolean("clicked",true)) {


               longClickBar.setVisibility(View.VISIBLE);
           }   }


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
        intent.putExtra("username", username);
        intent.putExtra("otheruserkey", otheruserkey);
        intent.putExtra("mykey", mykey);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);




            if (requestCode == MULTIPLE_IMAGE_SELECT) {


                final Uri imageUri = data.getData();

                String path=FirebaseChat.getPath(getApplication(),imageUri);
                if (imageUri.toString().contains("image")) {
                    Intent i=new Intent(FirebaseChat.this,AddCaptionChatPhoto.class);
                    i.putExtra("imageUrl",imageUri.toString());
                    i.putExtra("mykey",mykey);
                    i.putExtra("otheruserkey",otheruserkey);
                    startActivity(i);
                    //handle image

            }else{

                   // Bitmap bmThumbnail= ThumbnailUtils.createVideoThumbnail(imageUri.toString(), MediaStore.Images.Thumbnails.MICRO_KIND);
                   //imageview_micro.setImageBitmap(bmThumbnail);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("videoUrl",imageUri.toString());
                    Intent i=new Intent(FirebaseChat.this,AddCaptionChatPhoto.class);
                    i.putExtra("imageUrl",imageUri.toString());
                    i.putExtra("path",path);
                    i.putExtra("mykey",mykey);
                    i.putExtra("otheruserkey",otheruserkey);
                    startActivity(i);


// MINI_KIND, size: 512 x 384 thumbnail
                   // bmThumbnail = ThumbnailUtils.createVideoThumbnail(imageUri.toString(), MediaStore.Images.Thumbnails.MINI_KIND);
                   // imageview_mini.setImageBitmap(bmThumbnail);

                }




              /**  if(imageUri != null){
                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("chatImages");
                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    byte[] input = baos.toByteArray();
                    UploadTask uploadTask = filepath.putBytes(input);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            if (downloadUrl.toString().length() > 0) {
                                //textbox.setText("");
                                Message newMessage = new Message();
                                newMessage.text = downloadUrl.toString();
                                newMessage.idSender = mykey;
                                newMessage.idReceiver = otheruserkey;
                                newMessage.timestamp = System.currentTimeMillis();

                                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                            }


                            finish();
                            return;
                        }
                    });
                }else{
                    finish();
                }*/
                //Log.d(TAG, "multiple image select data" + data.getData());

              /**  String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<>();
                multipleImagesPathList = new ArrayList<>();
                if (data.getClipData() == null) {

                    Uri mImageUri = data.getData();
                   // Log.d(TAG, "mimageuri " + mImageUri);
                    // Get the cursor
                    Intent intent = new Intent(this, ShowPhotoActivity.class);
                    intent.putExtra("path1", UriHelper.getPath(FirebaseChat.this, mImageUri));
                    intent.putExtra("username", otherusername);
                    intent.putExtra("otheruserkey", otheruserkey);
                    intent.putExtra("mykey", mykey);
                    startActivity(intent);
                    // Log.d(TAG, imageEncoded + " imageencoded");
                }*/

            }

            else if(requestCode==PICK_DOCS)
            {
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();

                    File myFile = new File(uriString);

                    final String path = myFile.getAbsolutePath();
                   //path=path.substring(path.lastIndexOf("/")+1);




                    if (path.length() > 0) {
                        //textbox.setText("");
                        Message newMessage = new Message();
                        newMessage.text = path;
                        newMessage.idSender = mykey;
                        newMessage.idReceiver = otheruserkey;
                        newMessage.timestamp = System.currentTimeMillis();

                        FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
                    }


                    //String path = data.getStringExtra("path");
                    Toast.makeText(FirebaseChat.this, path, Toast.LENGTH_SHORT).show();

                   // Uri file = Uri.fromFile(new File(path));
                   /** StorageReference riversRef = FirebaseStorage.getInstance().getReference().child("doc/"+file.getLastPathSegment());
                    //UploadTask uploadTask = riversRef.putFile(file);
                    UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.getValue() != null) {
                                        mapMessage = (HashMap) dataSnapshot.getValue();
                                        newMessage = new Message();
                                        newMessage.idSender = (String) mapMessage.get("idSender");
                                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                                        newMessage.text = (String) mapMessage.get(downloadUrl);
                                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                                        consersation.getListMessageData().add(newMessage);
                                        adapter.notifyDataSetChanged();
                                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
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
                            recyclerChat.setAdapter(adapter);
                        }
                    });
                  Toast.makeText(FirebaseChat.this,file.getLastPathSegment(),Toast.LENGTH_LONG).show();
                    Log.e("data",path);*/
                }else{
                    Log.e("data","cance");
                }
            }
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


    /**@Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase.getInstance().getReference().child("message").child("null").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // if(dataSnapshot.child("Status").getValue().toString().equals("notseen"))

                    FirebaseDatabase.getInstance().getReference().child("message").child("null").child("Status").setValue("seen");

                    //  ((ItemMessageUserHolder) holder).tick.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
              //  }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/




    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
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
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }




    public String mykey()
    {
        return mykey;
    }
    public String otheruserkey()
    {
        return otheruserkey;
    }


}




    class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{

        private static final int REQUEST_CODE = 999;
        FirebaseChat c=new FirebaseChat();

        boolean click=false;
        String uri;

        UserSession u=new UserSession(getApplicationContext());
        String key=u.getUserKey();
      //  Bundle extras =((Activity)getApplicationContext()).getIntent().getExtras();


            String otheruserkey;
            //The key argument here must match that used in your adapter

    //    Intent intentData =((Activity) getApplicationContext()).getIntent();

      //  String otheruserkey = intentData.getStringExtra("otheruserkey");
        private Context context;
        private Consersation consersation;

        public ListMessageAdapter(Context context, Consersation consersation,String otherusrkey) {
            this.context = context;
            this.consersation = consersation;


            this.otheruserkey=otherusrkey;

        }
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == FirebaseChat.VIEW_TYPE_FRIEND_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
                return new ItemMessageFriendHolder(view);
            } else if (viewType == FirebaseChat.VIEW_TYPE_USER_MESSAGE) {
                View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
                return new ItemMessageUserHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {



            if (holder instanceof ItemMessageFriendHolder) {
                //((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                if(consersation.getListMessageData().get(position).text.startsWith("/file"))
                {
                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    //((ItemMessageFriendHolder) holder).tick.setVisibility(View.GONE);

                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).docText.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("/")+1));

                    ((ItemMessageFriendHolder) holder).docLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File targetFile = new File(consersation.getListMessageData().get(position).text.substring(6));
                            Uri targetUri = Uri.fromFile(targetFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(targetUri, "text/plain");
                            context.startActivity(intent);
                        }
                    });
                }
                else if(consersation.getListMessageData().get(position).text.startsWith("Name")){

                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                  //  ((ItemMessageFriendHolder) holder).tick.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).conLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).conName.setText(consersation.getListMessageData().get(position).text.substring(4,consersation.getListMessageData().get(position).text.lastIndexOf("r")-5));
                    ((ItemMessageFriendHolder) holder).conNumber.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("r")+1));
                }


                else if(consersation.getListMessageData().get(position).text.contains("chatImages")){
                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.VISIBLE);


                    Glide.with(context).load(consersation.getListMessageData().get(position).text).error(R.drawable.nouser).into(((ItemMessageFriendHolder) holder).imageChat);

                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                    //((ItemMessageFriendHolder) holder).tick.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.GONE);
                    if(consersation.getListMessageData().get(position).text.contains("caption"))
                        ((ItemMessageFriendHolder) holder).caption.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("caption")+7));
                    else
                        ((ItemMessageFriendHolder) holder).caption.setVisibility(View.GONE);

                    ((ItemMessageFriendHolder) holder).chatLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i=new Intent(getApplicationContext(),OpenChatPhoto.class);
                            i.putExtra("image",consersation.getListMessageData().get(position).text);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(i);


                        }
                    });


                }
                else if(consersation.getListMessageData().get(position).text.startsWith("Latitude")){

                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);
                  //  ((ItemMessageFriendHolder) holder).tick.setVisibility(View.GONE);
                    //((ItemMessageUserHolder) holder).txtContent.setText("Location");
                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).mapLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).mapLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //  String latitude=consersation.getListMessageData().get(position).text;
                            // String longitude=consersation.getListMessageData().get(position).text;
                            String latitude=consersation.getListMessageData().get(position).text.substring(8,consersation.getListMessageData().get(position).text.lastIndexOf("Longitude")-1);
                            String longitude=consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("Longitude")+9);
                            // Toast.makeText(context, latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(latitude), Double.valueOf(longitude));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }

                else if(consersation.getListMessageData().get(position).text.contains("Videos"))
                {

                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.GONE);

                    //((ItemMessageUserHolder) holder).txtContent.setText("Location");
                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).mapLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).videoLayout.setVisibility(View.VISIBLE);
                    String url=consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("2F")+2,consersation.getListMessageData().get(position).text.lastIndexOf("2F")+7)+":"+consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("3A")+2,consersation.getListMessageData().get(position).text.lastIndexOf("3A")+8);




                    final StorageReference  islandRef = FirebaseStorage.getInstance().getReference().child("Videos/"+url);
                    final File storagePath = new File(Environment.getExternalStorageDirectory(), "Toado/Videos");
                    final File myFile = new File(storagePath,url+".mp4");

                    if(!myFile.exists()) {

                        ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.VISIBLE);
                    }else {
                        ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.GONE);
                        ((ItemMessageFriendHolder) holder).videoProgress.setVisibility(View.GONE);
                        ((ItemMessageFriendHolder) holder).vThumb.setVisibility(View.VISIBLE);
                        BitmapPool bitmapPool = Glide.get(getApplicationContext()).getBitmapPool();
                        int microSecond = 6000000;// 6th second as an example
                        VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                        FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                        Glide.with(getApplicationContext())
                                .load(myFile.getAbsolutePath())
                                .asBitmap()
                                // .override(50,50)// Example
                                .videoDecoder(fileDescriptorBitmapDecoder)
                                .into(((ItemMessageFriendHolder) holder).chatVideo);
                    }

                    ((ItemMessageFriendHolder) holder).videoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {



                            if(!storagePath.exists()) {
                                storagePath.mkdirs();
                            }


                            if(!myFile.exists())
                            {

                                ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.VISIBLE);


                                ((ItemMessageFriendHolder) holder).vDownload.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        islandRef.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).videoProgress.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).vThumb.setVisibility(View.VISIBLE);


                                                BitmapPool bitmapPool = Glide.get(getApplicationContext()).getBitmapPool();
                                                int microSecond = 6000000;// 6th second as an example
                                                VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                                                FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                                                Glide.with(getApplicationContext())
                                                        .load(myFile.getAbsolutePath())
                                                        .asBitmap()
                                                        // .override(50,50)// Example
                                                        .videoDecoder(fileDescriptorBitmapDecoder)
                                                        .into(((ItemMessageFriendHolder) holder).chatVideo);
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

                                                ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.GONE);
                                                ((ItemMessageFriendHolder) holder).videoProgress.setVisibility(View.VISIBLE);
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
                                ((ItemMessageFriendHolder) holder).vDownload.setVisibility(View.GONE);
                                ((ItemMessageFriendHolder) holder).videoProgress.setVisibility(View.GONE);
                                ((ItemMessageFriendHolder) holder).vThumb.setVisibility(View.VISIBLE);
                                Intent tostart = new Intent(Intent.ACTION_VIEW);
                                //tostart.setDataAndType(Uri.fromFile(new File(myFile.getAbsolutePath())),"video/mp4");
                                tostart.setDataAndType(Uri.fromFile(new File(myFile.getAbsolutePath())),"video/mp4");
                                tostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(tostart);


                            }


                         /**   uri=consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("videoUrl")+8);

                            Intent tostart = new Intent(Intent.ACTION_VIEW);
                            tostart.setDataAndType(Uri.parse(uri), "video/*");
                            tostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(tostart);*/
                        }
                    });





                }

                else {
                    ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.VISIBLE);
                   // ((ItemMessageUserHolder) holder).tick.setVisibility(View.VISIBLE);
                    ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                    ((ItemMessageFriendHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageFriendHolder) holder).chatLayout.setVisibility(View.GONE);



                }

            }

            else if (holder instanceof ItemMessageUserHolder) {
                String text=(consersation.getListMessageData().get(position).text);


                if(consersation.getListMessageData().get(position).text.startsWith("/file"))
                {
                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).docText.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("/")+1));

                    ((ItemMessageUserHolder) holder).docLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File targetFile = new File(consersation.getListMessageData().get(position).text.substring(6));
                            Uri targetUri = Uri.fromFile(targetFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(targetUri, "text/plain");
                           context.startActivity(intent);
                        }
                    });
                }

                else if(consersation.getListMessageData().get(position).text.startsWith("Name")){

                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).conLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).conName.setText(consersation.getListMessageData().get(position).text.substring(4,consersation.getListMessageData().get(position).text.lastIndexOf("r")-5));
                    ((ItemMessageUserHolder) holder).conNumber.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("r")+1));
                }


              else if(consersation.getListMessageData().get(position).text.contains("chatImages")){

                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.VISIBLE);


                        Glide.with(context).load(consersation.getListMessageData().get(position).text).error(R.drawable.nouser).into(((ItemMessageUserHolder) holder).imageChat);
                    if(consersation.getListMessageData().get(position).text.contains("caption"))
                    ((ItemMessageUserHolder) holder).caption.setText(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("caption")+7));
                    else
                        ((ItemMessageUserHolder) holder).caption.setVisibility(View.GONE);

                    if(isNetworkAvailable())
                    {


                        ((ItemMessageUserHolder) holder).tickImage.setImageResource( R.drawable.ic_check_black_24dp);


                        if(! ((ItemMessageUserHolder) holder).tickImage.getDrawable().equals(R.drawable.ic_check_black_24dp))
                        {
                            ((ItemMessageUserHolder) holder).tickImage.setImageResource( R.drawable.ic_check_black_24dp);
                        }
                    }
                    if(!isNetworkAvailable()){

                        ((ItemMessageUserHolder) holder).tickImage.setVisibility(View.GONE);
                    }



                    FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists())
                            {
                                if(dataSnapshot.child("online").getValue().toString().equals("true"))
                                {
                                    ((ItemMessageUserHolder) holder).tickImage.setImageResource( R.drawable.ic_msg_recieve_black_24dp);
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
                    ((ItemMessageUserHolder) holder).chatLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i=new Intent(getApplicationContext(),OpenChatPhoto.class);
                            i.putExtra("image",consersation.getListMessageData().get(position).text);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(i);


                        }
                    });

                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.GONE);
                }
                else if(consersation.getListMessageData().get(position).text.startsWith("Latitude")){

                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                    //((ItemMessageUserHolder) holder).txtContent.setText("Location");
                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).mapLayout.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).mapLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  String latitude=consersation.getListMessageData().get(position).text;
                           // String longitude=consersation.getListMessageData().get(position).text;
                           String latitude=consersation.getListMessageData().get(position).text.substring(8,consersation.getListMessageData().get(position).text.lastIndexOf("Longitude")-1);
                            String longitude=consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("Longitude")+9);
                           // Toast.makeText(context, latitude+"\n"+longitude, Toast.LENGTH_SHORT).show();
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(latitude), Double.valueOf(longitude));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }
                else if(consersation.getListMessageData().get(position).text.contains("Videos"))
                {

                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                    //((ItemMessageUserHolder) holder).txtContent.setText("Location");
                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).mapLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.VISIBLE);


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String thumb=prefs.getString("videoUrl","");
                    BitmapPool bitmapPool = Glide.get(getApplicationContext()).getBitmapPool();
                    int microSecond = 6000000;// 6th second as an example
                    VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
                    FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
                    Glide.with(getApplicationContext())
                            .load(consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("videoUrl")+8))
                            .asBitmap()
                            // .override(50,50)// Example
                            .videoDecoder(fileDescriptorBitmapDecoder)
                            .into(((ItemMessageUserHolder) holder).chatVideo);
                    ((ItemMessageUserHolder) holder).videoLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                             uri=consersation.getListMessageData().get(position).text.substring(consersation.getListMessageData().get(position).text.lastIndexOf("videoUrl")+8);

                            Intent tostart = new Intent(Intent.ACTION_VIEW);
                            tostart.setDataAndType(Uri.parse(uri), "video/*");
                            tostart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(tostart);
                        }
                    });

                    if(isNetworkAvailable())
                    {


                        ((ItemMessageUserHolder) holder).tickVideo.setImageResource( R.drawable.ic_check_black_24dp);


                        if(! ((ItemMessageUserHolder) holder).tickVideo.getDrawable().equals(R.drawable.ic_check_black_24dp))
                        {
                            ((ItemMessageUserHolder) holder).tickVideo.setImageResource( R.drawable.ic_check_black_24dp);
                        }
                    }
                    if(!isNetworkAvailable()){

                        ((ItemMessageUserHolder) holder).tickVideo.setVisibility(View.GONE);
                    }



                    FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.exists())
                            {
                                if(dataSnapshot.child("online").getValue().toString().equals("true"))
                                {
                                    ((ItemMessageUserHolder) holder).tickVideo.setImageResource( R.drawable.ic_msg_recieve_black_24dp);
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

                else {
                    ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.VISIBLE);
                    ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
                    ((ItemMessageUserHolder) holder).docLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).chatLayout.setVisibility(View.GONE);
                    ((ItemMessageUserHolder) holder).videoLayout.setVisibility(View.GONE);


                    ((ItemMessageUserHolder) holder).txtContent.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Select any option");

// add a list
                            String[] animals = {"Copy", "Delete", "Forward"};
                            builder.setItems(animals, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            ClipboardManager _clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                            _clipboard.setText(consersation.getListMessageData().get(position).text);
                                            Toast.makeText(context, "Message Copied", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 1:

                                           //FirebaseDatabase.getInstance().getReference().child("message").limitToFirst(consersation.getListMessageData().get(position)).child("null").child(consersation.getListMessageData().get(position).toString()).removeValue();
                                            Toast.makeText(context,"Message Deleted", Toast.LENGTH_SHORT).show();
                                            break;

                                        case 2:
                                            Intent i=new Intent(getApplicationContext(), ForwardChatMessage.class);
                                            i.putExtra("msg",consersation.getListMessageData().get(position).text);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            getApplicationContext().startActivity(i);
                                            break;
                                    }
                                }
                            });

// create and show the alert dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                          /**  click=true;

                            ((ItemMessageUserHolder) holder).textlayout.setBackgroundColor(Color.LTGRAY);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = prefs.edit();

                       editor.putBoolean("clicked",click);




                           // editor.commit();
                            editor.apply();
                            //editor.notifyAll();*/

                            return true;
                        }
                    });
                }
                if(isNetworkAvailable())
                {


                    ((ItemMessageUserHolder) holder).tick.setImageResource( R.drawable.ic_check_black_24dp);


                    if(! ((ItemMessageUserHolder) holder).tick.getDrawable().equals(R.drawable.ic_check_black_24dp))
                    {
                        ((ItemMessageUserHolder) holder).tick.setImageResource( R.drawable.ic_check_black_24dp);
                    }
                }
               if(!isNetworkAvailable()){

                    ((ItemMessageUserHolder) holder).tick.setVisibility(View.GONE);
                }



             FirebaseDatabase.getInstance().getReference().child("Users").child("Usersession").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                 @Override
                 public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                     if(dataSnapshot.exists())
                     {
                         if(dataSnapshot.child("online").getValue().toString().equals("true"))
                         {
                             ((ItemMessageUserHolder) holder).tick.setImageResource( R.drawable.ic_msg_recieve_black_24dp);
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




        }

        @Override
        public int getItemViewType(int position) {
            return consersation.getListMessageData().get(position).idSender.equals(key) ? FirebaseChat.VIEW_TYPE_USER_MESSAGE : FirebaseChat.VIEW_TYPE_FRIEND_MESSAGE;

        }

        @Override
        public int getItemCount() {
            return consersation.getListMessageData().size();

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            switch (requestCode) {
                case REQUEST_CODE: {

                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }







    }



class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    ImageView tick,tickImage,tickVideo;
    CoordinatorLayout docLayout,chatLayout,conLayout,mapLayout,videoLayout;
    ImageView docImage,imageChat;
    TextView docText;
    LinearLayout textlayout;
    ImageView conImage;
    TextView conName,conNumber,caption;
    ImageView chatMap,chatVideo;





    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);
        tick=(ImageView)itemView.findViewById(R.id.tick);
        docLayout=(CoordinatorLayout)itemView.findViewById(R.id.docLayout);
        docImage=(ImageView)itemView.findViewById(R.id.docImage);
        docText=(TextView)itemView.findViewById(R.id.docName);
        imageChat=(ImageView)itemView.findViewById(R.id.imgchat);
        chatLayout=(CoordinatorLayout)itemView.findViewById(R.id.imgchatrel);
        tickImage=(ImageView)itemView.findViewById(R.id.tickImage);
        textlayout=(LinearLayout)itemView.findViewById(R.id.textLayout);
        conLayout=(CoordinatorLayout)itemView.findViewById(R.id.conLayout);
        conImage=(ImageView)itemView.findViewById(R.id.conImage);
        conName=(TextView)itemView.findViewById(R.id.conName);
        conNumber=(TextView)itemView.findViewById(R.id.conNumber);
        caption=(TextView)itemView.findViewById(R.id.caption);
        mapLayout=(CoordinatorLayout)itemView.findViewById(R.id.mapLayout);
        chatMap=(ImageView)itemView.findViewById(R.id.chatMap);
        videoLayout=(CoordinatorLayout)itemView.findViewById(R.id.videoLayout);
        chatVideo=(ImageView)itemView.findViewById(R.id.chatVideo);
        tickVideo=(ImageView)itemView.findViewById(R.id.tickVideo);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;

    CoordinatorLayout docLayout,chatLayout,conLayout,mapLayout,videoLayout;
    ImageView docImage,imageChat;
    TextView docText;

    ImageView conImage,chatVideo;
    TextView conName,conNumber,caption;
    ImageView chatMap;
    ImageView vDownload,vThumb;
    ProgressBar videoProgress;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);
        docLayout=(CoordinatorLayout)itemView.findViewById(R.id.docLayout);
        docImage=(ImageView)itemView.findViewById(R.id.docImage);
        docText=(TextView)itemView.findViewById(R.id.docName);
        imageChat=(ImageView)itemView.findViewById(R.id.imgchat);
        chatLayout=(CoordinatorLayout)itemView.findViewById(R.id.imgchatrel);
        conLayout=(CoordinatorLayout)itemView.findViewById(R.id.conLayout);
        conImage=(ImageView)itemView.findViewById(R.id.conImage);
        conName=(TextView)itemView.findViewById(R.id.conName);
        conNumber=(TextView)itemView.findViewById(R.id.conNumber);
        mapLayout=(CoordinatorLayout)itemView.findViewById(R.id.mapLayout);
        chatMap=(ImageView)itemView.findViewById(R.id.chatMap);
        caption=(TextView)itemView.findViewById(R.id.caption);
        videoLayout=(CoordinatorLayout)itemView.findViewById(R.id.videoLayout);
        chatVideo=(ImageView)itemView.findViewById(R.id.chatVideo);
        vDownload=(ImageView)itemView.findViewById(R.id.vDownload);
        videoProgress=(ProgressBar) itemView.findViewById(R.id.videoProgress);
        vThumb=(ImageView)itemView.findViewById(R.id.vThumb);

    }
}