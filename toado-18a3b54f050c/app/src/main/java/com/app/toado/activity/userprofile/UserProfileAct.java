package com.app.toado.activity.userprofile;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.FirebaseChat.FirebaseChat;
import com.app.toado.R;
import com.app.toado.TinderChat.Chat.ChatActivity;
import com.app.toado.activity.BaseActivity;
import com.app.toado.helper.ChatHelper;
import com.app.toado.helper.CircleTransform;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.helper.CallHelper;
import com.app.toado.services.SinchCallService;
import com.app.toado.settings.UserSession;
import com.app.toado.model.User;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.app.toado.helper.ToadoConfig.DBREF;
import static com.app.toado.helper.ToadoConfig.DBREF_USER_PROFILES;

/**
 * Created by ghanendra on 15/06/2017.
 */

public class UserProfileAct extends BaseActivity {
    private TextView tvname, tvage, tvdistance, tvinterests,reqsent;
    private ImageView imgvUsrprof;
    ImageView btnchat,btncall;
    ImageView back;

     String name,profilePicUrl;
    private String usrkey = "nil";
    UserSession usrsess;
    private String dbTablekey;
    private Button sendreq;
    String mykey = "nokey", otherusername, imgurl;
    String profiletype;

    private MarshmallowPermissions marshmallowPermissions;
    private ImageView btnemail;
    private ImageView btnvid;
    private String dist;
    private LinearLayout reqlay,btnlay1,btnlay2;

    SinchCallService callserv;
    boolean mServiceBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        marshmallowPermissions = new MarshmallowPermissions(this);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (!marshmallowPermissions.checkPermissionForCalls())
            marshmallowPermissions.requestPermissionForCalls();

        btnchat = (ImageView) findViewById(R.id.imgchat);
        btncall = (ImageView) findViewById(R.id.imgcall);
        btnvid = (ImageView) findViewById(R.id.imgvideo);
        btnemail = (ImageView) findViewById(R.id.imgemail);
        tvname = (TextView) findViewById(R.id.tvname);
        tvage = (TextView) findViewById(R.id.tvage);
        tvdistance = (TextView) findViewById(R.id.tvdist);
        tvinterests = (TextView) findViewById(R.id.tvintrsts);
        imgvUsrprof = (ImageView) findViewById(R.id.imgprofile);
        sendreq=(Button)findViewById(R.id.sendreq);
        reqlay=(LinearLayout)findViewById(R.id.reqlay);
        reqsent=(TextView)findViewById(R.id.reqsent);
        btnlay1=(LinearLayout)findViewById(R.id.btnlay1);
        //btnlay2=(LinearLayout)findViewById(R.id.btnlay2);
        usrsess = new UserSession(this);
        mykey = usrsess.getUserKey();

        if (getIntent() != null)
            profiletype = getIntent().getStringExtra("profiletype");

        System.out.println("profiletype userprofileact" + profiletype);

