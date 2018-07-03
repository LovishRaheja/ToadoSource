package com.app.toado.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.userprofile.UserProfileAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.model.DistanceUser;
import com.app.toado.settings.UserSession;
import com.app.toado.settings.UserSettingsSharedPref;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Silent Knight on 27-03-2018.
 */

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupAdapter.MyViewHolder> {

    String imgurl;
    ArrayList<DistanceUser> list = new ArrayList<>();
    private Context context;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    private boolean isEdit;
    private LinearLayout addGroup;


    public AddGroupAdapter(Context context,  LinearLayout btnAddGroup, Set<String> listIDChoose, Set<String> listIDRemove) {
        this.context = context;


        this.listIDChoose = listIDChoose;
        this.listIDRemove = listIDRemove;



    }


    public AddGroupAdapter(ArrayList<DistanceUser> list, Context context,LinearLayout addGroup, Set<String> listIDChoose, Set<String> listIDRemove) {
        this.list = list;
        this.context = context;
        this.addGroup=addGroup;
        this.listIDChoose=listIDChoose;
        this.listIDRemove=listIDRemove;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, distance;
        RelativeLayout lay;
        ImageView userImage;
        ImageView groupReady;
        public CheckBox checkBox;
        public MyViewHolder(View itemView) {
            super(itemView);


            name = (TextView) itemView.findViewById(R.id.name);
            lay=(RelativeLayout) itemView.findViewById(R.id.distlay);
            userImage=(ImageView)itemView.findViewById(R.id.userImage);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkAddPeople);
            groupReady=(ImageView)itemView.findViewById(R.id.groupReady);

        }
    }

    @Override
    public AddGroupAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_addgroup_list, parent, false);

        return new AddGroupAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddGroupAdapter.MyViewHolder holder, final int position) {
        final DistanceUser distanceUser = list.get(position);
        holder.name.setText(distanceUser.getName());

        Glide.with(context).load(distanceUser.getProfilePicUrl()).dontAnimate()
                .transform(new CircleTransform(context)).into(holder.userImage);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    listIDChoose.add(distanceUser.getKey());
                    listIDRemove.remove(distanceUser.getKey());
                } else {
                    listIDRemove.add(distanceUser.getKey());
                    listIDChoose.remove(distanceUser.getKey());
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }





}

