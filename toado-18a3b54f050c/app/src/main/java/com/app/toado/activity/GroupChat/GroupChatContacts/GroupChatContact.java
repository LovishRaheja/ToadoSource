package com.app.toado.activity.GroupChat.GroupChatContacts;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatContactAdapter;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatContacts;
import com.app.toado.TinderChat.Chat.ChatContacts.ChatDatabaseAdapter;
import com.app.toado.TinderChat.Chat.ChatContacts.SelectContact;
import com.app.toado.helper.MarshmallowPermissions;

import java.util.List;

public class GroupChatContact extends AppCompatActivity {
    GroupDatabaseAdapter mydb;
    GroupContactAdapter suAdapter;
    private MarshmallowPermissions marshmallowPermissions;
    RecyclerView recyclerView;
    SearchView search;

    Button inviteContact;
    private static final String[] SMS_PERMISSIONS = {
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS};

    ImageView back;
    String otheruserkey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_contact);
        mydb  = new GroupDatabaseAdapter(getApplicationContext());
        recyclerView = (RecyclerView)findViewById(R.id.contacts_list);
        setRecyclerview();
        search = (SearchView)findViewById(R.id.searchView);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String stext) {
                suAdapter.filter(stext);
                return false;
            }
        });

        marshmallowPermissions = new MarshmallowPermissions(this);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intentData = getIntent();
        otheruserkey = intentData.getStringExtra("otheruserkey");

        marshmallowPermissions.reguestNewPermissions(this, SMS_PERMISSIONS);

    }

    private void setRecyclerview() {
        new GroupChatContact.ConttactLoader().execute();
    }

    public class ConttactLoader extends AsyncTask<Void, Void, List<SelectGroupContact>> {

        @Override
        protected List<SelectGroupContact> doInBackground(Void... voids) {
            List<SelectGroupContact> MHList = mydb.getData();


            return MHList;
        }

        @Override
        protected void onPostExecute(List<SelectGroupContact> selectUsers) {
            if (selectUsers.isEmpty()==false){
                suAdapter = new GroupContactAdapter(GroupChatContact.this, selectUsers,otheruserkey);

                recyclerView.setLayoutManager(new LinearLayoutManager(GroupChatContact.this));
                recyclerView.setAdapter(suAdapter);
            }
        }
    }
}
