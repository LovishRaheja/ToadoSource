package com.app.toado.activity.ToadoContacts;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.ForwardChatMessage;
import com.app.toado.R;
import com.app.toado.adapter.DistanceUserAdapter;
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
import static com.facebook.FacebookSdk.getApplicationContext;

public class ToadoContacts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<DistanceUser> list;
    ArrayList<String> listId;
    HashMap<String, DistanceUser> hashMap;
    DatabaseReference ref = DBREF_USER_LOC;
    GeoFire geoFire = new GeoFire(ref);

    TextView noOnline;
    float distancePref;
    private UserSession session;
    private String userkey;
    UserSettingsSharedPref userSettingsSharedPref;
    LinearLayout laycoord;
    SwipeRefreshLayout swipe;
    ToadoAlerts alr;
    Boolean alertdisp = true;
    int count = 0;
    ImageView back;
    TextView recent;
    AlertDialog.Builder builder;

    ImageView search,cancelSearch,addContact,optionsMenu;
    RelativeLayout searchLayout;
    ProgressBar pb;
    EditText searchrecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toado_contacts);
        builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);

        session = new UserSession(this);

        recent=(TextView)findViewById(R.id.recent);
        if (ToadoContacts.this.getIntent().getStringExtra("mykey") != null) {
            userkey = this.getIntent().getStringExtra("mykey");
            System.out.println("1 homefragment from session" + userkey);
        } else {
            System.out.println("2 homefragment from session" + userkey);
            userkey = session.getUserKey();
        }


        back=(ImageView)findViewById(R.id.back);
        search=(ImageView)findViewById(R.id.search);
        searchLayout=(RelativeLayout)findViewById(R.id.searchlayout);
        cancelSearch=(ImageView)findViewById(R.id.cancelSearch);
        optionsMenu=(ImageView)findViewById(R.id.optionsMenu);
        pb=(ProgressBar)findViewById(R.id.pb);
        addContact=(ImageView)findViewById(R.id.addContact);
        searchrecycler=(EditText)findViewById(R.id.searchRecycler);



        searchrecycler.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });


        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ToadoContacts.this);
                builder.setTitle("Select any option");

// add a list
                String[] animals = {"Invite a friend", "Contacts", "Refresh","Help"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                try {
                                    Intent i = new Intent(Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                                    String sAux = "\nLet me recommend you this application\n\n";
                                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                                    startActivity(Intent.createChooser(i, "choose one"));
                                } catch(Exception e) {
                                    //e.toString();
                                }
                                break;
                            case 1:

                                Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                                startActivity(intent);
                                break;

                            case 2:
                                pb.setVisibility(View.VISIBLE);
                                Thread timer = new Thread()
                                {
                                    public void run()
                                    {
                                        try
                                        {
                                            for(int i=1; i<=30; i ++)
                                            {
                                                pb.setProgress(i);
                                                sleep(30);
                                            }


                                        }catch(Exception e){}
                                        finally{
                                            runOnUiThread( new Runnable() {
                                                public void run() {
                                                    pb.setVisibility(View.INVISIBLE);
                                                   // Toast.makeText(ToadoContacts .this, "Thank you for downloading", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                };
                                timer.start();

                                break;
                            case  3:

                                Intent i=new Intent(ToadoContacts.this,ToadoContactsHelp.class);
                                startActivity(i);
                                break;
                        }
                    }
                });

// create and show the alert dialog
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.rvToadoContacts);
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
        if(list.size()>0)
        {
            recent.setVisibility(View.GONE);
        }


    }


    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<DistanceUser> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (DistanceUser s : list) {
            //if the existing elements contains the search input

                //adding the element to filtered list
                filterdNames.add(s);

        }
        mAdapter.notifyDataSetChanged();

        //calling a method of the adapter class and passing the filtered list
      //  mAdapter.filterList(filterdNames);
    }

    public void filterList(ArrayList<DistanceUser> filterdNames) {
        this.list = filterdNames;
        mAdapter.notifyDataSetChanged();
    }

    class AsynTasks extends AsyncTask<Void, Integer, Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=1;i<=100;i++)
            {
                SystemClock.sleep(2000);
                publishProgress(i);
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pb.setVisibility(View.INVISIBLE);
           // Toast.makeText(ProgressBarDemo .this, "Thank you for downloading", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }
    }

    /**  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toado_chat, menu);
        return true;
    }*/

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
                                            if(list.size()>0)
                                            {
                                                recent.setVisibility(View.GONE);
                                            }
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
