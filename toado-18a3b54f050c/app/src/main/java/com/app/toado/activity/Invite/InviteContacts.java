package com.app.toado.activity.Invite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.app.toado.Manifest;
import com.app.toado.R;
import com.app.toado.helper.MarshmallowPermissions;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class InviteContacts extends AppCompatActivity {

    DatabaseAdapter mydb;
    SelectUserAdapter suAdapter;
    private MarshmallowPermissions marshmallowPermissions;
    RecyclerView recyclerView;
    SearchView search;

    Button inviteContact;
    private static final String[] SMS_PERMISSIONS = {
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS};

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);
        mydb  = new DatabaseAdapter(getApplicationContext());
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

        marshmallowPermissions.reguestNewPermissions(this, SMS_PERMISSIONS);
        inviteContact=(Button)findViewById(R.id.inviteContact);
        final Intent intentData = getIntent();
        String num=intentData.getStringExtra("list");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("check"));





    }



    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            final String ItemName = intent.getStringExtra("size");
          inviteContact.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {



                  if(ItemName.length()>0){
                      try {
                          SmsManager smsManager = SmsManager.getDefault();
                          smsManager.sendTextMessage(ItemName.substring(0,ItemName.length()-1), null, "Hi, Check out Toado. I use it to discover new people nearby, message and call the people i care about. Get it fot free at https://www.google.com", null, null);
                          Toast.makeText(getApplicationContext(), "Message Sent",
                                  Toast.LENGTH_LONG).show();
                      } catch (Exception ex) {
                          Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                  Toast.LENGTH_LONG).show();
                          ex.printStackTrace();
                      }
                  }
                  else
                      Toast.makeText(InviteContacts.this, "Select at least one contact", Toast.LENGTH_SHORT).show();
              }
          });


        }
    };


    private void setRecyclerview() {
        new ConttactLoader().execute();
    }

    public class ConttactLoader extends AsyncTask<Void, Void, List<SelectUser>> {

        @Override
        protected List<SelectUser> doInBackground(Void... voids) {
            List<SelectUser> MHList = mydb.getData();


            return MHList;
        }

        @Override
        protected void onPostExecute(List<SelectUser> selectUsers) {
            if (selectUsers.isEmpty()==false){
             //   HashSet hs = new HashSet();

               // hs.addAll(selectUsers); // demoArrayList= name of arrayList from which u want to remove duplicates

               // selectUsers.clear();
               // selectUsers.addAll(hs);
                //Collections.sort(selectUsers, Collator.getInstance());
                suAdapter = new SelectUserAdapter(InviteContacts.this, selectUsers);

                recyclerView.setLayoutManager(new LinearLayoutManager(InviteContacts.this));
                recyclerView.setAdapter(suAdapter);
            }
        }
    }
}
