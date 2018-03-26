package com.app.toado.activity.Notifications;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
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

public class NotiFragment extends Fragment {


    FragmentManager fmm;
    private NotifiAdapter mAdapter;
    private RecyclerView recyclerView;
    ArrayList<NotifiDetails> list;
    TextView empty;

    public static NotiFragment newInstance() {
        NotiFragment fragment = new NotiFragment();
        return fragment;
    }

    public static NotiFragment newInstance(Bundle args) {
        NotiFragment fragment = new NotiFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_noti_fragment, null);
        fmm = getFragmentManager();
        final UserSession usess = new UserSession(getActivity());


        recyclerView = (RecyclerView)v. findViewById(R.id.rvnotification);
        list=new ArrayList<>();
        empty=(TextView)v.findViewById(R.id.empty);
        recyclerView.setHasFixedSize(true);
        mAdapter = new NotifiAdapter(list,getActivity(),getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

         //RequestDetails requestDetails=new RequestDetails();


               // if(DBREF_USER_PROFILES.child(usess.getUserKey()).child("Connections").getRef().child("Status").equals("accepted"))
                //{

                   // DBREF_USER_PROFILES.child(requestDetails.getOtherusrkey()).child("Friends").child(usess.getUserKey()).child("Status").setValue("accepted");
                    //mAdapter.notifyDataSetChanged();

               // }






        DBREF_USER_PROFILES.child(usess.getUserKey()).child("Friends").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotifiDetails cd = NotifiDetails.parse(dataSnapshot);
                //list.add(cd);
                //mAdapter.notifyDataSetChanged();
                if(dataSnapshot.child("Status").getValue().equals("accepted"))
                {
                    list.add(cd);
                    mAdapter.notifyDataSetChanged();

                }
                else if(dataSnapshot.child("Status").getValue().equals("sent"))
                {
                    Toast.makeText(getContext(),"No new Notifications",Toast.LENGTH_LONG).show();
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
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setupUI(view.findViewById(R.id.relcity));

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
