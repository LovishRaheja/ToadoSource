package com.app.toado.TinderChat.Chat.ChatContacts;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.Invite.InviteFacebook;
import com.app.toado.helper.CircleTransform;
import com.bumptech.glide.Glide;

public class ShowContact extends AppCompatActivity {

    ImageView back,pic;
    String name,number;

    TextView Name,phone;
    Button addContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pic=(ImageView)findViewById(R.id.pic);
        Glide.with(ShowContact.this).load(R.drawable.image).dontAnimate().transform(new CircleTransform(ShowContact.this)).into(pic);

        Name=(TextView)findViewById(R.id.name);
        phone=(TextView)findViewById(R.id.phone);


        final Intent intentData = getIntent();
         name = intentData.getStringExtra("name");
         number=intentData.getStringExtra("number");

        Name.setText(name);
        phone.setText(number);

        addContact=(Button)findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, name)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, number);

                startActivityForResult(contactIntent,1);
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Added Contact", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
