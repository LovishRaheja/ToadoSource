package com.app.toado.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.Account.PrivacyAct;
import com.app.toado.activity.Account.SecurityAct;
import com.app.toado.activity.chat.ChatActivity;
import com.app.toado.helper.ContactVO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.codetroopers.betterpickers.timezonepicker.TimeZoneFilterTypeAdapter.TAG;

/**
 * Created by Silent Knight on 14-02-2018.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>{



    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref=DBREF_USER_MOBS;
    private List<ContactVO> contactVOList;
    private Context mContext;
    public ContactsAdapter(List<ContactVO> contactVOList, Context mContext){
        this.contactVOList = contactVOList;
        this.mContext = mContext;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_contact_view, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, int position) {
        final ContactVO contactVO = contactVOList.get(position);
        holder.tvContactName.setText(contactVO.getContactName());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    String key= snapshot.getKey();
                    String value=snapshot.getValue().toString();
                    if(value.toString().equals(contactVO.getContactNumber().toString()))
                    {
                        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
                        holder.tvContactName.setText(contactVO.getContactName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // Toast.makeText(ListUser.this,databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        holder.tvPhoneNumber.setText(contactVO.getContactNumber());
        holder.gotochat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch (holder.tvContactName.getText().toString()){

                        mContext.startActivity(new Intent(mContext, ChatActivity.class));
                       // break;


                //}
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactVOList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder{

        ImageView ivContactImage;
        TextView tvContactName;
        TextView tvPhoneNumber;
        LinearLayout gotochat;;

        public ContactViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = (TextView) itemView.findViewById(R.id.tvPhoneNumber);
            gotochat=(LinearLayout)itemView.findViewById(R.id.gotochat);
        }
    }
}