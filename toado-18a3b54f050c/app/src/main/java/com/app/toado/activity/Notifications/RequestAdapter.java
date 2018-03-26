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
 * Created by Silent Knight on 27-02-2018.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    ArrayList<RequestDetails> list = new ArrayList<>();
    private Activity context;
    private Context contx;
   UserSession us;


    public RequestAdapter(ArrayList<RequestDetails> list, Activity context, Context contx) {
        this.context = context;
        this.list = list;
      this.contx=contx;


    }

    public RequestAdapter() {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,desc;
        Button accept,reject;
        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tvtitle);
            desc = (TextView) itemView.findViewById(R.id.desc);
            accept=(Button)itemView.findViewById(R.id.accept);
            reject=(Button)itemView.findViewById(R.id.reject);
            profilePic=(ImageView)itemView.findViewById(R.id.profilePic);
        }

    }

    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);

        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.MyViewHolder holder, int position) {
         //String topic= list.get(position);
        //holder.title.setText(topic);
        final RequestDetails phn = list.get(position);
        holder.title.setText(phn.getOtherusrname().toString());
        final String key=phn.getOtherusrkey().toString();
        us = new UserSession(context);




        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBREF_USER_PROFILES.child(us.getUserKey()).child("Connections").child(key).child("Status").setValue("accepted");
                DBREF_USER_PROFILES.child(key).child("Friends").child(us.getUserKey()).child("Status").setValue("accepted");
                DBREF_USER_PROFILES.child(key).child("Friends").child(us.getUserKey()).child("Name").setValue(us.getUsername().toString());
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.desc.setText(" is now connected with you");




            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
