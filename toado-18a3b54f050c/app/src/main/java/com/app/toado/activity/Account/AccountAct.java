package com.app.toado.activity.Account;


import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.app.toado.R;
import com.app.toado.activity.ToadoBaseActivity;
import com.app.toado.adapter.AccountAdapter;

import com.app.toado.settings.UserSession;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;

public class AccountAct extends ToadoBaseActivity {
    private AccountAdapter mAdapter;
    private RecyclerView recyclerView;
    ArrayList<String> list;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        UserSession usess = new UserSession(this);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.rvaccount);
        list=new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(AccountAct.this, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        mAdapter = new AccountAdapter(list,this,usess);
        recyclerView.setAdapter(mAdapter);
        list.add("Privacy");
        list.add("Security");
        list.add("Two Step Verification");
        list.add("Change Number");
        list.add("Delete my account");
        mAdapter.notifyDataSetChanged();

    }
    @Override
    public void onBackPressed() {
        finish();
    }


}
