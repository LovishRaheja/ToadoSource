package com.app.toado.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.Account.AccountUpdate;
import com.app.toado.activity.AppSettings.AppSettings;
import com.app.toado.activity.ChatSettings.ChatSettingsAct;
import com.app.toado.activity.Help.Help;
import com.app.toado.activity.Invite.Invite;
import com.app.toado.activity.profile.ProfileAct;
import com.app.toado.activity.settings.DistancePreferencesActivity;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;

/**
 * Created by RajK on 16-05-2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    private Activity context;
    UserSession usess;
    public SettingsAdapter(ArrayList<String> list, Activity context, UserSession usess) {
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        String topic = list.get(position);
        holder.title.setText(topic);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.title.getText().toString()){
                    case "Profile":
                        context.startActivity(new Intent(context, ProfileAct.class));
                        break;
                    case "Account":
                        context.startActivity(new Intent(context, AccountUpdate.class));
                        break;
                    case "Chats":
                        context.startActivity(new Intent(context,ChatSettingsAct.class));
                        break;
                    case "Invite":
                        context.startActivity(new Intent(context,Invite.class));
                        break;
                      /**  try {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                            String sAux = "\nLet me recommend you this application\n\n";
                            sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                            i.putExtra(Intent.EXTRA_TEXT, sAux);
                            context.startActivity(Intent.createChooser(i, "choose one"));
                        } catch(Exception e) {
                            //e.toString();
                        }
                        break;*/

                    case "App Settings":
                        context.startActivity(new Intent(context,AppSettings.class));
                        break;
                    case "Distance and Gps":
                        context.startActivity(new Intent(context, DistancePreferencesActivity.class));
                        break;
                    case "Logout":
                        ToadoAlerts.showLogoutAlert(context,usess);
                        break;
                    case "Help":
                        context.startActivity(new Intent(context, Help.class));


                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
