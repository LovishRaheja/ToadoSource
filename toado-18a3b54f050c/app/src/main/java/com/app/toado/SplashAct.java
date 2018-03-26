package com.app.toado;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.app.toado.activity.register.PersonalDetailsAct;
import com.app.toado.activity.settings.DistancePreferencesActivity;
 import com.app.toado.services.LocServ;
 import com.app.toado.settings.UserSettingsSharedPref;
import com.app.toado.activity.ToadoBaseActivity;
import com.app.toado.activity.main.MainAct;
import com.app.toado.activity.register.MobileRegisterAct;
import com.app.toado.settings.UserSession;

public class SplashAct extends ToadoBaseActivity {
    Button btnSignin, btnSignup;
    private UserSession session;
    private ImageView logo;
    Context context;

    private UserSettingsSharedPref userSettingsSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new UserSession(SplashAct.this);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //logo = (ImageView)findViewById(R.id.logo);


        /**ObjectAnimator fadeOut = ObjectAnimator.ofFloat(logo, "alpha",  1f, .3f);
        fadeOut.setDuration(2000);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", .3f, 1f);
        fadeIn.setDuration(2000);

        final AnimatorSet mAnimationSet = new AnimatorSet();

        mAnimationSet.play(fadeIn).after(fadeOut);

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimationSet.start();
            }
        });
        mAnimationSet.start();*/
        userSettingsSharedPref = new UserSettingsSharedPref(SplashAct.this);
        System.out.println("splash activity session data" + session.getUserKey());
        if (session.isolduser() == true) {
            Intent in = new Intent(SplashAct.this, MainAct.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(in);
            Intent in2 = new Intent(this, LocServ.class);
            in2.putExtra("keyval", session.getUserKey());
            System.out.println("starting service from splash act with key" + session.getUserKey());
            startService(in2);
            finish();

        }
        btnSignin = (Button) findViewById(R.id.btnsignin);
        btnSignup = (Button) findViewById(R.id.btnsignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashAct.this, MobileRegisterAct.class);
                intent.putExtra("method", "signup");
                startActivity(intent);
            }
        });
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashAct.this, MobileRegisterAct.class);
                intent.putExtra("method", "signin");
                startActivity(intent);
            }
        });
    }

    public void signinAct(View view) {
        System.out.println("signin act");
    }

    public void singupAct(View view) {
        System.out.println("singup act");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
