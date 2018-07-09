package com.app.toado.activity.profile;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.app.toado.R;
import com.app.toado.activity.GroupChat.GroupDetail;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class EditGroupName extends AppCompatActivity {
    ImageView back;
    EditText editGroupName;
    String groupId,groupName;

    Button cancel,ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_name);
        back=(ImageView)findViewById(R.id.back);
        editGroupName=(EditText)findViewById(R.id.groupName);
        cancel=(Button)findViewById(R.id.cancel);
        ok=(Button)findViewById(R.id.ok);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        groupId=getIntent().getStringExtra("groupId");
        groupName=getIntent().getStringExtra("groupName");
        editGroupName.setText(groupName);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("group").child(groupId).child("groupInfo").child("name").setValue(editGroupName.getText().toString());
                Intent i=new Intent(EditGroupName.this, GroupDetail.class);
                i.putExtra("groupId",groupId);
                startActivity(i);
            }
        });
    }
}
