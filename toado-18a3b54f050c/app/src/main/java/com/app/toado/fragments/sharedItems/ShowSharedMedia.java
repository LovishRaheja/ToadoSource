package com.app.toado.fragments.sharedItems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class ShowSharedMedia extends AppCompatActivity {

    String imageUrl,timestamp;
    String currentuser;
    UserSession session;

    TextView title,time,caption;
    ImageView image,back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shared_media);

        session = new UserSession(this);


        title = (TextView) findViewById(R.id.tvtitle);
        image = (ImageView) findViewById(R.id.image);
        time = (TextView) findViewById(R.id.timestamp);
        caption = (TextView) findViewById(R.id.caption);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageUrl = getIntent().getStringExtra("image");
        timestamp = getIntent().getStringExtra("timestamp");

        String today = new SimpleDateFormat("MMM d").format(new Date(System.currentTimeMillis()));
        if (today.equals(new SimpleDateFormat("MMM d").format(new Date(Long.valueOf(timestamp))))) {
            time.setText("Today"+ ", " + new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timestamp))));
        } else
            time.setText(new SimpleDateFormat("MMM d yyyy").format(new Date(Long.valueOf(timestamp))) + ", " + new SimpleDateFormat("KK:mm aa").format(new Date(Long.valueOf(timestamp))));


        if (imageUrl.contains("caption"))
            caption.setText(imageUrl.substring(imageUrl.lastIndexOf("caption") + 7));
        else
            caption.setVisibility(View.GONE);


        String matchId = getIntent().getStringExtra("matchId");
        if (getIntent().getBooleanExtra("user", true)) {
            currentuser = session.getUsername();
            title.setText(currentuser);
        } else {
            DBREF_USER_PROFILES.child(matchId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    title.setText(dataSnapshot.getValue().toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        Picasso.with(this).load(imageUrl).into(image);
    }
}
