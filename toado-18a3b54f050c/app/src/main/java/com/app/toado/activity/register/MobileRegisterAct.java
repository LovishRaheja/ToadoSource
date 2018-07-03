package com.app.toado.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.ToadoBaseActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.helper.CloseKeyboard;
import com.app.toado.settings.UserSession;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.sinch.verification.Logger;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.SinchVerification;

import org.w3c.dom.Text;

import static com.app.toado.helper.ToadoConfig.DBREF_USER_MOBS;




public class MobileRegisterAct extends ToadoBaseActivity {
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
    CheckBox checkTerms;
    TextView termsText;
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

    private LoginButton loginButton;
     TextView textsign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        back=(ImageView)findViewById(R.id.back);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();


            }
        });

        UserSession usr = new UserSession(this);
        if (usr.isolduser()) {
            startActivity(new Intent(this, MainAct.class));
            finish();
        }

        checkTerms=(CheckBox)findViewById(R.id.checkTerms);
        termsText=(TextView)findViewById(R.id.termsText);

        textsign=(TextView)findViewById(R.id.textsign);
        ccp = (CountryCodePicker) findViewById(R.id.ccp1);
        Intent intent = getIntent();
        if (intent != null) {
            option = intent.getStringExtra("method");
            if (option.equals("signin")) {
                textsign.setText("Sign In");
                checkTerms.setVisibility(View.GONE);
                termsText.setVisibility(View.GONE);
            }
            else{
                textsign.setText("Sign Up");
            }
        }


        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);

        final CloseKeyboard cb = new CloseKeyboard();
        mPhoneNumber.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println("on editor action mob register act");
                return cb.closeKeyb2(mPhoneNumber, MobileRegisterAct.this, v, actionId, event);
            }

        });


        mSmsButton = (Button) findViewById(R.id.smsVerificationButton);
        mFlashCallButton = (Button) findViewById(R.id.callVerificationButton);

        mSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("sms button clicked");
                if (option.equals("signup")) {
                    if (checkTerms.isChecked()) {
                        mSmsButton.setEnabled(true);
                        mFlashCallButton.setEnabled(true);
                        if (!mPhoneNumber.getText().toString().trim().matches(""))
                            checkmeth("sms");
                        else
                            Toast.makeText(MobileRegisterAct.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    } else if (!checkTerms.isChecked()) {
                        mSmsButton.setEnabled(false);
                        mFlashCallButton.setEnabled(false);
                        Toast.makeText(MobileRegisterAct.this, "Please agree to above terms and conditions before proceeding", Toast.LENGTH_SHORT).show();
                        mSmsButton.setEnabled(true);
                        mFlashCallButton.setEnabled(true);
                    }
                }else if (option.equals("signin")) {
                    if (!mPhoneNumber.getText().toString().trim().matches(""))
                        checkmeth("sms");
                    else
                        Toast.makeText(MobileRegisterAct.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mFlashCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (option.equals("signup")) {
                    if (checkTerms.isChecked()) {
                        mSmsButton.setEnabled(true);
                        mFlashCallButton.setEnabled(true);
                        System.out.println("flash button clicked");
                        if (!mPhoneNumber.getText().toString().trim().matches(""))
                            checkmeth("flashcall");
                        else
                            Toast.makeText(MobileRegisterAct.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                    } else if (!checkTerms.isChecked()) {
                        mSmsButton.setEnabled(false);
                        mFlashCallButton.setEnabled(false);
                        Toast.makeText(MobileRegisterAct.this, "Please agree to above terms and conditions before proceeding", Toast.LENGTH_SHORT).show();
                        mSmsButton.setEnabled(true);
                        mFlashCallButton.setEnabled(true);
                    }
                }else if (option.equals("signin")) {
                    System.out.println("flash button clicked");
                    if (!mPhoneNumber.getText().toString().trim().matches(""))
                        checkmeth("flashcall");
                    else
                        Toast.makeText(MobileRegisterAct.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                }
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
                    Toast.makeText(MobileRegisterAct.this, "Please select country code.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void openActivity(String phoneNumber1, String method) {
        System.out.println("open activity called");
        Intent verification = new Intent(this, OtpAct.class);
        verification.putExtra(INTENT_PHONENUMBER, phoneNumber1);
        verification.putExtra(INTENT_METHOD, method);
        verification.putExtra("option", option);
        System.out.println(" mobile register act phn num" + phoneNumber1);
        startActivity(verification);
    }

    public void checkmeth(final String view) {
        if (option.equals("signin")) {
            textsign.setText("Sign In");
            DatabaseReference checkDuplicate = DBREF_USER_MOBS.child(getE164Number()).getRef();
            checkDuplicate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (view == "sms") {
                            System.out.println(getE164Number() + "sms button clicked 1" + mCountryIso);
                            openActivity(getE164Number(), "sms");
                        } else if (view == "flashcall") {
                            System.out.println(getE164Number() + "flash button clicked 1" + mCountryIso);
                            System.out.println("flashcall button clicked 1");
                            openActivity(getE164Number(), "flashcall");
                        }
                    } else {
                        Toast.makeText(MobileRegisterAct.this, "Number is not registered, please signup first.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            System.out.println("else signup called");


            DatabaseReference checkDuplicate = DBREF_USER_MOBS.child(getE164Number()).getRef();
            checkDuplicate.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("datasnap" + dataSnapshot);
                    if (!dataSnapshot.exists()) {
                        System.out.println("view datasnap mobile register act" + view);
                        if (view.matches("sms")) {
                            System.out.println("sms button clicked inside2");
                            System.out.println(getE164Number() + "sms button clicked 2" + mCountryIso);
                            openActivity(getE164Number(), SMS);
                        } else if (view.matches("flashcall")) {
                            System.out.println("flashcall button clicked inside2");
                            System.out.println(getE164Number() + "flash button clicked 2" + mCountryIso);
                            openActivity(getE164Number(), FLASHCALL);
                        }

                    } else {
                        Toast.makeText(MobileRegisterAct.this, "Number is already registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public String getE164Number() {
        System.out.println((ccp.getFullNumberWithPlus() + mPhoneNumber.getText().toString() + " phn num utils mobile register act"));
        return (ccp.getFullNumberWithPlus() + mPhoneNumber.getText().toString()).trim();
    }

}
