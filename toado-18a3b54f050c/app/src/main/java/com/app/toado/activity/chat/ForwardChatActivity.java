package com.app.toado.activity.chat;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.adapter.ContactsAdapter;
import com.app.toado.adapter.NewChatAdapter;
import com.app.toado.adapter.RecentChatsAdapter;
import com.app.toado.helper.ContactVO;
import com.app.toado.helper.GetTimeStamp;
import com.app.toado.helper.MyXMPP2;
import com.app.toado.model.ChatMessageForward;
import com.app.toado.model.PhoneContacts;
import com.app.toado.model.realm.ActiveChatsRealm;
import com.app.toado.model.realm.ChatMessageRealm;
import com.app.toado.settings.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ForwardChatActivity extends AppCompatActivity {
    private static final String[] CONTACTS_PERMISSION = {
            Manifest.permission.READ_CONTACTS};

    HashMap<String, String> fireContacts;
    EditText etSearch;
    Cursor cursor;
    String name, phonenumber;
    ArrayList<String> phnlistIds;
    ArrayList<PhoneContacts> phnlist;
    private RecyclerView recyclerView;
    private ContactsAdapter mAdapterRecents;
    ArrayList<ChatMessageForward> forwardList;
    final String TAG = "NEWCHATACTIVITY";
    private ArrayList<ActiveChatsRealm> listRecent;
    private ArrayList<String> liststring;
    private ArrayList<ActiveChatsRealm> listReceiverId;
    UserSession us;
    //  private ArrayList<ActiveChatsRealm> listOthers;
    TextView tvitems;
    FloatingActionButton sendbutton;
    Realm mRealm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forwardchat);
        mRealm = Realm.getDefaultInstance();

        if (getIntent().getSerializableExtra("arrmsgids") != null) {
            forwardList = (ArrayList<ChatMessageForward>) getIntent().getSerializableExtra("arrmsgids");
            for (ChatMessageForward a : forwardList)
                Log.d(TAG, "forwardList " + a.getMsgstring());
        }

       // tvitems = (TextView) findViewById(R.id.tvselectednames);

        us = new UserSession(this);
        recyclerView = (RecyclerView) findViewById(R.id.rvContacts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        listRecent = new ArrayList<>();
        //mAdapterRecents = new ContactsAdapter(liststring,this,us);
        //recyclerView.setAdapter(mAdapterRecents);
        sendbutton = (FloatingActionButton) findViewById(R.id.sendButton);
       // loadRecentData();
       // getAllContacts();
    }

    private void getAllContacts() {
        List<ContactVO> contactVOList = new ArrayList();
        ContactVO contactVO;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactVO = new ContactVO();
                    contactVO.setContactName(name);

                    Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactVO.setContactNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    Cursor emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCursor.moveToNext()) {
                        String emailId = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    contactVOList.add(contactVO);
                }
            }

            ContactsAdapter contactAdapter = new ContactsAdapter(contactVOList, getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(contactAdapter);
        }
    }

    public void onBack(View view) {
        finish();
    }

   /** public void showSelectedNames(ArrayList<String> selectedlist, int show, ArrayList<ActiveChatsRealm> activeList2) {
        if (show == 1) {
            liststring = selectedlist;
            listReceiverId = activeList2;
            sendbutton.setVisibility(View.VISIBLE);
            tvitems.setVisibility(View.VISIBLE);
            String sel = "";
            String name = "";
            for (String sa : selectedlist)
                sel = sel + "," + sa;

            String a = String.valueOf(sel.charAt(0));
            if (a.matches(",")) {
                name = sel.substring(1);
            } else
                name = sel;

            Log.d(TAG, "name to set" + name);
            tvitems.setText(name);

        } else {
            sendbutton.setVisibility(View.GONE);
            tvitems.setVisibility(View.GONE);
            liststring.clear();
            listReceiverId.clear();
        }
    }

    private void loadRecentData() {
        Sort sort[] = {Sort.DESCENDING};
        String[] fieldNames = {"msgid"};
        RealmResults<ActiveChatsRealm> shows = mRealm.where(ActiveChatsRealm.class).findAll();
        for (int i = 0; i < shows.size(); i++) {
            listRecent.add(shows.get(i));
            mAdapterRecents.notifyDataSetChanged();
        }
        mAdapterRecents.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    public void sendForwards(View view) {
        Toast.makeText(this, "Sending messages..", Toast.LENGTH_SHORT).show();
        MyXMPP2 myXMPP2 = MyXMPP2.getInstance(this, getString(R.string.server), us.getUserKey());
        for (ChatMessageForward msgfor : forwardList) {
            for (ActiveChatsRealm cmr : listReceiverId) {
                ChatMessageRealm cm = new ChatMessageRealm(us.getUserKey() + cmr.getOtherkey(), cmr.getOtherkey(), msgfor.getMsgstring(), us.getUserKey(), GetTimeStamp.timeStampTime(), GetTimeStamp.timeStampDate(), msgfor.getMsgtype(), String.valueOf(GetTimeStamp.Id()), "1", msgfor.getMsgweburl(), msgfor.getMsglocalurl(), msgfor.getMediathumbnail());
                myXMPP2.sendMessage(cm);
            }
        }

        finish();
    }
*/

}
