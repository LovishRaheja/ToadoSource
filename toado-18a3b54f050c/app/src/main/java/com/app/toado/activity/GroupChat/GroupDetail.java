package com.app.toado.activity.GroupChat;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Matches.MatchesActivity;
import com.app.toado.TinderChat.Matches.MatchesAdapter;
import com.app.toado.TinderChat.Matches.MatchesObject;
import com.app.toado.activity.GroupChat.GroupDetailRecycler.GroupDetailAdapter;
import com.app.toado.activity.GroupChat.GroupDetailRecycler.GroupDetailObject;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class GroupDetail extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    String groupId;
    TextView groupTitle;
    ImageView groupImage;
    ImageView back;
    TextView recyclerCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupId=getIntent().getStringExtra("groupId");
        groupTitle=(TextView)findViewById(R.id.user_detail_name);
        groupImage=(ImageView)findViewById(R.id.user_detail_image);

        FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupTitle.setText(dataSnapshot.child("name").getValue().toString());
                Glide.with(GroupDetail.this).load(dataSnapshot.child("image").getValue().toString()).error(R.drawable.nouser).into(groupImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        recyclerCount=(TextView)findViewById(R.id.recyclerCount);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerMembers);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(GroupDetail.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new GroupDetailAdapter(getDataSetMatches(), GroupDetail.this,groupId);
        mRecyclerView.setAdapter(mMatchesAdapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);

        back=(ImageView)findViewById(R.id.imgback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getGroupMemberIds();
    }


    private void getGroupMemberIds() {

        DatabaseReference matchDb =FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("member").getRef();
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    recyclerCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));



                 for(int i=0;i<dataSnapshot.getChildrenCount();i++)
                  {

                      GetMemberId(dataSnapshot.child(String.valueOf(i)).getValue().toString());

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

    private void GetMemberId(String key) {

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


                    GroupDetailObject obj = new GroupDetailObject(userId, name, profileImageUrl);
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private ArrayList<GroupDetailObject> resultsMatches = new ArrayList<GroupDetailObject>();
    private List<GroupDetailObject> getDataSetMatches() {
        return resultsMatches;
    }

}