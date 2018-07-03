package com.app.toado.Contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

import com.app.toado.FirebaseChat.StaticConfig;
import com.app.toado.R;

import java.util.ArrayList;

/**
 * Created by Silent Knight on 12-04-2018.
 */

public class SendContact extends Activity {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    String otheruserkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_contact);
        listContacts = new ContactFetcher(this).fetchAll();
        Intent intentData = getIntent();
        otheruserkey = intentData.getStringExtra("otheruserkey");
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts,otheruserkey);
        lvContacts.setAdapter(adapterContacts);
    }



}
