package com.app.toado.activity.Notifications;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;

import com.app.toado.settings.UserSession;

import java.util.ArrayList;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by Silent Knight on 28-02-2018.
 */

public class NotifiAdapter extends RecyclerView.Adapter<NotifiAdapter.MyViewHolder> {
    ArrayList<NotifiDetails> list = new ArrayList<>();
    private Activity context;
    private Context contx;
    UserSession us;


    public NotifiAdapter(ArrayList<NotifiDetails> list, Activity context, Context contx) {
        this.context = context;
        this.list = list;
        this.contx=contx;


    }

    public NotifiAdapter() {
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvtitle);

            profilePic=(ImageView)itemView.findViewById(R.id.profilePic);
        }

    }

    @Override
    public NotifiAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification, parent, false);

        return new NotifiAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NotifiAdapter.MyViewHolder holder, int position) {

        final NotifiDetails phn = list.get(position);
        holder.title.setText(phn.getOtherusrname().toString());

        us = new UserSession(context);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
