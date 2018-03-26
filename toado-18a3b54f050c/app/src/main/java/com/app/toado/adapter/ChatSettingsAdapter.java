package com.app.toado.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.Account.AccountAct;
import com.app.toado.activity.profile.ProfileAct;
import com.app.toado.activity.settings.DistancePreferencesActivity;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;

/**
 * Created by Silent Knight on 13-02-2018.
 */

public class ChatSettingsAdapter extends RecyclerView.Adapter<ChatSettingsAdapter.MyViewHolder> {
        ArrayList<String> list = new ArrayList<>();

    private Activity context;
        UserSession usess;

    public ChatSettingsAdapter(ArrayList<String> list, Activity context, UserSession usess) {
        this.context = context;
        this.list = list;
        this.usess=usess;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView title;

    public MyViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.tvtitle);
    }

}

    @Override
    public ChatSettingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_settings, parent, false);
        return new ChatSettingsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatSettingsAdapter.MyViewHolder holder, int position) {
        String topic = list.get(position);
        holder.title.setText(topic);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.title.getText().toString()){

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
