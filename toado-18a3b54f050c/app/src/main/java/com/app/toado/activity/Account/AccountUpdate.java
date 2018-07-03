package com.app.toado.activity.Account;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.SplashAct;
import com.app.toado.activity.BaseActivity;
import com.app.toado.activity.BlockedUsers.BlockedUsers;
import com.app.toado.activity.register.MobileRegisterAct;
import com.app.toado.activity.register.OtpAct;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.CloseKeyboard;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.model.User;
import com.app.toado.settings.UserSession;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.hbb20.CountryCodePicker;
import com.sinch.verification.Logger;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.SinchVerification;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

public class AccountUpdate extends BaseActivity {

    public static final String SMS = "sms";
    public static final String FLASHCALL = "flashcall";
    public static final String INTENT_PHONENUMBER = "phonenumber";
    public static final String INTENT_METHOD = "method";
    public static final String INTENT_COUNTRYCODE = "cc";
    CountryCodePicker ccp;
    String countrycode;
    private EditText mPhoneNumber;
    private Button mSmsButton;
    private Button mFlashCallButton;
    private String mCountryIso;
    private TextWatcher mNumberTextWatcher;
    private String option;
    ImageView back;
    RelativeLayout blockedLayout;
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    static {
        // Provide an external logger
        SinchVerification.setLogger(new Logger() {
            @Override
            public void println(int priority, String tag, String message) {
                // forward to logcat
                android.util.Log.println(priority, tag, message);
            }
        });
    }

    UserSession usess;
    private Activity context;
    Button logout,delete;
    String phone="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_update);
        usess=new UserSession(this);
        logout=(Button)findViewById(R.id.logout);
        delete=(Button)findViewById(R.id.deleteAccount);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        blockedLayout=(RelativeLayout)findViewById(R.id.blockedLayout);
        blockedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(AccountUpdate.this, BlockedUsers.class);
                startActivity(i);
            }
        });



        ccp = (CountryCodePicker) findViewById(R.id.ccp1);
        Intent intent = getIntent();
        if (intent != null) {
            option = intent.getStringExtra("method");
        }
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);

        final CloseKeyboard cb = new CloseKeyboard();
        mPhoneNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println("on editor action mob register act");
                return cb.closeKeyb2(mPhoneNumber, AccountUpdate.this, v, actionId, event);
            }

        });
        mSmsButton = (Button) findViewById(R.id.smsVerificationButton);
        mFlashCallButton = (Button) findViewById(R.id.callVerificationButton);

        mSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sms button clicked");



                if (!mPhoneNumber.getText().toString().trim().matches(""))
                    openActivity(getE164Number(),"sms");
                else
                    Toast.makeText(AccountUpdate.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
        });

        mFlashCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("flash button clicked");
                if (!mPhoneNumber.getText().toString().trim().matches(""))
                    openActivity(getE164Number(),"call");
                else
                    Toast.makeText(AccountUpdate.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
        });



        mCountryIso = PhoneNumberUtils.getDefaultCountryIso(this);

        countrycode = ccp.getSelectedCountryCode();
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                System.out.println(ccp.getSelectedCountryCodeWithPlus() + "country code de");
                countrycode = ccp.getSelectedCountryName();
                if (countrycode != null) {
                    mCountryIso = countrycode;
                } else {
                    Toast.makeText(AccountUpdate.this, "Please select country code.", Toast.LENGTH_SHORT).show();
                }
            }
        });




        //usrkey = us.getUserKey();
        DBREF_USER_PROFILES.child(usess.getUserKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                   // String imgurl = u.getProfpicurl();

                 phone=u.getPhone().toString();


                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToadoAlerts.showLogoutAlert(AccountUpdate.this,usess);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                removeNumber();


                removeData();


            }
        });
    }


    private void openActivity(String phoneNumber1, String method) {
        System.out.println("open activity called");
        Intent verification = new Intent(this, VerifyNumber.class);
        verification.putExtra(INTENT_PHONENUMBER, phoneNumber1);
        verification.putExtra(INTENT_METHOD, method);
        verification.putExtra("option", option);
        System.out.println(" mobile register act phn num" + phoneNumber1);
        startActivity(verification);
    }


    public String getE164Number() {
        System.out.println((ccp.getFullNumberWithPlus() + mPhoneNumber.getText().toString() + " phn num utils mobile register act"));
        return (ccp.getFullNumberWithPlus() + mPhoneNumber.getText().toString()).trim();
    }
    public void removeNumber()
    {
        Query deleteQuery = DBREF_USER_MOBS.orderByChild("usermob").equalTo(phone);
        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                    deleteSnapshot.getRef().removeValue();


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void removeData()
    {

        Query deleteQuery = DBREF_USER_PROFILES.orderByChild("phone").equalTo(phone);


        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                    deleteSnapshot.getRef().removeValue();



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void goToSplash()
    {
        Intent i=new Intent(AccountUpdate.this, SplashAct.class);
        startActivity(i);


    }

}
