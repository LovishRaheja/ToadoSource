package com.app.toado.activity.SharedItems;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.main.MainAct;
import com.app.toado.fragments.mainviewpager.MainpagerAdapter;
import com.app.toado.fragments.mainviewpager.MainpagerItems;
import com.app.toado.fragments.sharedviewpager.SharedPagerAdapter;
import com.app.toado.fragments.sharedviewpager.SharedPagerItems;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by aksha on 9/10/2017.
 */

public class SharedItemsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private SharedPagerAdapter mAdapter;

    private String mOtherUserKey;
    ImageView back;
    TextView name;
    String chatId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shareditemsactivity);

        mOtherUserKey = getIntent().getStringExtra("OtherUserKey");
        chatId=getIntent().getStringExtra("chatId");


        setupViewPager();

    }

    private void setupViewPager() {
        mAdapter = new SharedPagerAdapter(getSupportFragmentManager(), getApplicationContext(), mOtherUserKey,chatId);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        name=(TextView)findViewById(R.id.tvtitle);
        DBREF_USER_PROFILES.child(mOtherUserKey).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayList<SharedPagerItems> items = new ArrayList<>();
        items.add(new SharedPagerItems(SharedPagerItems.PAGE_TYPE.Media, "Media"));
        items.add(new SharedPagerItems(SharedPagerItems.PAGE_TYPE.Documents, "Document"));
        items.add(new SharedPagerItems(SharedPagerItems.PAGE_TYPE.Links, "Links"));

        mAdapter.setItems(items);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.viewpagertab);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(items.get(0).getTitle());
        tabLayout.getTabAt(1).setText(items.get(1).getTitle());
        tabLayout.getTabAt(2).setText(items.get(2).getTitle());

    }

}