        if (profiletype.matches("otherprofile")) {
            usrkey = getIntent().getStringExtra("keyval");
            dist = getIntent().getStringExtra("distance");
          //  btncall.setVisibility(View.VISIBLE);
           // btnchat.setVisibility(View.GONE);
            //btnvid.setVisibility(View.VISIBLE);
            //btnemail.setVisibility(View.VISIBLE);
        } else {
            UserSession us = new UserSession(this);
            usrkey = us.getUserKey();

        }

        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usrkey.matches("nil"))
                    callButtonClicked();
            }
        });

        btnvid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usrkey.matches("nil"))
                    videoCallButtonClicked();
            }
        });

        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(UserProfileAct.this, ChatActivity.class);
                Bundle b = new Bundle();
                b.putString("matchId", usrkey.toString());
                b.putString("name",otherusername.toString());

                intent.putExtras(b);
               startActivity(intent);
             //   ChatHelper.goToChatActivity(UserProfileAct.this,usrkey,otherusername,imgurl);
            }
        });

        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(UserProfileAct.this,ChatActivity.class);
                i.putExtra("matchId",usrkey);
                i.putExtra("name",otherusername);
                i.putExtra("profiletype",imgurl);
                startActivity(i);
            }
        });


        DBREF_USER_PROFILES.child(mykey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                    name = u.getName().toString();
                    profilePicUrl=u.getProfpicurl().toString();



                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        request();



        sendreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usrkey.matches("nil"))
                {
                    DBREF_USER_PROFILES.child(usrkey).child("Connections").child(mykey).child("Name").setValue(name);
                    DBREF_USER_PROFILES.child(usrkey).child("Connections").child(mykey).child("ProfilePicUrl").setValue(profilePicUrl);
                    DBREF_USER_PROFILES.child(usrkey).child("Connections").child(mykey).child("Status").setValue("recieved");
                    DBREF_USER_PROFILES.child(mykey).child("Friends").child(usrkey).child("Status").setValue("sent");
                    reqlay.setVisibility(View.GONE);
                    reqsent.setVisibility(View.VISIBLE);
                }


            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        request();



        System.out.println("user key from userprof act" + usrkey);
        if (!usrkey.matches("nil"))
            getOtherUserFirebaseData(usrkey);
        else {
            System.out.println("no key matches error userprofile act");
        }

    }

    private void getOtherUserFirebaseData(final String k) {
        System.out.println(usrkey + "db ref getusr lcoal data" + dist);
        DBREF_USER_PROFILES.child(k).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("user profiles datasnapshot" + dataSnapshot.toString());
                    User u = User.parse(dataSnapshot);
                    imgurl = u.getProfpicurl();
                    Glide.with(UserProfileAct.this).load(imgurl).into(imgvUsrprof);

                    System.out.println(u.getProfpicurl() + "user key from getuserlocaldata userprof act" + u.getName());
                    getlikes(k);
                    otherusername = u.getName();
                    tvname.setText(otherusername);
                    Calendar currentDate = Calendar.getInstance();

                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date birthdate = null;

                    try {
                        birthdate = myFormat.parse(u.getDob().lastIndexOf("/")+"-"+u.getDob().substring(0,1)+"-"+u.getDob().substring(u.getDob().indexOf("/")+1,u.getDob().indexOf("/")+2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Long time= currentDate.getTime().getTime() / 1000 - birthdate.getTime() / 1000;

                    int years = Math.round(time) / 31536000;


                    int age;
                    final Calendar calenderToday = Calendar.getInstance();
                    int currentYear = calenderToday.get(Calendar.YEAR);
                    int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
                    int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

                    age = currentYear - Integer.valueOf(u.getDob().substring(u.getDob().lastIndexOf("/")+1));
                   // Toast.makeText(UserProfileAct.this,String.valueOf(age), Toast.LENGTH_SHORT).show();
                  /**  if(DOBmonth > currentMonth) {
                        --age;
                    } else if(DOBmonth == currentMonth) {
                        if(DOBday > todayDay){
                            --age;
                        }
                    }*/


                    tvage.setText(", "+String.valueOf(age));
                    tvdistance.setText(dist + " miles away ");
                    if(u.getUserLikes().equals(""))
                    {
                        tvinterests.setText("No Interests Found");
                    }else
                        tvinterests.setText(u.getUserLikes());
                } else
                    System.out.println("no snapshot exists userprof act");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getlikes(final String key) {
        System.out.println("interests facebook");

        if (AccessToken.getCurrentAccessToken() != null) {
            new GraphRequest(

                    AccessToken.getCurrentAccessToken(),
                    "/" + AccessToken.getCurrentAccessToken().getUserId() + "/likes",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {

                                System.out.println("fb interests inside async task" + response.getJSONObject().getJSONArray("data").toString());
                                String interests="";
                               // int first = response.getJSONObject().getJSONArray("data").toString().indexOf("name");
                               // int firstid = response.getJSONObject().getJSONArray("data").toString().indexOf("id");

                                String result=response.getJSONObject().getJSONArray("data").toString();
                                for (int i=0;i<result.length()-2;i++)
                                {
                                    if(i<3){
                                    interests=interests+result.substring(result.indexOf("name", result.indexOf("name") +i)+7,result.indexOf("id", result.indexOf("id")+i )-3)+"\n";

                                    i++;}
                                    else{
                                        break;
                                    }


                                }
                               // updateUsrLikes(response.getJSONObject().getJSONArray("data").toString(), key);
                                updateUsrLikes(interests, key);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
    }

    private void updateUsrLikes(final String likes, String key) {

        DBREF_USER_PROFILES.child(key).child("userLikes").setValue(likes);
        System.out.println(DBREF_USER_PROFILES.child(key).child("userLikes") + "likes recd in update usr likes" + likes);


        DBREF_USER_PROFILES.child(usrkey).child("userLikes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                   tvinterests.setText(dataSnapshot.getValue().toString());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvinterests.setText(likes);
    }



    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    private void stopButtonClicked() {

        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    private void videoCallButtonClicked() {
        CallHelper.vidcallbtnClicked(getSinchServiceInterface(), marshmallowPermissions, mykey, usrkey, otherusername, imgurl, UserProfileAct.this);
    }

    private void callButtonClicked() {
        CallHelper.callbtnClicked(getSinchServiceInterface(), marshmallowPermissions, mykey, usrkey, otherusername, imgurl, UserProfileAct.this);
    }

    //call service BINDINGS

      @Override
    public void onServiceConnected() {

        try {
            callserv = getSinchServiceInterface().getService();
            getSinchServiceInterface().startClient(usrkey);
            mServiceBound = true;

        } catch (NullPointerException e) {
            //getSinchServiceInterface() in doStuff below throw null pointer error.
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mServiceBound = false;

    }

    @Override
    protected void onStop() {
        super.onStop();

     }

    @Override
    protected void onStart() {
        super.onStart();

        request();

      /**  DBREF_USER_PROFILES.child(usrkey).child("Connections").child(mykey).child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    reqlay.setVisibility(View.VISIBLE);
                    reqsent.setVisibility(View.GONE);
                    btnlay1.setVisibility(View.GONE);
                    //btnlay2.setVisibility(View.GONE);
                }
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.getValue().equals("accepted")) {
                        reqlay.setVisibility(View.GONE);
                        reqsent.setVisibility(View.GONE);
                        btnlay1.setVisibility(View.VISIBLE);
                        //btnlay2.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/




    }

    public void request()
    {
        DBREF_USER_PROFILES.child(mykey).child("Friends").child(usrkey).child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.exists()) {
                        myrequest();
                        //btnlay2.setVisibility(View.GONE);
                    }
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getValue().equals("accepted")) {
                            reqlay.setVisibility(View.GONE);
                            reqsent.setVisibility(View.GONE);
                            btnlay1.setVisibility(View.VISIBLE);
                            //btnlay2.setVisibility(View.VISIBLE);
                        }
                        if(dataSnapshot.getValue().equals("sent"))
                        {
                            reqlay.setVisibility(View.GONE);
                            reqsent.setVisibility(View.VISIBLE);

                            btnlay1.setVisibility(View.GONE);
                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
        });

    }

    public void myrequest()
    {


        DBREF_USER_PROFILES.child(mykey).child("Connections").child(usrkey).child("Status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    reqlay.setVisibility(View.VISIBLE);
                    reqsent.setVisibility(View.GONE);
                    btnlay1.setVisibility(View.GONE);
                    //btnlay2.setVisibility(View.GONE);
                }
                if (dataSnapshot.exists()) {

                  if(dataSnapshot.getValue().equals("recieved"))
                  {
                      reqlay.setVisibility(View.GONE);
                      reqsent.setVisibility(View.VISIBLE);

                      btnlay1.setVisibility(View.GONE);
                  }

                    if (dataSnapshot.getValue().equals("accepted")) {
                        reqlay.setVisibility(View.GONE);
                        reqsent.setVisibility(View.GONE);
                        btnlay1.setVisibility(View.VISIBLE);
                        //btnlay2.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
