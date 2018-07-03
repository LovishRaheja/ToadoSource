package com.app.toado.TinderChat.ChatFragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.TinderChat.Matches.MatchesAdapter;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.settings.UserSession;
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
 * Created by Silent Knight on 08-05-2018.
 */

public class ChatFragment  extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String cusrrentUserID;
    private UserSession session;
    MarshmallowPermissions marshper;
    TextView recent;

    private View myFragmentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        marshper = new MarshmallowPermissions(getActivity());

        myFragmentView = inflater.inflate(R.layout.fragment_tinder_chat, container, false);


        session = new UserSession(getContext());

        cusrrentUserID = session.getUserKey();
        recent=(TextView)myFragmentView.findViewById(R.id.recent);

        FloatingActionButton fab = (FloatingActionButton) myFragmentView.findViewById(R.id.fab);
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
        // Toast.makeText(MatchesActivity.this, FirebaseDatabase.getInstance().getReference().child("Userprofiles").child(cusrrentUserID).child("Connections")..toString(), Toast.LENGTH_SHORT).show();

        mRecyclerView = (RecyclerView)myFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new ChatFragmentAdapter(getDataSetMatches(),getActivity());
        mRecyclerView.setAdapter(mMatchesAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        getUserMatchId();

        if(getDataSetMatches().size()>0)
        {
            recent.setVisibility(View.GONE);
        }
        else{
            recent.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase.getInstance().getReference().child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {

                    mRecyclerView.setVisibility(View.INVISIBLE);
                    recent.setVisibility(View.VISIBLE);
                }
                else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    recent.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return myFragmentView;
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public static ChatFragment newInstance(Bundle args) {
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getUserMatchId() {

        final DatabaseReference matchDb = DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches");
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
        DatabaseReference userDb = DBREF_USER_PROFILES.child(key);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    if(dataSnapshot.child("name").getValue()!=null){
                        name = dataSnapshot.child("name").getValue().toString();

                    }
                    if(dataSnapshot.child("profpicurl").getValue()!=null){
                        profileImageUrl = dataSnapshot.child("profpicurl").getValue().toString();
                    }


                    ChatFragmentObject obj1 = new ChatFragmentObject(userId, name, profileImageUrl);
                    resultsMatches.add(obj1);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<ChatFragmentObject> resultsMatches = new ArrayList<ChatFragmentObject>();
    private List<ChatFragmentObject> getDataSetMatches() {
        return resultsMatches;
    }
}
