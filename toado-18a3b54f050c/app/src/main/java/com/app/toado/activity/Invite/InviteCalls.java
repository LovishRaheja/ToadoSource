package com.app.toado.activity.Invite;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.helper.MarshmallowPermissions;

import java.util.ArrayList;
import java.util.Date;

public class InviteCalls extends AppCompatActivity  implements Callbacks{
    ArrayList<CallLogItem> mItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MarshmallowPermissions marshmallowPermissions;
    private RecyclerView.Adapter mAdapter;
    private static final String[] SMS_PERMISSIONS = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG};

    Button inviteCalls;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_calls);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        marshmallowPermissions = new MarshmallowPermissions(this);

        inviteCalls=(Button)findViewById(R.id.inviteCalls);

        marshmallowPermissions.reguestNewPermissions(this, SMS_PERMISSIONS);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getCallDetails();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("CallPref"));
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Get extra data included in the Intent
            final String ItemName = intent.getStringExtra("callInvite");
            inviteCalls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    if(ItemName.length()>0){
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            String text = "<a href='www.link.com'>Click here</a>";
                            smsManager.sendTextMessage(ItemName.substring(0,ItemName.length()-1), null, "Hi, Check out Toado. I use it to discover new people nearby, message and call the people i care about. Get it fot free at https://www.google/com", null, null);
                            Toast.makeText(getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(InviteCalls.this, "Select at least one contact", Toast.LENGTH_SHORT).show();
                }
            });


        }
    };
    private void getCallDetails() {
        String strOrder = CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int name=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
      //  int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
       // int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
       // int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String Name = managedCursor.getString(name);
           // String strcallDate = managedCursor.getString(date);
           // Date callDate = new Date(Long.valueOf(strcallDate));
          //  String callDuration = managedCursor.getString(duration);
            String callType = null;
           // int callcode = Integer.parseInt(callTypeCode);
          /**  switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }*/

            CallLogItem callLogItem = new CallLogItem();
            callLogItem.setPhoneNumber(phNum);
            callLogItem.setName(Name);
        //    callLogItem.setCallDate(String.valueOf(callDate));
            //callLogItem.setCallType(callType);
           // callLogItem.setCallDuration(callDuration);
            mItems.add(callLogItem);

            mAdapter = new CallLogAdapter(mItems, this,InviteCalls.this);
            mRecyclerView.setAdapter(mAdapter);

        }
        managedCursor.close();
    }

    @Override
    public void onCustomClick(CallLogItem callLogItem) {

    }

    @Override
    public void onCustomLongClick(CallLogItem callLogItem) {

    }
}


