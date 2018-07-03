package com.app.toado.activity.AppSettings;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.toado.FirebaseChat.ForwardChatMessage;
import com.app.toado.R;
import com.app.toado.helper.MarshmallowPermissions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AppSettings extends AppCompatActivity {
    RelativeLayout changeWallpaper;
    ImageView back;
    MarshmallowPermissions marshper;
    String imguri = "nil";
    private static final String[] STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };

    RelativeLayout chooseSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        back=(ImageView)findViewById(R.id.back);
        marshper = new MarshmallowPermissions(this);
        marshper.reguestNewPermissions(this, STORAGE_PERMISSION);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });
        changeWallpaper=(RelativeLayout)findViewById(R.id.changeWallpaper);
        chooseSound=(RelativeLayout)findViewById(R.id.chooseSound);

        chooseSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);

                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                startActivityForResult(intent, 5);
            }
        });

        changeWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AppSettings.this);
                builder.setTitle("Select any option");

// add a list
                String[] animals = {"Default Wallpaper", "Select Wallpaper"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SharedPreferences preferences =
                                        PreferenceManager.getDefaultSharedPreferences(AppSettings.this);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("image","image");
                                editor.commit();
                                Toast.makeText(AppSettings.this, "wallpaper Set", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                if (marshper.reguestNewPermissions(AppSettings.this, STORAGE_PERMISSION).matches("pernotreq")) {
                                    System.out.println("permission not required imagepicker personaldetact");
                                    CropImage.activity()
                                            .setGuidelines(CropImageView.Guidelines.ON)
                                            .setMultiTouchEnabled(true)
                                            .start(AppSettings.this);
                                }

                                break;


                        }
                    }
                });

// create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }

    void dialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_layout, null);




        AlertDialog.Builder alert = new AlertDialog.Builder(AppSettings.this);
        alert.setTitle("Info");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

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
        if(resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();

            }

        }

        if (resultCode == RESULT_OK) {
            imguri = result.getUri().toString();

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("image", result.getUri().getPath());
            editor.commit();
            Toast.makeText(this, "wallpaper Set", Toast.LENGTH_SHORT).show();

            System.out.println("image cropped uri personaldetailsact" + imguri);


        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            System.out.println("image pick failed");
            imguri = "nil";
        }
    }
}
