package com.app.toado.Contact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.FirebaseChat.ForwardChatMessage;
import com.app.toado.FirebaseChat.Message;
import com.app.toado.FirebaseChat.StaticConfig;
import com.app.toado.R;
import com.app.toado.activity.register.MobileRegisterAct;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USERS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class ContactsAdapter extends ArrayAdapter<Contact> {
	private String usrkey = "nil";
	String name,phone,email;

	private UserSession session;
	private String userkey;
	private String roomId;
	DatabaseReference mDatabaseUser, mDatabaseChat;
	String chatId;
String otheruserkey;

	public ContactsAdapter(Context context, ArrayList<Contact> contacts,String otheruserkey) {
		super(context, 0, contacts);
		this.otheruserkey=otheruserkey;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		final Contact contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.adapter_contact_item, parent, false);
		}
		// Populate the data into the template view using the data object
		final TextView tvName = (TextView) view.findViewById(R.id.tvName);

		final TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);

		LinearLayout contactLayout=(LinearLayout)view.findViewById(R.id.contactLayout);
		mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
		mDatabaseUser =DBREF_USER_PROFILES.child(session.getUserKey()).child("connections").child("matches").child(otheruserkey).child("ChatId");
		mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if (dataSnapshot.exists()){
					chatId = dataSnapshot.getValue().toString();
					mDatabaseChat = mDatabaseChat.child(chatId);

				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		session = new UserSession(getContext());
		Intent intentData = ((Activity)getContext()).getIntent();
		roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);

		if (((Activity)getContext()).getIntent().getStringExtra("mykey") != null) {
			userkey = ((Activity)getContext()).getIntent().getStringExtra("mykey");
			System.out.println("1 homefragment from session" + userkey);
		} else {
			System.out.println("2 homefragment from session" + userkey);
			userkey = session.getUserKey();
		}

	//	session = new UserSession(getContext());
		tvPhone.setText("");

		UserSession us = new UserSession(getContext());
		usrkey = us.getUserKey();
		//roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);



		tvName.setText(contact.name);

		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}

		contactLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg="Name"+contact.name+"Number"+contact.numbers.get(0).number;

				if (msg.length() > 0) {

					DatabaseReference newMessageDb = mDatabaseChat.push();

					Map newMessage = new HashMap();
					newMessage.put("createdByUser", session.getUserKey());
					newMessage.put("text", msg.toString());
					newMessage.put("timestamp",System.currentTimeMillis());

					newMessageDb.setValue(newMessage);
				}

				Intent i=new Intent(getContext(), FirebaseChat.class);
				//i.putExtra("name",contact.name);
				//i.putExtra("number",contact.numbers.get(0).number);
				getContext().startActivity(i);
			}
		});

		return view;
	}


}