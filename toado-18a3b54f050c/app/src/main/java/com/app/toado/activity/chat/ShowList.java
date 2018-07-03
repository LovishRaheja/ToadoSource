package com.app.toado.activity.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.toado.Contact.Contact;
import com.app.toado.Contact.ContactFetcher;
import com.app.toado.Contact.ContactsAdapter;
import com.app.toado.R;
import com.app.toado.activity.ToadoContacts.ToadoContacts;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ShowList extends AppCompatActivity {
    ArrayList<Contact> listContacts;
    ListView lvContacts;
    ImageView toado,back;
    LinearLayout toadoContacts;
    String otheruserkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        listContacts = new ContactFetcher(this).fetchAll();
        lvContacts = (ListView) findViewById(R.id.lvContacts);
        ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts,otheruserkey);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lvContacts.setAdapter(adapterContacts);

        toado=(ImageView)findViewById(R.id.profile_image);
        toadoContacts=(LinearLayout)findViewById(R.id.toadoContacts);
        toadoContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ShowList.this, ToadoContacts.class);
                startActivity(i);
            }
        });

    }
}
