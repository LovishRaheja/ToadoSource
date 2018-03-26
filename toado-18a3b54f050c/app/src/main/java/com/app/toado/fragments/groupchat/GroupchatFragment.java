package com.app.toado.fragments.groupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.toado.R;
import com.app.toado.activity.GroupChat.AddGroup;

/**
 * Created by ghanendra on 14/06/2017.
 */

public class GroupchatFragment extends Fragment {
    private View myFragmentView;
    FragmentManager fmm;


    public static GroupchatFragment newInstance() {
        GroupchatFragment fragment = new GroupchatFragment();
        return fragment;
    }

    public static GroupchatFragment newInstance(Bundle args) {
        GroupchatFragment fragment = new GroupchatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_groupchats, container, false);
        FloatingActionButton fabGroup=(FloatingActionButton)myFragmentView.findViewById(R.id.fabGroup);

        fabGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(),AddGroup.class);
                startActivity(i);
            }
        });

        return myFragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setupUI(view.findViewById(R.id.relcity));
        fmm = getFragmentManager();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
