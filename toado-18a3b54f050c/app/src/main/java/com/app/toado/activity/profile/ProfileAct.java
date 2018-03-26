package com.app.toado.activity.profile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.helper.CircleTransform;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 12-02-2018.
 */

public class ProfileAct extends AppCompatActivity {

    private Context mContext;
    private Resources mResources;
    private RelativeLayout mRelativeLayout;
    private Button mButton;
    private ImageView mProfile;
    TextView name,dob,interests;
    private Bitmap mBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Profile");
        mContext = getApplicationContext();
        mResources = getResources();
        mProfile=(ImageView)findViewById(R.id.profile);
        name=(TextView)findViewById(R.id.name);
        dob=(TextView)findViewById(R.id.dob);
        interests=(TextView)findViewById(R.id.interests);


        UserSession usess = new UserSession(this);

        DBREF_USER_PROFILES.child(usess.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                    String imgurl = u.getProfpicurl();


                    name.setText(u.getName());
                    dob.setText(u.getDob());
                    interests.setText(u.getUserLikes());

                   // phone.setText(u.getPhone());
                    Glide.with(mContext).load(imgurl).error(R.drawable.ic_person_black_24dp).load(imgurl).into(mProfile);

                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
