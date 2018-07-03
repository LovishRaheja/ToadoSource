package com.app.toado.FirebaseChat;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.ForwardChat.ForwardChat;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

public class OpenChatPhoto extends AppCompatActivity {
    ImageView back,chatImage,delete,forward;
    RelativeLayout topBar;
    TextView caption;
    String chatId;
    UserSession session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_chat_photo);
        back=(ImageView)findViewById(R.id.back);
        chatImage=(ImageView)findViewById(R.id.chatImage);

        forward=(ImageView)findViewById(R.id.forward);
        caption=(TextView)findViewById(R.id.caption);

        session = new UserSession(this);

        delete=(ImageView)findViewById(R.id.deleteImage);
        topBar=(RelativeLayout) findViewById(R.id.topBar);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final String imageUrl;

        imageUrl=getIntent().getStringExtra("image");
        chatId=getIntent().getStringExtra("chatId");


        Glide.with(OpenChatPhoto.this).load(imageUrl).error(R.drawable.nouser).into(chatImage);

        caption.setText(imageUrl.substring(imageUrl.lastIndexOf("caption")+7));
//        PhotoViewAttacher pAttacher;
  //      pAttacher = new PhotoViewAttacher(chatImage);
    //    pAttacher.update();

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(OpenChatPhoto.this, ForwardChat.class);
                i.putExtra("msg",imageUrl);
                i.putExtra("myId",session.getUserKey());
                startActivity(i);


            }
        });
      /**  delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /  Query deleteQuery = DBREF_USER_PROFILES.child();


                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                            deleteSnapshot.getRef().removeValue();



                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });*/


    }
}
