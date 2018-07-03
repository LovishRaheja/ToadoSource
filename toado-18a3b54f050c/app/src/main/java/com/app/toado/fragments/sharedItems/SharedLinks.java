package com.app.toado.fragments.sharedItems;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.toado.R;
import com.app.toado.adapter.DistanceUserAdapter;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.model.DistanceUser;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_LOC;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;


public class SharedLinks extends Fragment {

    private View myFragmentView;
    private FragmentManager fmm;

    private RecyclerView mMediaRV;
    private SharedLinkAdapter mLinksAdapter;

    private String mOtherUserKey,chatId;

    private UserSession mUserSession;
    DatabaseReference mDatabaseUser, mDatabaseChat;

    public static SharedLinks newInstance() {
        SharedLinks fragment = new SharedLinks();
        return fragment;
    }

    public static SharedLinks newInstance(Bundle args) {
        SharedLinks fragment = new SharedLinks();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_links, container, false);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        fmm = getFragmentManager();
        mUserSession = new UserSession(getContext());
        Bundle bundle = getArguments();
        mOtherUserKey = bundle.getString("OtherUserKey");
        chatId=bundle.getString("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);



        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.getValue().toString().contains(".com"))
                    {
                        String message = null;
                        String createdByUser = null;
                        String timestamp=null;

                        if(dataSnapshot.child("text").getValue()!=null){
                            message = dataSnapshot.child("text").getValue().toString();
                        }
                        if(dataSnapshot.child("createdByUser").getValue()!=null){
                            createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                        }
                        if(dataSnapshot.child("timestamp").getValue()!=null){
                            timestamp = dataSnapshot.child("timestamp").getValue().toString();
                        }


                        if(message!=null && createdByUser!=null){
                            Boolean currentUserBoolean = false;
                            if(createdByUser.equals(mUserSession.getUserKey())){
                                currentUserBoolean = true;
                            }
                            SharedLinksObject newMessage = new SharedLinksObject(message,timestamp, currentUserBoolean);
                            resultsChat.add(newMessage);
                            mLinksAdapter.notifyDataSetChanged();

                        }
                    }
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });





        mMediaRV = (RecyclerView) view.findViewById(R.id.recyclerLink);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mMediaRV.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setAutoMeasureEnabled(true);

        mLinksAdapter = new SharedLinkAdapter( getDataSetChat(), getContext(),mOtherUserKey,chatId);
        mMediaRV.setAdapter(mLinksAdapter);
    }
    ArrayList<SharedLinksObject> resultsChat = new ArrayList<SharedLinksObject>();
    List<SharedLinksObject> getDataSetChat() {
        return resultsChat;
    }
}
