package com.app.toado.activity.GroupChat;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.toado.R;
import com.app.toado.activity.ToadoContacts.ToadoContacts;
import com.app.toado.adapter.ToadoAdapter;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.model.DistanceUser;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.LinearLayout.VERTICAL;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        session = new UserSession(this);


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

        mAdapter = new ToadoAdapter(list, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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
