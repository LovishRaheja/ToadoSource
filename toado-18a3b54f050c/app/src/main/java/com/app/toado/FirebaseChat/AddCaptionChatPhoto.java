package com.app.toado.FirebaseChat;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jivesoftware.smackx.bytestreams.ibb.packet.Open;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class AddCaptionChatPhoto extends AppCompatActivity {

    EditText editCaption;
    FloatingActionButton send;
    private String roomId;
    ImageView addImage;
    Bitmap bmThumbnail;
    ProgressDialog progressDialog;
    ImageView vThumb;
    DatabaseReference mDatabaseUser, mDatabaseChat;
    UserSession session;
     String chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_caption_chat_photo);

        session = new UserSession(this);

        editCaption=(EditText) findViewById(R.id.editcaption);
        addImage=(ImageView)findViewById(R.id.addImage);
        final String imageUrl,mykey,otheruserkey,path;
        Intent intentData = getIntent();
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        mykey=getIntent().getStringExtra("mykey");
        otheruserkey=getIntent().getStringExtra("otheruserkey");
        imageUrl=getIntent().getStringExtra("imageUrl");
        path=getIntent().getStringExtra("path");
        final Uri myUri = Uri.parse(imageUrl);
        send=(FloatingActionButton)findViewById(R.id.send);
        vThumb=(ImageView)findViewById(R.id.vThumb);
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
       // getChatId();
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
        progressDialog = new ProgressDialog(AddCaptionChatPhoto.this);


        if(imageUrl.toString().contains("image"))
        {
            Glide.with(AddCaptionChatPhoto.this).load(imageUrl).error(R.drawable.nouser).into(addImage);
           // Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show();
            vThumb.setVisibility(View.GONE);
        }
        else if(imageUrl.toString().contains("sent"))
        {
            Glide.with(AddCaptionChatPhoto.this).load(imageUrl).error(R.drawable.nouser).into(addImage);
            vThumb.setVisibility(View.GONE);

        }
        else {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getApplication().getContentResolver().query(myUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
          //  bmThumbnail = ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MICRO_KIND);
           // Glide.with(AddCaptionChatPhoto.this).load("file:///"+ myUri ) .into( addImage );
            BitmapPool bitmapPool = Glide.get(getApplication()).getBitmapPool();
            int microSecond = 6000000;// 6th second as an example
            VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
            FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
            Glide.with(getApplication())
                    .load(imageUrl)
                    .asBitmap()
                   // .override(50,50)// Example
                    .videoDecoder(fileDescriptorBitmapDecoder)
                    .into(addImage);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myUri != null){
                    if(imageUrl.toString().contains("image") | imageUrl.toString().contains("sent")) {
                        StorageReference filepath = FirebaseStorage.getInstance().getReference().child("chatImages");
                        Bitmap bitmap = null;

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), myUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] input = baos.toByteArray();
                        UploadTask uploadTask = filepath.putBytes(input);
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.setMessage("Sending Image");
                                progressDialog.show();
                            }
                        });
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


                                if(downloadUrl.toString().length() > 0){
                                    DatabaseReference newMessageDb = mDatabaseChat.push();

                                    Map newMessage = new HashMap();
                                    newMessage.put("createdByUser", session.getUserKey());
                                    if(editCaption.getText().toString().equals(null)) {
                                        newMessage.put("text", downloadUrl.toString());
                                    }else{
                                        newMessage.put("text",downloadUrl.toString()+"caption"+editCaption.getText().toString());
                                    }
                                    newMessage.put("timestamp",System.currentTimeMillis());

                                    newMessageDb.setValue(newMessage);
                                }
                                progressDialog.hide();




                                finish();
                                return;
                            }
                        });
                    }

                    else{

                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        final StorageReference photoRef = storageRef.child("Videos").child(myUri.getLastPathSegment());
                        photoRef.putFile(myUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("sending Video " + ((int) progress) + "%...");
                                progressDialog.show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Upload failed...", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();


                                if(downloadUrl.toString().length() > 0){
                                    DatabaseReference newMessageDb = mDatabaseChat.push();

                                    Map newMessage = new HashMap();
                                    newMessage.put("createdByUser", session.getUserKey());
                                    newMessage.put("text", downloadUrl.toString());
                                    newMessage.put("timestamp",System.currentTimeMillis());

                                    newMessageDb.setValue(newMessage);
                                }


                                Toast.makeText(AddCaptionChatPhoto.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();


                                Intent i=new Intent(AddCaptionChatPhoto.this,FirebaseChat.class);
                                // i.putExtra("imageUrl",imageUrl);
                                // i.putExtra("mykey",mykey);
                                // i.putExtra("otheruserkey",otheruserkey);
                                startActivity(i);
                              //  notificationBuilder.setContentText("Your story is uploaded").setProgress(0,0,false);
                              //  notificationManager.notify(notificationID, notificationBuilder.build());

                            }
                        });
                    }

                }else{
                    finish();
                }
            }
        });

    }




    private void getChatId(){

    }

}
