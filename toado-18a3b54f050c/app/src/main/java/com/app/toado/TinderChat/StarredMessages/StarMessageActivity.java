package com.app.toado.TinderChat.StarredMessages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.TinderChat.Chat.ChatAdapter;
import com.app.toado.TinderChat.Chat.ChatObject;
import com.app.toado.activity.ToadoBaseActivity;

import com.app.toado.adapter.StarredMessageAdapter;
import com.app.toado.helper.ChatHelper;
import com.app.toado.helper.GetTimeStamp;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.model.realm.ChatMessageRealm;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by ghanendra on 21/08/2017.
 */

public class StarMessageActivity extends ToadoBaseActivity {
    RecyclerView rvstar;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    UserSession usr;
    final String TAG = "STARMESSAGEACTIVITY";
    private UserSession session;
    ImageView imgnorv;
    ImageButton btnpopup, btnsearch;
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starred_messages);
        rvstar = (RecyclerView) findViewById(R.id.star_recycler_view);
        imgnorv = (ImageView) findViewById(R.id.imgnorv);

        btnsearch = (ImageButton) findViewById(R.id.imgsearch);
        btnpopup = (ImageButton) findViewById(R.id.btnpopup);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.showSearch(true);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter(newText);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {

                //Do some magic
            }
        });
        session = new UserSession(this);
        getChatMessages();

        mChatLayoutManager = new LinearLayoutManager(StarMessageActivity.this);
        rvstar.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new StarMessageAdapter(getDataSetChat(), StarMessageActivity.this, session.getUserKey());
        rvstar.setAdapter(mChatAdapter);


        DBREF_USER_PROFILES.child(session.getUserKey()).child("Starred").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    imgnorv.setVisibility(View.GONE);
                    rvstar.setVisibility(View.VISIBLE);
                    btnpopup.setVisibility(View.VISIBLE);
                }else{
                    btnpopup.setVisibility(View.GONE);
                    //btnsearch.setVisibility(View.GONE);
                    imgnorv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getChatMessages() {
        DBREF_USER_PROFILES.child(session.getUserKey()).child("Starred").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    String createdForUser=null;
                    String timestamp=null;
                    String position=null;

                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(dataSnapshot.child("createdForUser").getValue()!=null){
                        createdForUser = dataSnapshot.child("createdForUser").getValue().toString();
                    }
                    if(dataSnapshot.child("timestamp").getValue()!=null){
                        timestamp = dataSnapshot.child("timestamp").getValue().toString();
                    }
                    if(dataSnapshot.child("position").getValue()!=null)
                    {
                        position=dataSnapshot.child("position").getValue().toString();
                    }


                    if(message!=null && createdByUser!=null){

                        StarObject newMessage = new StarObject(message,timestamp,position, createdByUser,createdForUser);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();

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
    }



    ArrayList<StarObject> resultsChat = new ArrayList<StarObject>();
    List<StarObject> getDataSetChat() {
        return resultsChat;
    }

    public void onBack(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (mAdapter != null)
         //   mAdapter.notifyDataSetChanged();
    }

    public void openMenu(View view) {
        final PopupMenu popup = new PopupMenu(this, findViewById(R.id.btnpopup));
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_staract, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.menuunstar):
                        Query deleteQuery = DBREF_USER_PROFILES.child(session.getUserKey()).child("Starred");
                        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Snapshot) {
                               Snapshot.getRef().removeValue();

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        rvstar.setVisibility(View.GONE);
                        imgnorv.setVisibility(View.VISIBLE);
                        btnpopup.setVisibility(View.GONE);
                        btnsearch.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

 /**   @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            mAdapter.updateList(arlist,"");
        } else {
            super.onBackPressed();
        }
    }*/

  /**  void filter(String text) {
        ArrayList<ChatMessageRealm> temp = new ArrayList();
        for (ChatMessageRealm d : arlist) {
            //or use .contains(text)
            if (d.getMsgstring().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp,text);
    }*/
}
