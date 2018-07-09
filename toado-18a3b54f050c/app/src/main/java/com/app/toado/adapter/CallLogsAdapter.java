package com.app.toado.adapter;


import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.activity.BaseActivity;
import com.app.toado.activity.ToadoAppCompatActivity;
import com.app.toado.activity.userprofile.UserProfileAct;
import com.app.toado.helper.CallHelper;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.model.CallDetails;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;


/**
 * Created by ghanendra on 02/07/2017.
 */

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> implements ServiceConnection{

    private List<CallDetails> phnList;
    private Context contx;
    UserSession us;
    Activity a;
    SinchCallService callserv;

    private SinchCallService.SinchServiceInterface mSinchServiceInterface;


    boolean mServiceBound = false;
    private ToadoAppCompatActivity toadoAppCompatActivity;

    private MarshmallowPermissions marshmallowPermissions;

    public CallLogsAdapter(List<CallDetails> phnList, Context contx) {
        this.phnList = phnList;
        this.contx = contx;
        us = new UserSession(contx);
    }

    public void onServiceConnected() {

        try {
            callserv = getSinchServiceInterface().getService();
            getSinchServiceInterface().startClient(us.getUserKey());
            mServiceBound = true;
        } catch (NullPointerException e) {
            //getSinchServiceInterface() in doStuff below throw null pointer error.
        }
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {


        if (SinchCallService.class.getName().equals(name.getClassName())) {
            mSinchServiceInterface = (SinchCallService.SinchServiceInterface) service;
            onServiceConnected();
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (SinchCallService.class.getName().equals(name.getClassName())) {
            mSinchServiceInterface = null;

        }

            mServiceBound = false;
    }
    protected SinchCallService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        //public TextView duration;
        public TextView timestamp;
        public TextView type;//outgoing incoming?
        public TextView status;
        public ImageView imgv,imgCallStatus;
        public RelativeLayout relcont;






        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.tvcontactname);
            timestamp = (TextView) view.findViewById(R.id.tvcalltimestamp);
            //duration = (TextView) view.findViewById(R.id.tvcallduration);
            type = (TextView) view.findViewById(R.id.tvcalltype);
            status = (TextView) view.findViewById(R.id.tvcallstatus);
            imgv = (ImageView) view.findViewById(R.id.imgcontact);
            imgCallStatus = (ImageView) view.findViewById(R.id.imgcallstatus);
            relcont = (RelativeLayout) view.findViewById(R.id.relcontact);
            toadoAppCompatActivity=new ToadoAppCompatActivity();


            marshmallowPermissions = new MarshmallowPermissions((Activity) view.getContext());

            if (!marshmallowPermissions.checkPermissionForCalls())
                marshmallowPermissions.requestPermissionForCalls();



        }
    }


    @Override
    public CallLogsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_callog, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CallLogsAdapter.MyViewHolder holder, int position) {

       // final SinchCallService.SinchServiceInterface sinchServiceInterface=null;
        final CallDetails phn = phnList.get(position);
        System.out.println(phn.getDuration() + " call logs adapter " + phn.getOtherusrname() + phn.getDuration());
        holder.title.setText(phn.getOtherusrname());
        final String otherusername=phn.getOtherusrname();
        final String otheruserkey=phn.getReceiveruid();
        final String otherprofileimage=phn.getProfpicurl();


//        final String imgurl=phn.getProfpicurl().toString();

        if (us.getUserKey().matches(phn.getCalleruid())) {
            holder.type.setText("outgoing "+phn.getCalltype()+" call");
        } else {
            holder.type.setText("incoming "+phn.getCalltype()+" call"   );
        }

        if(holder.type.getText().equals("outgoing voice call")||holder.type.getText().equals("incoming voice call"))
        {
            holder.imgCallStatus.setImageResource(R.drawable.callprofile);
        }
        else{
            holder.imgCallStatus.setImageResource(R.drawable.videoprofile);

        }

        holder.imgCallStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(holder.type.getText().equals("outgoing voice call")||holder.type.getText().equals("incoming voice call")) {
                    Toast.makeText(v.getContext(), "outgoing voice call", Toast.LENGTH_LONG).show();
                    CallHelper.vidcallbtnClicked(getSinchServiceInterface(), marshmallowPermissions, us.getUserKey(),otheruserkey, otherusername, otherprofileimage,contx);
                }
                else{

                    CallHelper.vidcallbtnClicked(getSinchServiceInterface(), marshmallowPermissions, us.getUserKey(), phn.getCalleruid(), otherusername, phn.getProfpicurl(),contx);
                  //  CallHelper.vidcallbtnClicked(,marshmallowPermissions, us.getUserKey(), otheruserkey, otherusername, holder.imgCallStatus, CallLogsAdapter.this);
                    Toast.makeText(v.getContext(), "outgoing video call", Toast.LENGTH_LONG).show();

                }

            }
        });

        holder.timestamp.setText(phn.getTimestamp().toString().toUpperCase());
       // holder.duration.setText(CallHelper.formatTimeCallLogs(Long.parseLong(phn.getDuration())));
        switch (phn.getStatus()) {
            case "CANCELED":
                if (us.getUserKey().matches(phn.getCalleruid())) {
                    holder.status.setText("Outgoing Cancelled");
                    holder.status.setTextColor(Color.RED);
                }
               // statusMeth("Outgoing Cancelled", "Missed Call", phn, holder, R.color.bpDarker_red);
                break;
            case "DENIED":
                if (us.getUserKey().matches(phn.getCalleruid())) {
                    holder.status.setText("Receiver Declined");
                    holder.status.setTextColor(Color.RED);
                }
               // statusMeth("Receiver Declined", "Missed Call", phn, holder, R.color.bpDarker_red);
                break;
            case "completed":
                if (us.getUserKey().matches(phn.getCalleruid())) {
                    holder.status.setText("Completed");
                    holder.status.setTextColor(Color.GREEN);
                }
                //statusMeth("Completed", "Completed", phn, holder, R.color.light_green);
                break;
            case "NO_ANSWER":
                if (us.getUserKey().matches(phn.getCalleruid())) {
                    holder.status.setText("Call Not Answered");
                    holder.status.setTextColor(Color.RED);
                }

                //statusMeth("Call Not Answered", "Missed Call", phn, holder, R.color.bpDarker_red);
                break;
            case "TIMEOUT":
                if (us.getUserKey().matches(phn.getCalleruid())) {
                    holder.status.setText("Call Not Connected");
                    holder.status.setTextColor(Color.RED);
                }


                //statusMeth("Call Not Answered", "Missed Call", phn, holder, R.color.bpDarker_red);
                break;

        }

       if(phn.getCalltype().equals("voice")) {
            Glide.with(contx).load(phn.getProfpicurl()).dontAnimate()
                    .transform(new CircleTransform(contx)).error(R.drawable.person).into(holder.imgv);
        }
        else{
            Glide.with(contx).load(phn.getProfpicurl()).dontAnimate()
                    .transform(new CircleTransform(contx)).error(R.drawable.person).into(holder.imgv);

        }
    }

    @Override
    public int getItemCount() {
        return phnList.size();
    }

    public void updateList(List<CallDetails> mList) {
        phnList = mList;
        notifyDataSetChanged();
    }

    public void statusMeth(String callermsg, String receivermsg, CallDetails phn, MyViewHolder holder, int colr) {
        System.out.println("status meth calllogs  adapter" + callermsg + receivermsg);

       /** if (us.getUserKey().matches(phn.getCalleruid())) {
            holder.status.setText(callermsg);
            holder.status.setTextColor(R.color.bpDarker_red);
        } else {
            holder.status.setText(receivermsg);
            holder.status.setTextColor(colr);

        }*/



    }



}
