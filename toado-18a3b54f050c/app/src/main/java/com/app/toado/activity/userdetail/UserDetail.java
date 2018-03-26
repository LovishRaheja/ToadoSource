package com.app.toado.activity.userdetail;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.SharedItems.SharedItemsActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.activity.register.OtpAct;
import com.app.toado.helper.MyXMPP2;
import com.app.toado.model.MobileKey;
import com.app.toado.services.LocServ;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by aksha on 9/8/2017.
 */

public class UserDetail extends AppCompatActivity {

    private String mUserName;
    private String mUserKey;
    private String mUserPicture;
    private ImageView sendProfile;

    private ImageView mUserImageVIew;
    private TextView mUserNameTextView;
    private ImageView mSettingsButton;

    private RelativeLayout media_title_container;
    ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detial);

        mUserName = getIntent().getStringExtra("UserName");
        mUserKey = getIntent().getStringExtra("UserKey");
        mUserPicture = getIntent().getStringExtra("UserPicture");

        sendProfile=(ImageView)findViewById(R.id.sendProfile);

        mUserImageVIew = (ImageView) findViewById(R.id.user_detail_image);
        mUserNameTextView = (TextView) findViewById(R.id.user_detail_name);

        back=(ImageView)findViewById(R.id.imgback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Glide.with(getApplicationContext()).load(mUserPicture).into(mUserImageVIew);
        mUserNameTextView.setText(mUserName);

        media_title_container = (RelativeLayout) findViewById(R.id.media_title_container);
        media_title_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SharedItemsActivity.class);
                intent.putExtra("OtherUserKey", mUserKey);
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
}
