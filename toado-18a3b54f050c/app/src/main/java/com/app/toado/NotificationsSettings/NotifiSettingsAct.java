package com.app.toado.NotificationsSettings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.app.toado.activity.Account.AccountAct;
import com.app.toado.adapter.NotifiGrpAdapter;
import com.app.toado.adapter.NotifiMsgAdapter;
import com.app.toado.adapter.SettingsAdapter;
import com.app.toado.settings.UserSession;

import org.w3c.dom.Text;
import com.app.toado.R;
import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class NotifiSettingsAct extends AppCompatActivity {
    private NotifiGrpAdapter grpAdapter;
    private NotifiMsgAdapter msgAdapter;
    private RecyclerView msgRecyclerView;
    private RecyclerView grpRecyclerView;
    ArrayList<String> list1;
    ArrayList<String> list2;
    TextView title,grpnoti;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifi_settings);
        title=(TextView)findViewById(R.id.tvtitle);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        grpnoti=(TextView)findViewById(R.id.grpnoti);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotifiSettingsAct.this,"Enable sounds here",Toast.LENGTH_LONG).show();
            }
        });
        UserSession usess = new UserSession(this);
        msgRecyclerView = (RecyclerView) findViewById(R.id.rvmsgnotification);
        list1=new ArrayList<>();
        msgRecyclerView.setHasFixedSize(true);
        msgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(NotifiSettingsAct.this, VERTICAL);
        msgRecyclerView.addItemDecoration(itemDecor);
        msgAdapter = new NotifiMsgAdapter(list1,this,usess);
        msgRecyclerView.setAdapter(msgAdapter);
        list1.add("Notification Tone");
        list1.add("Vibrate");
        list1.add("Popup Notification");
        list1.add("Light");

        msgAdapter.notifyDataSetChanged();

        grpRecyclerView = (RecyclerView) findViewById(R.id.rvgrpnotification);
        list2=new ArrayList<>();
        grpRecyclerView.setHasFixedSize(true);
        grpRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor2 = new DividerItemDecoration(NotifiSettingsAct.this, VERTICAL);
        grpRecyclerView.addItemDecoration(itemDecor2);
        grpAdapter = new NotifiGrpAdapter(list2,this,usess);
        grpRecyclerView.setAdapter(grpAdapter);
        list2.add("Notification Tone");
        list2.add("Vibrate");
        list2.add("Popup Notification");
        list2.add("Light");
        grpAdapter.notifyDataSetChanged();
    }
}
