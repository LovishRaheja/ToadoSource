package com.app.toado.activity.GroupChat.GroupChatContacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.app.toado.TinderChat.Chat.ChatContacts.SelectContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Silent Knight on 03-07-2018.
 */

public class GroupDatabaseAdapter {
    Cursor getPhoneNumber;
    ContentResolver resolver ;

    Context context;

    public GroupDatabaseAdapter(Context context) {
        this.context = context;
    }

    public List<SelectGroupContact> getData() {
        List<SelectGroupContact> data = new ArrayList<>();

        getPhoneNumber = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (getPhoneNumber != null) {
            Log.e("count", "" + getPhoneNumber.getCount());
            if (getPhoneNumber.getCount() == 0) {
                Toast.makeText(context, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            }

            while (getPhoneNumber.moveToNext()) {
                String id = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = getPhoneNumber.getString(getPhoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                SelectGroupContact selectUser = new SelectGroupContact();
                selectUser.setName(name);
                selectUser.setPhone(phoneNumber);
                data.add(selectUser);
            }
        } else {
            Log.e("Cursor close 1", "----");
        }

        getPhoneNumber.close();

        return data;
    }
}
