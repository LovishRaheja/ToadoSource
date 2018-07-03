package com.app.toado.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.app.toado.Manifest;
import com.app.toado.helper.MarshmallowPermissions;
import com.app.toado.services.SinchCallService;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.net.URL;

import me.pushy.sdk.Pushy;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by aksha on 9/10/2017.
 */

public class ToadoAppCompatActivity extends AppCompatActivity implements ServiceConnection {

    private SinchCallService.SinchServiceInterface mSinchServiceInterface;
    private MarshmallowPermissions marshmallowPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplicationContext().bindService(new Intent(this, SinchCallService.class), this,
                BIND_AUTO_CREATE);
        Pushy.listen(this);
        marshmallowPermissions = new MarshmallowPermissions(this);

            marshmallowPermissions.requestPermissionForReadExternalStorage();




    }


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchCallService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchCallService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchCallService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchCallService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                // Assign a unique token to this device
                String deviceToken = Pushy.register(getApplicationContext());

                // Log it for debugging purposes
                Log.d("MyApp", "Pushy device token: " + deviceToken);

                // Send the token to your backend server via an HTTP GET request
                new URL("https://5b1501075f69fefe7618a5a2/register/device?token=" + deviceToken).openConnection();
            } catch (Exception exc) {
                // Return exc to onPostExecute
                return exc;
            }

            // Success
            return null;
        }

        @Override
        protected void onPostExecute(Exception exc) {
            // Failed?
            if (exc != null) {
                // Show error as toast message
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            // Succeeded, do something to alert the user
        }
    }
}