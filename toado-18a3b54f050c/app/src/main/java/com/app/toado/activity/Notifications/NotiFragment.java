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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.Invite.InviteFacebook;
import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
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


    //ImageView img1,img2,img3;

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

       // img1=(ImageView)v.findViewById(R.id.img1);
      //  img2=(ImageView)v.findViewById(R.id.img2);
       // img3=(ImageView)v.findViewById(R.id.img3);


        recyclerView = (RecyclerView)v. findViewById(R.id.rvnotification);
        list=new ArrayList<>();

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


      //  Glide.with(getActivity()).load(R.drawable.img10).dontAnimate().transform(new CircleTransform(getActivity())).into(img1);
      //  Glide.with(getActivity()).load(R.drawable.img8).dontAnimate().transform(new CircleTransform(getActivity())).into(img2);

      //  Glide.with(getActivity()).load(R.drawable.img9).dontAnimate().transform(new CircleTransform(getActivity())).into(img3);




        DBREF_USER_PROFILES.child(usess.getUserKey()).child("Friends").getRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotifiDetails cd = NotifiDetails.parse(dataSnapshot);
                //list.add(cd);
                //mAdapter.notifyDataSetChanged();
                if(dataSnapshot.child("Status").exists()) {
                    if (dataSnapshot.child("Status").getValue().equals("accepted")) {
                        list.add(cd);
                        mAdapter.notifyDataSetChanged();

                    } else if (dataSnapshot.child("Status").getValue().equals("sent")) {
                        Toast.makeText(getContext(), "No new Notifications", Toast.LENGTH_LONG).show();
                        mAdapter.notifyDataSetChanged();

                    }
                }
                else{
                    Toast.makeText(getContext(), "No recent notifications", Toast.LENGTH_SHORT).show();
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
