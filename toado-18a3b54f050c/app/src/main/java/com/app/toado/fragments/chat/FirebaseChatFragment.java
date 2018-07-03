package com.app.toado.fragments.chat;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.RivChat.model.Friend;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.activity.ToadoContacts.ToadoContacts;
import com.app.toado.adapter.ChatAdapter;
import com.app.toado.adapter.ChatListAdapter;
import com.app.toado.adapter.ToadoAdapter;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.model.DistanceUser;
import com.app.toado.model.User;
import com.app.toado.model.realm.ActiveChatsRealm;
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

import static android.R.id.list;
import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_LOC;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 04-04-2018.
 */

public class FirebaseChatFragment extends Fragment
{
    private View myFragmentView;
    FragmentManager fmm;
    MarshmallowPermissions marshper;
    private RecyclerView recyclerView;
    private String mykey;
    private ChatAdapter mAdapter;
    UserSession us;

    ArrayList<String> listId;
    private final String TAG = " CHATFRAGMENT";
    float distancePref;
    DatabaseReference ref = DBREF_USER_LOC;
    GeoFire geoFire = new GeoFire(ref);
    HashMap<String, DistanceUser> hashMap;
    TextView recent;
    private String userkey;
    UserSettingsSharedPref userSettingsSharedPref;

    private ArrayList<DistanceUser> list;

    public static FirebaseChatFragment newInstance() {
        FirebaseChatFragment fragment = new FirebaseChatFragment();
        return fragment;
    }

    public static FirebaseChatFragment newInstance(Bundle args) {
        FirebaseChatFragment fragment = new FirebaseChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_firebase_chat, container, false);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        marshper = new MarshmallowPermissions(getActivity());
        list = new ArrayList<>();
        us = new UserSession(getActivity());
        recent=(TextView)view.findViewById(R.id.recent);
        if (getActivity().getIntent().getStringExtra("mykey") != null) {
            userkey = getActivity().getIntent().getStringExtra("mykey");
            System.out.println("1 homefragment from session" + userkey);
        } else {
            System.out.println("2 homefragment from session" + userkey);
            userkey = us.getUserKey();
        }
        Log.d(TAG, "chatfragment onviewcreated "+us.getUserKey());
       // recent=(TextView)view.findViewById(R.id.recent);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marshper.checkPermissionForContacts()) {
                    getContext().startActivity(new Intent(getActivity(), MatchesActivity.class));
                } else {
                    marshper.requestPermissionForContacts();
                    getContext().startActivity(new Intent(getActivity(), MatchesActivity.class));
                }
            }
        });
        listId = new ArrayList<>();
        hashMap = new HashMap<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ChatAdapter(list, getActivity());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
      /* if(list.size()>0)
        {
            recent.setVisibility(View.GONE);
        }*/

        FirebaseDatabase.getInstance().getReference().child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {

                    recyclerView.setVisibility(View.INVISIBLE);
                    recent.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    recent.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("on resume called home fragment");
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
        userSettingsSharedPref = new UserSettingsSharedPref(getActivity());
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
                                        /**    if(list.size()>0)
                                            {
                                                recent.setVisibility(View.GONE);
                                            }*/
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
                    //sendBroadcast(new Intent().putExtra("tabindex", "2").setAction("MainActTabHandler"));
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
