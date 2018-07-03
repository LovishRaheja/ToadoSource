package com.app.toado.activity.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.register.PersonalDetailsAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.helper.RegisterProfileFirebase;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

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
    TextView dob;
    EditText name,interests;
    ImageView editIntersts;
    private Bitmap mBitmap;
    ProgressDialog pd;
    UserSession usess;
    String imguri = "nil";
    ImageView editImage;
    Button saveProfile;
    private String downloadUrl;
    ImageView back;
    MarshmallowPermissions marshper;
    private static final String[] STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setTitle("Profile");
        pd = new ProgressDialog(this);
        mContext = getApplicationContext();
        mResources = getResources();
        marshper = new MarshmallowPermissions(this);
        mProfile=(ImageView)findViewById(R.id.profile);
        name=(EditText) findViewById(R.id.name);
        dob=(TextView)findViewById(R.id.dob);
        interests=(EditText) findViewById(R.id.interests);
        editImage=(ImageView)findViewById(R.id.editImage);
        saveProfile=(Button)findViewById(R.id.saveProfile);
        back=(ImageView)findViewById(R.id.back);
        editIntersts=(ImageView)findViewById(R.id.editIntersts);
        editIntersts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.setEnabled(true);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marshper.reguestNewPermissions(ProfileAct.this, STORAGE_PERMISSION).matches("pernotreq")) {
                    System.out.println("permission not required imagepicker personaldetact");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMultiTouchEnabled(true)
                            .start(ProfileAct.this);
                }
            }
        });




usess = new UserSession(this);

        DBREF_USER_PROFILES.child(usess.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                    String imgurl = u.getProfpicurl();


                    name.setText(u.getName());

                    int age;
                    final Calendar calenderToday = Calendar.getInstance();
                    int currentYear = calenderToday.get(Calendar.YEAR);
                    int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
                    int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

                    age = currentYear - Integer.valueOf(u.getDob().substring(u.getDob().lastIndexOf("/")+1));
                    dob.setText(String.valueOf(age));
                    interests.setText(u.getUserLikes());

                   // phone.setText(u.getPhone());
                    Glide.with(mContext).load(imgurl).dontAnimate()
                            .transform(new CircleTransform(mContext)).error(R.drawable.ic_person_black_24dp).into(mProfile);


                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProgressDialog("show");
                String n = name.getText().toString().trim();
                String i=interests.getText().toString().trim();
                if(n.matches("")){
                    handleProgressDialog("hide");
                    Toast.makeText(ProfileAct.this, "Please enter your name to continue", Toast.LENGTH_SHORT).show();
                }
                else if(i.matches("")){
                    handleProgressDialog("hide");
                    Toast.makeText(ProfileAct.this, "Please enter your interests to continue", Toast.LENGTH_SHORT).show();
                }
                else {
                    switch (imguri) {
                        case "nil":
                            System.out.println("image uri is nil");

                            changeName(n,i);
                           // RegisterProfileFirebase.saveDataToFirebase(name, mPhone, gender, dob, interest, "nil",mykey,sharedpref,userSettingsSharedPref,PersonalDetailsAct.this);
                            break;
                        default:
                            //changeName(n);
                            uploadImage(n,i,Uri.parse(imguri));
                            break;
                    }
                }
            }
        });

    }

    private void uploadImage(final String name,final String interests, Uri uri) {

        StorageReference profilepicref = FirebaseStorage.getInstance().getReference().child("profileImages").child(usess.getUserKey()).child(uri.getLastPathSegment());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilepicref.putBytes(data);

        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        System.out.println("file upload successfulregister user pdetailsact" + downloadUrl);
                        DBREF_USER_PROFILES.child(usess.getUserKey()).child("name").setValue(name);
                        DBREF_USER_PROFILES.child(usess.getUserKey()).child("profpicurl").setValue(downloadUrl.toString());
                        DBREF_USER_PROFILES.child(usess.getUserKey()).child("userLikes").setValue(interests);

                        handleProgressDialog("hide");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        downloadUrl = "nil";
                        System.out.println("failed to upload image to fireabse" + downloadUrl);
                        DBREF_USER_PROFILES.child(usess.getUserKey()).child("name").setValue(name);
                        DBREF_USER_PROFILES.child(usess.getUserKey()).child("userLikes").setValue(interests);
                        handleProgressDialog("hide");
                        Toast.makeText(ProfileAct.this, "Image Not uploaded", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void changeName(String name,final String interests) {

        DBREF_USER_PROFILES.child(usess.getUserKey()).child("name").setValue(name);
        DBREF_USER_PROFILES.child(usess.getUserKey()).child("userLikes").setValue(interests);
        handleProgressDialog("hide");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println(" on request permission result persdetact");
//        imgMeth();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imguri = result.getUri().toString();

            System.out.println("image cropped uri personaldetailsact" + imguri);
            Glide.with(this).load(new File(result.getUri().getPath())).dontAnimate()
                    .transform(new CircleTransform(mContext)).into(mProfile);

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            System.out.println("image pick failed");
            imguri = "nil";
        }
    }
    private void handleProgressDialog(String s) {
        pd.setMessage("Saving Profile Information. Please Wait....");
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

}
