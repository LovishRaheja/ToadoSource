package com.app.toado.Contact;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.toado.R;
import com.app.toado.activity.register.MobileRegisterAct;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import static com.app.toado.helper.ToadoConfig.DBREF_USERS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class ContactsAdapter extends ArrayAdapter<Contact> {
	private String usrkey = "nil";
	String name,phone,email;


	public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
		super(context, 0, contacts);
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


		tvPhone.setText("");

		UserSession us = new UserSession(getContext());
		usrkey = us.getUserKey();

		tvName.setText(contact.name);

		if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}



		DBREF_USER_PROFILES.getRef().addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		/**DatabaseReference checkNumber = DBREF_USER_MOBS.getRef();
		checkNumber.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				if(dataSnapshot.exists())
				{
					if(dataSnapshot.getValue().toString().equals(contact.numbers.get(0).number)|dataSnapshot.getKey().toString().equals(contact.numbers.get(0).number.substring(3,contact.numbers.get(0).number.length())))
					{


					}
				}

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});


		/**if (contact.numbers.size() > 0 && contact.numbers.get(0) != null) {
			tvPhone.setText(contact.numbers.get(0).number);
		}
		if (contact.emails.size() > 0 && contact.emails.get(0) != null) {
			tvEmail.setText(contact.emails.get(0).address);
		}*/
		return view;
	}


}