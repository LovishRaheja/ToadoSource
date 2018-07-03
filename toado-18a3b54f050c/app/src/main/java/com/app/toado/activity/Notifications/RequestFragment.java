package com.app.toado.activity.Notifications;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 27-02-2018.
 */

public class RequestFragment extends Fragment {


    FragmentManager fmm;
    private RequestAdapter mAdapter;
    private RecyclerView recyclerView;
    ArrayList<RequestDetails> list;
    TextView empty;
    UserSession usess;

    public static RequestFragment newInstance() {
        RequestFragment fragment = new RequestFragment();
        return fragment;
    }

    public static RequestFragment newInstance(Bundle args) {
        RequestFragment fragment = new RequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_noti_req_fragment, container, false);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        DBREF_USER_PROFILES.child(usess.getUserKey()).child("Connections").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RequestDetails cd = RequestDetails.parse(dataSnapshot);
                mAdapter.notifyDataSetChanged();
                if(dataSnapshot.child("Status").exists()) {
                    if (dataSnapshot.child("Status").getValue().equals("accepted")) {
                        list.remove(cd);
                        mAdapter.notifyDataSetChanged();

                    }
                }else{
                    Toast.makeText(getContext(), "No recent requests", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onStart() {
        super.onStart();
        DBREF_USER_PROFILES.child(usess.getUserKey()).child("Connections").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RequestDetails cd = RequestDetails.parse(dataSnapshot);
                mAdapter.notifyDataSetChanged();
                if(dataSnapshot.child("Status").getValue().equals("accepted"))
                {
                    list.remove(cd);
                    mAdapter.notifyDataSetChanged();

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


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fmm = getFragmentManager();


        usess = new UserSession(getActivity());

        recyclerView = (RecyclerView)view. findViewById(R.id.rvrequest);
        list=new ArrayList<>();
        empty=(TextView)view.findViewById(R.id.empty);
        recyclerView.setHasFixedSize(true);
        mAdapter = new RequestAdapter(list,getActivity(),getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);



        DBREF_USER_PROFILES.child(usess.getUserKey()).child("Connections").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                RequestDetails cd = RequestDetails.parse(dataSnapshot);
                list.add(cd);
                mAdapter.notifyDataSetChanged();
                if(dataSnapshot.child("Status").getValue().equals("accepted"))
                {
                    list.remove(cd);
                    mAdapter.notifyDataSetChanged();

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

        if(list.size()>0)
        {
            empty.setVisibility(View.GONE);
        }
//        setupUI(view.findViewById(R.id.relcity));

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
