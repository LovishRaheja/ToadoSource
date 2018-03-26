package com.app.toado.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.Account.PrivacyAct;
import com.app.toado.activity.Account.SecurityAct;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;

/**
 * Created by Silent Knight on 21-02-2018.
 */

public class PrivacyAdapter extends RecyclerView.Adapter<PrivacyAdapter.MyViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    AlertDialog levelDialog;

    // Strings to Show In Dialog with Radio Buttons
    final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};
    private Activity context;
    UserSession usess;
    CharSequence[] values = {" First Item "," Second Item "," Third Item "};
    public PrivacyAdapter(ArrayList<String> list, Activity context, UserSession usess) {
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
    public PrivacyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account, parent, false);
        return new PrivacyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PrivacyAdapter.MyViewHolder holder, int position) {
        String topic = list.get(position);
        holder.title.setText(topic);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.title.getText().toString()){
                    case "Last Seen":


                        // Creating and Building the Dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Select The Difficulty Level");
                        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {


                                switch(item)
                                {
                                    case 0:
                                        // Your code when first option seletced
                                        break;
                                    case 1:
                                        // Your code when 2nd  option seletced

                                        break;
                                    case 2:
                                        // Your code when 3rd option seletced
                                        break;
                                    case 3:
                                        // Your code when 4th  option seletced
                                        break;

                                }
                                levelDialog.dismiss();
                            }
                        });
                        levelDialog = builder.create();
                        levelDialog.show();
                    case "Profile Photo":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "About":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "Status":
                        context.startActivity(new Intent(context, SecurityAct.class));
                        break;
                    case "Live Location":
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