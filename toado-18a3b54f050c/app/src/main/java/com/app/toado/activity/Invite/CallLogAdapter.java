package com.app.toado.activity.Invite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.toado.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silent Knight on 24-05-2018.
 */

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    private List<CallLogItem> mItems;
    private Callbacks mCallbacks;
    Context context;
    String list="";

    public CallLogAdapter(ArrayList<CallLogItem> mItems, Callbacks callbacks,Context context) {
        this.mItems = mItems;
        this.mCallbacks = callbacks;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_invite_calls, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CallLogItem callLogItem = mItems.get(position);

        viewHolder.phoneNumber.setText(callLogItem.getPhoneNumber());
        viewHolder.checkBoxSelectItem.setChecked(callLogItem.getCheckedBox());
        viewHolder.name.setText(callLogItem.getName());

       // viewHolder.callDuration.setText("Call Duration(In Sec): " + callLogItem.getCallDuration());
        //viewHolder.callDate.setText("Call Date: " + callLogItem.getCallDate());
       // viewHolder.callType.setText("Call Type: " + callLogItem.getCallType());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public View view;
        public TextView phoneNumber, callDuration, callDate, callType;
        CheckBox checkBoxSelectItem;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            phoneNumber = (TextView) itemView.findViewById(R.id.no);
            checkBoxSelectItem = (CheckBox) itemView.findViewById(R.id.check);
            name=(TextView)itemView.findViewById(R.id.name);

            checkBoxSelectItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallLogItem callLogItem = mItems.get(getAdapterPosition());
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked()) {
                        callLogItem.setCheckedBox(true);
                        list=list+mItems.get(getAdapterPosition()).getPhoneNumber()+";";

                        Intent intent = new Intent("CallPref");
                        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                        intent.putExtra("callInvite",String.valueOf(list));

                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                        SharedPreferences sharedPref1 = context.getSharedPreferences("CallPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref1.edit();
                        editor.putString("callInvite", list);  // Pick a key here.
                        editor.commit();

                    } else {
                        callLogItem.setCheckedBox(false);
                    }
                    notifyDataSetChanged();
                }
            });
          //  callDuration = (TextView) itemView.findViewById(R.id.callDuration);
           // callDate = (TextView) itemView.findViewById(R.id.callDate);
           // callType = (TextView) itemView.findViewById(R.id.callType);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);

        }


        // onCustomClick Listener for view
        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            CallLogItem callLogItem = mItems.get(position);

            mCallbacks.onCustomClick(callLogItem);
        }


        //onLongClickListener for view
        @Override
        public boolean onLongClick(View v) {

            int position = getAdapterPosition();
            CallLogItem callLogItem = mItems.get(position);

            mCallbacks.onCustomLongClick(callLogItem);
            return true;
        }
    }}