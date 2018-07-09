package com.app.toado.fragments.call;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.adapter.CallLogsAdapter;
import com.app.toado.model.CallDetails;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

import static android.widget.LinearLayout.VERTICAL;
import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_CALLS;

/**
 * Created by ghanendra on 14/06/2017.
 */

public class CallFragment extends Fragment implements ServiceConnection{
    private View myFragmentView;
    FragmentManager fmm;
    ArrayList<CallDetails> phnlist;
    ArrayList<String> phnlistIds;
    private RecyclerView recyclerView;
    private CallLogsAdapter mAdapter;
    UserSession us;
    TextView nocall;


    String mk;

    public static CallFragment newInstance() {
        CallFragment fragment = new CallFragment();
        return fragment;
    }

    public static CallFragment newInstance(Bundle args) {
        CallFragment fragment = new CallFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_calls, container, false);
        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setupUI(view.findViewById(R.id.relcity));
        fmm = getFragmentManager();

        us = new UserSession(getContext());
        mk = us.getUserKey();

nocall=(TextView)view.findViewById(R.id.nocall);
        phnlist = new ArrayList<>();
        phnlistIds = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.rvcalllogs);
        mAdapter = new CallLogsAdapter(phnlist, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRvData();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    private void loadRvData() {
        DBREF_CALLS.child(mk).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println("ddatasnapshot callfragment " + dataSnapshot);
                CallDetails cd = CallDetails.parse(dataSnapshot);
                if (!phnlistIds.contains(cd.getCallid())) {
                    phnlist.add(cd);
                    phnlistIds.add(cd.getCallid());
                }
                mAdapter.notifyDataSetChanged();
                if(phnlist.size()>0)
                {
                    nocall.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
