package com.app.toado.activity.ChatSettings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.app.toado.R;
import com.app.toado.activity.Account.AccountAct;
import com.app.toado.activity.ToadoBaseActivity;
import com.app.toado.adapter.ChatSettingsAdapter;
import com.app.toado.adapter.SettingsAdapter;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class ChatSettingsAct extends ToadoBaseActivity {
    private ChatSettingsAdapter mAdapter;
    private RecyclerView recyclerView;
    ArrayList<String> list;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        UserSession usess = new UserSession(this);

        recyclerView = (RecyclerView) findViewById(R.id.rvchatsettings);
        list=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(ChatSettingsAct.this, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        mAdapter = new ChatSettingsAdapter(list,this,usess);
        recyclerView.setAdapter(mAdapter);
        list.add("App Langauge");
        list.add("Enter is Send");
        list.add("Font Size");
        list.add("Wallpaper");
        list.add("Chat Backup");
        list.add("Chat History");
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
