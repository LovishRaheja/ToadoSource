package com.app.toado.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.model.CallDetails;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by ghanendra on 02/07/2017.
 */

public class CallLogsAdapter extends RecyclerView.Adapter<CallLogsAdapter.MyViewHolder> {

    private List<CallDetails> phnList;
    private Context contx;
    UserSession us;
    Activity a;
    SinchCallService callserv;

    boolean mServiceBound = false;

    private MarshmallowPermissions marshmallowPermissions;

    public CallLogsAdapter(List<CallDetails> phnList, Context contx) {
        this.phnList = phnList;
        this.contx = contx;
        us = new UserSession(contx);
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


            marshmallowPermissions = new MarshmallowPermissions((Activity) view.getContext());

            if (!marshmallowPermissions.checkPermissionForCalls())
                marshmallowPermissions.requestPermissionForCalls();



        }
    }


    @Override
    public CallLogsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_callog, parent, false);

      //  marshmallowPermissions = new MarshmallowPermissions(a);

       // if (!marshmallowPermissions.checkPermissionForCalls())
           // marshmallowPermissions.requestPermissionForCalls();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CallLogsAdapter.MyViewHolder holder, int position) {

        final SinchCallService.SinchServiceInterface sinchServiceInterface=null;
        CallDetails phn = phnList.get(position);
        System.out.println(phn.getDuration() + " call logs adapter " + phn.getOtherusrname() + phn.getDuration());
        holder.title.setText(phn.getOtherusrname());
        final String otherusername=phn.getOtherusrname();
        final String otheruserkey=phn.getCalleruid();

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
                }
                else{


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
                    .transform(new CircleTransform(contx)).error(R.drawable.nouser).into(holder.imgv);
        }
        else{
            Glide.with(contx).load(phn.getProfpicurl()).dontAnimate()
                    .transform(new CircleTransform(contx)).error(R.drawable.nouser).into(holder.imgv);

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
