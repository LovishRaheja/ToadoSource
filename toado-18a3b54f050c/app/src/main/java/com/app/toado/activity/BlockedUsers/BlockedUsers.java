package com.app.toado.activity.BlockedUsers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.TinderChat.Matches.MatchesAdapter;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.helper.CircleTransform;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class BlockedUsers extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private String cusrrentUserID;
    private UserSession session;
    ImageView back;
    TextView noBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);

        back=(ImageView)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        noBlock=(TextView)findViewById(R.id.noBlock);

        session = new UserSession(this);

        cusrrentUserID = session.getUserKey();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(BlockedUsers.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new BlockedAdapter(getDataSetMatches(), BlockedUsers.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
        back=(ImageView )findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBlockedId();

    }

    private void getBlockedId() {

        final DatabaseReference matchDb = DBREF_USER_PROFILES.child(session.getUserKey()).child("Blocked");
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
                else{
                    noBlock.setVisibility(View.VISIBLE);

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


                    BlockedObject obj = new BlockedObject(userId, name, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<BlockedObject> resultsMatches = new ArrayList<BlockedObject>();
    private List<BlockedObject> getDataSetMatches() {
        return resultsMatches;
    }

}
