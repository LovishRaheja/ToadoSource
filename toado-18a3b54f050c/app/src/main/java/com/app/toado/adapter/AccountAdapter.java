package com.app.toado.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.Account.PrivacyAct;
import com.app.toado.activity.Account.SecurityAct;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;

/**
 * Created by Silent Knight on 13-02-2018.
 */

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.MyViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    private Activity context;
    UserSession usess;
    public AccountAdapter(ArrayList<String> list, Activity context, UserSession usess) {
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
    public AccountAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account, parent, false);
        return new AccountAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccountAdapter.MyViewHolder holder, int position) {
        String topic = list.get(position);
        holder.title.setText(topic);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.title.getText().toString()){
                    case "Privacy":
                        context.startActivity(new Intent(context, PrivacyAct.class));
                        break;
                    case "Security":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "Two Step Verification":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "Change Number":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "Delete my account":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
