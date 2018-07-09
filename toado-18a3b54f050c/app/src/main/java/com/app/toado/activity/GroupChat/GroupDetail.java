package com.app.toado.activity.GroupChat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.TinderChat.Matches.MatchesAdapter;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.activity.GroupChat.GroupDetailRecycler.GroupDetailAdapter;
import com.app.toado.activity.GroupChat.GroupDetailRecycler.GroupDetailObject;
import com.app.toado.activity.profile.EditGroupName;
import com.app.toado.activity.profile.ProfileAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class GroupDetail extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    String groupId;
    TextView groupTitle;
    ImageView groupImage;
    ImageView back;
    TextView recyclerCount;
    ImageView editGroup,mSettingsButton;
    String imguri = "nil";
    MarshmallowPermissions marshper;
    ProgressDialog pd;
    private static final String[] STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupId=getIntent().getStringExtra("groupId");
        groupTitle=(TextView)findViewById(R.id.user_detail_name);
        groupImage=(ImageView)findViewById(R.id.user_detail_image);

        editGroup=(ImageView)findViewById(R.id.editGroup);
        marshper = new MarshmallowPermissions(this);
        pd = new ProgressDialog(this);

        FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupTitle.setText(dataSnapshot.child("name").getValue().toString());
                Glide.with(GroupDetail.this).load(dataSnapshot.child("image").getValue().toString()).error(R.drawable.nouser).into(groupImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerCount=(TextView)findViewById(R.id.recyclerCount);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMembers);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(GroupDetail.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new GroupDetailAdapter(getDataSetMatches(), GroupDetail.this,groupId);
        mRecyclerView.setAdapter(mMatchesAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        back=(ImageView)findViewById(R.id.imgback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getGroupMemberIds();

        editGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GroupDetail.this, EditGroupName.class);
                i.putExtra("groupId",groupId);
                i.putExtra("groupName",groupTitle.getText().toString());
                startActivity(i);
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
                                Drawable mDrawable = groupImage.getDrawable().getCurrent();
                                Bitmap mBitmap = ((GlideBitmapDrawable) mDrawable).getBitmap();

                                String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, groupTitle.getText().toString(), null);
                                Uri uri = Uri.parse(path);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/jpeg");
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(intent, "Share Image"));
                                break;
                            case (R.id.edit):

                                if (marshper.reguestNewPermissions(GroupDetail.this, STORAGE_PERMISSION).matches("pernotreq")) {
                                    System.out.println("permission not required imagepicker personaldetact");
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setMultiTouchEnabled(true)
                                            .start(GroupDetail.this);
                                }
                                break;
                        }
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(GroupDetail.this,GroupChatActivity.class);
        i.putExtra("groupId",groupId);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imguri = result.getUri().toString();

           // System.out.println("image cropped uri personaldetailsact" + imguri);
            Glide.with(this).load(new File(result.getUri().getPath())).into(groupImage);

            StorageReference profilepicref = FirebaseStorage.getInstance().getReference().child("profileImages").child(groupId).child(Uri.parse(imguri).getLastPathSegment());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), Uri.parse(imguri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imgData = baos.toByteArray();
            UploadTask uploadTask = profilepicref.putBytes(imgData);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    handleProgressDialog("show");
                }
            });

            uploadTask
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            downloadUrl = taskSnapshot.getDownloadUrl().toString();
                            FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").child("image").setValue(downloadUrl.toString());
                            Toast.makeText(GroupDetail.this, "Image Successfully updated", Toast.LENGTH_SHORT).show();
                            handleProgressDialog("hide");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            downloadUrl = "nil";

                            handleProgressDialog("hide");
                            Toast.makeText(GroupDetail.this, "Image Not uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            System.out.println("image pick failed");
            imguri = "nil";
        }
    }

    private void handleProgressDialog(String s) {
        pd.setMessage("Uploading Image. Please Wait....");
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        switch (s) {
            case "show":
                pd.show();
                break;
            case "hide":
                pd.cancel();
                break;
        }
    }

    private void getGroupMemberIds() {

        DatabaseReference matchDb =FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("member").getRef();
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    recyclerCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));



                 for(int i=0;i<dataSnapshot.getChildrenCount();i++)
                  {

                      GetMemberId(dataSnapshot.child(String.valueOf(i)).getValue().toString());

                    }
                }

                // String key = matchDb.getKey();
                //  Toast.makeText(MatchesActivity.this, key, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void GetMemberId(String key) {

        DatabaseReference userDb = DBREF_USER_PROFILES.child(key);



        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue()!=null){
                        name = dataSnapshot.child("name").getValue().toString();

                    }
                    if(dataSnapshot.child("profpicurl").getValue()!=null){
                        profileImageUrl = dataSnapshot.child("profpicurl").getValue().toString();
                    }


                    GroupDetailObject obj = new GroupDetailObject(userId, name, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private ArrayList<GroupDetailObject> resultsMatches = new ArrayList<GroupDetailObject>();
    private List<GroupDetailObject> getDataSetMatches() {
        return resultsMatches;
    }

}