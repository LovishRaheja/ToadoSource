package com.app.toado.fragments.groupchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.Consersation;
import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.TinderChat.Matches.MatchesAdapter;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.activity.GroupChat.AddGroup;
import com.app.toado.activity.GroupChat.GroupChatActivity;

import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by ghanendra on 14/06/2017.
 */

public class GroupchatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mGroupAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;


    private View myFragmentView;
    FragmentManager fmm;



    private String userkey;

    private UserSession session;
    public static GroupchatFragment newInstance() {
        GroupchatFragment fragment = new GroupchatFragment();
        return fragment;
    }

    public static GroupchatFragment newInstance(Bundle args) {
        GroupchatFragment fragment = new GroupchatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_groupchats, container, false);
        FloatingActionButton fabGroup = (FloatingActionButton) myFragmentView.findViewById(R.id.fabGroup);
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recyclerGroup);

        session = new UserSession(getActivity());
        if (getActivity().getIntent().getStringExtra("mykey") != null) {
            userkey = getActivity().getIntent().getStringExtra("mykey");
            System.out.println("1 homefragment from session" + userkey);
        } else {
            System.out.println("2 homefragment from session" + userkey);
            userkey = session.getUserKey();
        }


        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mMatchesLayoutManager);

        mGroupAdapter = new GroupAdapterFragment(getDataSetMatches(), getContext());
        recyclerView.setAdapter(mGroupAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        getGroupId();

       fabGroup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent i = new Intent(getActivity(), AddGroup.class);
               startActivity(i);
           }
                });


        return myFragmentView;
    }

    private void getGroupId() {



        final DatabaseReference matchDb = DBREF_USER_PROFILES.child(session.getUserKey()).child("group");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // Toast.makeText(MatchesActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot match : dataSnapshot.getChildren()){

                        // Toast.makeText(MatchesActivity.this, match.getKey(), Toast.LENGTH_SHORT).show();

                        FetchMatchInformation(match.getKey());
                    }
                }

                // String key = matchDb.getKey();
                //  Toast.makeText(MatchesActivity.this, key, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("group").child(key);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String groupId = dataSnapshot.getKey();
                    String name = "";
                    String image = "";
                    if(dataSnapshot.child("groupInfo").child("name").getValue()!=null){
                        name = dataSnapshot.child("groupInfo").child("name").getValue().toString();

                    }
                    if(dataSnapshot.child("groupInfo").child("image").getValue()!=null){
                        image = dataSnapshot.child("groupInfo").child("image").getValue().toString();

                    }
                    else{
                        image="nil";
                    }


                    GroupObject obj = new GroupObject(groupId, name,image);
                    resultsMatches.add(obj);
                    mGroupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    private ArrayList<GroupObject> resultsMatches = new ArrayList<GroupObject>();
    private List<GroupObject> getDataSetMatches() {
        return resultsMatches;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setupUI(view.findViewById(R.id.relcity));
        fmm = getFragmentManager();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {

    }
}




