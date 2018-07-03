package com.app.toado.activity.GroupChat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.toado.FirebaseChat.Room;
import com.app.toado.FirebaseChat.StaticConfig;
import com.app.toado.R;
import com.app.toado.activity.ToadoContacts.ToadoContacts;
import com.app.toado.activity.main.MainAct;
import com.app.toado.activity.register.PersonalDetailsAct;
import com.app.toado.adapter.AddGroupAdapter;
import com.app.toado.adapter.ToadoAdapter;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.helper.RegisterProfileFirebase;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.model.DistanceUser;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_LOC;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class AddGroup extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<DistanceUser> list;
    ArrayList<String> listId;
    HashMap<String, DistanceUser> hashMap;
    DatabaseReference ref = DBREF_USER_LOC;
    GeoFire geoFire = new GeoFire(ref);
    float distancePref;
    private UserSession session;
    private String userkey;
    UserSettingsSharedPref userSettingsSharedPref;
    ToadoAlerts alr;
    Boolean alertdisp = true;
    int count = 0;
    AlertDialog.Builder builder;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    LinearLayout addGroup;
    EditText groupName;
    private LovelyProgressDialog dialogWait;
    ImageView iconGroup;
    MarshmallowPermissions marshper;
    String imguri = "nil";
    private String downloadUrl;

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
        setContentView(R.layout.activity_add_group);
        marshper = new MarshmallowPermissions(this);
        marshper.reguestNewPermissions(this, STORAGE_PERMISSION);

        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        session = new UserSession(this);

        listIDChoose = new HashSet<>();
        listIDRemove = new HashSet<>();
        listIDChoose.add(session.getUserKey());

        dialogWait = new LovelyProgressDialog(this).setCancelable(false);
        addGroup=(LinearLayout)findViewById(R.id.btnAddGroup);
        groupName=(EditText)findViewById(R.id.editGroupName);

        iconGroup=(ImageView)findViewById(R.id.icon_group);

        iconGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marshper.reguestNewPermissions(AddGroup.this, STORAGE_PERMISSION).matches("pernotreq")) {
                    System.out.println("permission not required imagepicker personaldetact");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMultiTouchEnabled(true)
                            .start(AddGroup.this);
                }
            }
        });


        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listIDChoose.size()<3)
                {
                    Toast.makeText(AddGroup.this, "Please select at least 2 users from the list", Toast.LENGTH_SHORT).show();
                   // createGroup();
                }
                else{
                    if(groupName.getText().length() == 0)
                    {
                        Toast.makeText(AddGroup.this, "Enter group name to proceed", Toast.LENGTH_SHORT).show();
                        //createGroup();
                    }
                    else{
                        createGroup();
                    }
                }
            }
        });


        if (AddGroup.this.getIntent().getStringExtra("mykey") != null) {
            userkey = this.getIntent().getStringExtra("mykey");
            System.out.println("1 homefragment from session" + userkey);
        } else {
            System.out.println("2 homefragment from session" + userkey);
            userkey = session.getUserKey();
        }


        recyclerView = (RecyclerView)findViewById(R.id.recycleListFriend);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        list = new ArrayList<>();
        listId = new ArrayList<>();


        hashMap = new HashMap<>();

        alr = new ToadoAlerts(this);

        mAdapter = new AddGroupAdapter(list, this,addGroup,listIDChoose,listIDRemove);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    public void createGroup()
    {
        dialogWait.setIcon(R.drawable.tab3)
                .setTitle("Registering....")
                .setTopColorRes(R.color.colorPrimary)
                .show();
        final String idGroup = ("Toado" + System.currentTimeMillis()).hashCode() + "";
        final Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }

                Uri uri=Uri.parse(imguri);
        StorageReference profilepicref = FirebaseStorage.getInstance().getReference().child("groupImages").child(session.getUserKey()).child(uri.getLastPathSegment());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = profilepicref.putBytes(data);

        uploadTask
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content



                       // room.groupInfo.put("image",taskSnapshot.getDownloadUrl().toString());
                        room.groupInfo.put("name", groupName.getText().toString());
                        room.groupInfo.put("admin", userkey);

                        downloadUrl = taskSnapshot.getDownloadUrl().toString();


                            room.groupInfo.put("image", downloadUrl);


                            FirebaseDatabase.getInstance().getReference().child("group/" + idGroup).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                addRoomForUser(idGroup, 0);

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        downloadUrl="nil";
                        Toast.makeText(AddGroup.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();



                    }
                });














    }



    private void addRoomForUser(final String roomId, final int userIndex) {
        if (userIndex == listIDChoose.size()) {

                dialogWait.dismiss();
                Toast.makeText(this, "Group Created Successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);

                Intent i=new Intent(this, MainAct.class);
            startActivity(i);
            finish();
            return;


        } else {
            DBREF_USER_PROFILES.child(listIDChoose.toArray()[userIndex] + "/group/" + roomId).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addRoomForUser(roomId, userIndex + 1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogWait.dismiss();
                    new LovelyInfoDialog(AddGroup.this) {
                        @Override
                        public LovelyInfoDialog setConfirmButtonText(String text) {
                            findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dismiss();
                                }
                            });
                            return super.setConfirmButtonText(text);
                        }
                    }
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.tab3)
                            .setTitle("False")
                            .setMessage("Group Not Created")
                            .setCancelable(false)
                            .setConfirmButtonText("Ok")
                            .show();
                }
            });

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            imguri = result.getUri().toString();

            System.out.println("image cropped uri personaldetailsact" + imguri);
            Glide.with(this).load(new File(result.getUri().getPath())).dontAnimate()
                    .transform(new CircleTransform(AddGroup.this)).error(R.drawable.nouser).into(iconGroup);

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            System.out.println("image pick failed");
            imguri = "nil";
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("on resume called homefragment");
        setDistance();
        loadRecyclerViewData();
        mAdapter.notifyDataSetChanged();
        handleNoKeys();
    }


    @Override
    public void onStart() {
        super.onStart();
        setDistance();
        loadRecyclerViewData();
        mAdapter.notifyDataSetChanged();
        handleNoKeys();
    }


    public void setDistance() {
        System.out.println("set distance called homefragment");
        userSettingsSharedPref = new UserSettingsSharedPref(this);
        distancePref = userSettingsSharedPref.getValue() * userSettingsSharedPref.getConversionFactor();//km
        distancePref = Math.round(distancePref);
        System.out.println(userSettingsSharedPref.getConversionFactor() + "distance pref calling loadrecview homefragment" + distancePref);
    }
    private void loadRecyclerViewData() {
        System.out.println("load rv data called homefragment");
        geoFire.getLocation(userkey, new com.firebase.geofire.LocationCallback() {
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("geo fire error homefragment " + databaseError.getDetails());
            }

            @Override
            public void onLocationResult(final String mykey, final GeoLocation mylocation) {
                System.out.println(mykey + " on locresult homefragment" + mylocation);
                if (mylocation != null) {
                    System.out.println("The location for key %s is [%f,%f]" + mykey + mylocation.latitude + mylocation.longitude);
                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mylocation.latitude, mylocation.longitude), distancePref);
                    System.out.println(geoQuery.getRadius() + " geo query new in miles homefragment " + distancePref);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(final String key, final GeoLocation location) {
                            if (!key.equals(mykey)) {
                                DatabaseReference username = DBREF_USER_PROFILES.child(key).getRef();
                                username.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
//                                            System.out.println(dataSnapshot.toString() + "key from homefragment" + key);
                                            mAdapter.notifyDataSetChanged();
                                            callMethod(dataSnapshot, mylocation, location,  key);
                                        } else {
//                                            System.out.println("homefragment else no datasnapshot onkeyenetered");
                                            handleNoKeys();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("on location error homefrag" + databaseError.getDetails());
                                    }
                                });
                            } else {
//                                System.out.println(" my key same");
                                handleNoKeys();
                            }
                        }

                        @Override
                        public void onKeyExited(String key) {
                            System.out.println(" remove called onkeyexited" + key);
                            DistanceUser distanceUser = hashMap.get(key);
                            removeTile(key, distanceUser);
                        }

                        @Override
                        public void onKeyMoved(final String key, final GeoLocation location) {
                            if (!key.equals(mykey)) {
                                System.out.println("remove called onkeymoved");
                                DistanceUser distanceUserOld = hashMap.get(key);
                                removeTile(key, distanceUserOld);

                                DatabaseReference username = FirebaseDatabase.getInstance().getReference().child("Users").child("Userprofiles").child(key).getRef();
                                username.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            mAdapter.notifyDataSetChanged();
                                            callMethod(dataSnapshot, mylocation, location, key);}
                                        else {
//                                            System.out.println("handle nokeys from onkeymoved");
                                            handleNoKeys();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        System.out.println("on location error homefrag" + databaseError.getDetails());
                                    }
                                });

                            }
                        }

                        @Override
                        public void onGeoQueryReady() {
                            System.out.println("All initial data has been loaded and events have been fired!");
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            System.out.println("error geo fire" + error.getDetails());
                        }
                    });

                }
            }
        });

        mAdapter.notifyDataSetChanged();
    }
    private void handleNoKeys() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                System.out.println("list size handler nokeys " + list.size());

                if (list.size() == 0 && distancePref == 50) {
                    System.out.println("sending broadcast from onkeymoved homefragment");
                    sendBroadcast(new Intent().putExtra("tabindex", "2").setAction("MainActTabHandler"));
                }
            }
        }, 2000);
    }



    private void callMethod(final DataSnapshot dataSnapshot,  final GeoLocation mylocation, final GeoLocation location, final String key1) {
        if (listId.contains(key1)) {
            System.out.println("homefragment list id already contains key= " + key1);
        } else {



            Location myloc = new Location("");
            myloc.setLatitude(mylocation.latitude);
            myloc.setLongitude(mylocation.longitude);

            Location userLoc = new Location("");
            userLoc.setLatitude(location.latitude);
            userLoc.setLongitude(location.longitude);

            float distanceInMiles = myloc.distanceTo(userLoc) / (1000 * userSettingsSharedPref.getConversionFactor());
            distanceInMiles = Math.round(distanceInMiles);
//            System.out.println("myloc callmeth" + myloc.getLatitude() + myloc.getLongitude());



//            System.out.println("meteres = " + myloc.distanceTo(userLoc) + "distance in miles homefragment = " + distanceInMiles);

            User user = User.parse(dataSnapshot);

            DistanceUser distanceUser = new DistanceUser(key1, user.getName(),user.getProfpicurl());
            list.add(distanceUser);
            listId.add(key1);

            hashMap.put(key1, distanceUser);
            mAdapter.notifyDataSetChanged();
//            System.out.println(distanceUser.getName() + " distance user to add " + list.size());

        }
    }

    public void removeTile(String key, DistanceUser du) {
        hashMap.remove(key);
        list.remove(du);
        listId.remove(key);
        mAdapter.notifyItemRemoved(list.indexOf(du));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
