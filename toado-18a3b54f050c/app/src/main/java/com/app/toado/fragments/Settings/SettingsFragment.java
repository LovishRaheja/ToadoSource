package com.app.toado.fragments.Settings;


import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.profile.ProfileAct;
import com.app.toado.adapter.DistanceUserAdapter;
import com.app.toado.adapter.SettingsAdapter;
import com.app.toado.fragments.home.HomeFragment;
import com.app.toado.helper.CircleTransform;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_LOC;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Silent Knight on 15-02-2018.
 */

public class SettingsFragment extends Fragment {

    private View myFragmentView;
    FragmentManager fmm;
    private RecyclerView recyclerView;

    ImageView userImage;
    TextView name,phone;
    LinearLayout settingsProfile;


    private SettingsAdapter mSettingsAdapter;


    private UserSession session;
    private String userkey;
    UserSettingsSharedPref userSettingsSharedPref;
   // LinearLayout laycoord;


    AlertDialog.Builder builder;


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public static SettingsFragment newInstance(Bundle args) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fmm = getFragmentManager();
        userImage=(ImageView)view.findViewById(R.id.profile_image);
        name=(TextView)view.findViewById(R.id.name);
        phone=(TextView)view.findViewById(R.id.phone);
        UserSession usess = new UserSession(getActivity());
        settingsProfile=(LinearLayout)view.findViewById(R.id.settingsProfile);
        settingsProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ProfileAct.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });


        //usrkey = us.getUserKey();
        DBREF_USER_PROFILES.child(usess.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                    String imgurl = u.getProfpicurl();

                    name.setText(u.getName());
                    phone.setText(u.getPhone());
                    Glide.with(getActivity()).load(imgurl).dontAnimate()
                            .transform(new CircleTransform(getActivity())).load(imgurl).into(userImage);

                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // laycoord = (CoordinatorLayout) view.findViewById(R.id.coordinatorlayout);

        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);


        ArrayList<String>  list=new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_settings);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        mSettingsAdapter = new SettingsAdapter(list,getActivity(),usess);
        recyclerView.setAdapter(mSettingsAdapter);



        list.add("Account");
        list.add("Chats");
        list.add("Invite");
        list.add("App Settings");
        list.add("Distance and Gps");
        list.add("Help");
        //list.add("Firebase Chat");
       // list.add("Logout");
        mSettingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
