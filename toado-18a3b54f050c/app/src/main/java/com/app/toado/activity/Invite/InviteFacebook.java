package com.app.toado.activity.Invite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.app.toado.R;
import com.app.toado.activity.BlockedUsers.BlockedUsers;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;

public class InviteFacebook extends AppCompatActivity {

    ImageView img1,img2,img3,img4,img5,img6,img7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_facebook);
        img1=(ImageView)findViewById(R.id.img1);
        img2=(ImageView)findViewById(R.id.img2);
        img3=(ImageView)findViewById(R.id.img3);
        img4=(ImageView)findViewById(R.id.img4);
        img5=(ImageView)findViewById(R.id.img5);
        img6=(ImageView)findViewById(R.id.img6);
        img7=(ImageView)findViewById(R.id.img7);

        Glide.with(InviteFacebook.this).load(R.drawable.img1).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img1);

        Glide.with(InviteFacebook.this).load(R.drawable.img2).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img2);

        Glide.with(InviteFacebook.this).load(R.drawable.img3).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img3);

        Glide.with(InviteFacebook.this).load(R.drawable.img4).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img4);

        Glide.with(InviteFacebook.this).load(R.drawable.img5).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img5);

        Glide.with(InviteFacebook.this).load(R.drawable.img6).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img6);

        Glide.with(InviteFacebook.this).load(R.drawable.img7).dontAnimate().transform(new CircleTransform(InviteFacebook.this)).into(img7);
    }
}
