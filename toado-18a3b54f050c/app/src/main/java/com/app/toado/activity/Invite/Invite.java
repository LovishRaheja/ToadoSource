package com.app.toado.activity.Invite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.app.toado.R;

public class Invite extends AppCompatActivity {

    Button invFb,invContact,invCall;

    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        invFb=(Button)findViewById(R.id.invFb);
        invContact=(Button)findViewById(R.id.invContact);
        invCall=(Button)findViewById(R.id.invCall);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        invFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Invite.this,InviteFacebook.class);
                startActivity(i);
            }
        });

        invContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Invite.this,InviteContacts.class);
                startActivity(i);
            }
        });

        invCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Invite.this,InviteCalls.class);
                startActivity(i);
            }
        });

    }
}
