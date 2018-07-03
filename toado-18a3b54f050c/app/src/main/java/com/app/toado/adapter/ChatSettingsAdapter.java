package com.app.toado.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.toado.R;
import com.app.toado.activity.Account.AccountAct;
import com.app.toado.activity.Account.AccountUpdate;
import com.app.toado.activity.profile.ProfileAct;
import com.app.toado.activity.settings.DistancePreferencesActivity;
import com.app.toado.helper.ToadoAlerts;
import com.app.toado.settings.UserSession;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Silent Knight on 13-02-2018.
 */

public class ChatSettingsAdapter extends RecyclerView.Adapter<ChatSettingsAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{
        ArrayList<String> list = new ArrayList<>();

    private Activity context;
        UserSession usess;
    String data="";
    private static final int PERMISSION_REQUEST_CODE = 1;

    public ChatSettingsAdapter(ArrayList<String> list, Activity context, UserSession usess) {
        this.context = context;
        this.list = list;
        this.usess=usess;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView title;

    public MyViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.tvtitle);
    }

}

    @Override
    public ChatSettingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_settings, parent, false);
        return new ChatSettingsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatSettingsAdapter.MyViewHolder holder, int position) {
        String topic = list.get(position);
        holder.title.setText(topic);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.title.getText().toString()){

                    case "Email Chat":

                        FirebaseDatabase.getInstance().getReference().child("message").child("null").getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {

                                    data=data+snapshot.getValue().toString()+"\n"+"\n";

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Toast.makeText(context, data, Toast.LENGTH_SHORT).show();


                        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(context);

                        // Setting Dialog Title
                        alertDialog2.setTitle("email Chat");

                        // Setting Dialog Message
                        alertDialog2.setMessage("Are you sure you want to share chat backup to Email ?");

                        // Setting Icon to Dialog


                        // Setting Positive "Yes" Button
                        alertDialog2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                if (Build.VERSION.SDK_INT >= 23)
                                {
                                    if (checkPermission())
                                    {

                                        try {



                                            final File storagePath = new File(Environment.getExternalStorageDirectory(), "/Toado/Backup");
                                            final File myFile = new File(storagePath,"chatbackup"+".txt");

                                            if(!storagePath.exists()) {
                                                storagePath.mkdirs();
                                            }


                                            //  myFile.createNewFile();

                                            FileOutputStream fOut = new FileOutputStream(myFile);
                                            OutputStreamWriter myOutWriter =
                                                    new OutputStreamWriter(fOut);
                                            myOutWriter.append(data);
                                            myOutWriter.close();
                                            fOut.close();


                                            Uri zipUri = Uri.parse(Environment.getExternalStorageDirectory()+"/Toado/"+"chatbackup.txt");
                                            String[] emailArr = {"lovishraheja27@gmail.com"};
                                            Intent shareIntent2 = ShareCompat.IntentBuilder.from(context)
                                                    .setText("Share Chat Backup")
                                                    .setType("text/plain")
                                                    .setStream(zipUri)
                                                    .setStream(zipUri)
                                                    .setSubject("Chat Backup")
                                                    .setText("Sent with email app.")
                                                    .getIntent()
                                                    .setPackage("com.google.android.gm");
                                            shareIntent2.putExtra(Intent.EXTRA_STREAM,zipUri);
                                            context.startActivity(shareIntent2);


                                           // Toast.makeText(context, "Backup created in Toado folder in your SD card", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(context, e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } else {
                                    requestPermission(); // Code for permission
                                }
                            }
                        });


                        // Setting Negative "NO" Button
                        alertDialog2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to invoke NO event

                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog2.show();


                        break;


                    case "Chat Backup":

                        FirebaseDatabase.getInstance().getReference().child("message").child("null").getRef().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {

                                    data=data+snapshot.getValue().toString()+"\n"+"\n";

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Toast.makeText(context, data, Toast.LENGTH_SHORT).show();


                        AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(context);

                        // Setting Dialog Title
                        alertDialog1.setTitle("Backup");

                        // Setting Dialog Message
                        alertDialog1.setMessage("Are you sure you want to make backup ?");

                        // Setting Icon to Dialog


                        // Setting Positive "Yes" Button
                        alertDialog1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                if (Build.VERSION.SDK_INT >= 23)
                                {
                                    if (checkPermission())
                                    {

                                try {



                                    final File storagePath = new File(Environment.getExternalStorageDirectory(), "/Toado/Backup");
                                    final File myFile = new File(storagePath,"chatbackup"+".txt");

                                    if(!storagePath.exists()) {
                                        storagePath.mkdirs();
                                    }


                                      //  myFile.createNewFile();

                                    FileOutputStream fOut = new FileOutputStream(myFile);
                                    OutputStreamWriter myOutWriter =
                                            new OutputStreamWriter(fOut);
                                    myOutWriter.append(data);
                                    myOutWriter.close();
                                    fOut.close();

                                    Uri pdfUri = Uri.parse(Environment.getExternalStorageDirectory()+"/Toado/"+"chatbackup.txt");
                                    Intent shareIntent = ShareCompat.IntentBuilder.from(context)
                                            .setText("Share PDF doc")
                                            .setType("text/plain")
                                            .setStream(pdfUri )
                                            .getIntent()
                                            .setPackage("com.google.android.apps.docs");
                                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(shareIntent);
                                    Toast.makeText(context, "Backup craeted in Toado folder in your SD card", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(context, e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                                } else {
                                    requestPermission(); // Code for permission
                                }
                            }
                        });


                        // Setting Negative "NO" Button
                        alertDialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to invoke NO event

                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog1.show();


                        break;

                    case "Delete Chats":
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                        // Setting Dialog Title
                        alertDialog.setTitle("Confirm Delete...");

                        // Setting Dialog Message
                        alertDialog.setMessage("Are you sure you want delete this?");

                        // Setting Icon to Dialog


                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                FirebaseDatabase.getInstance().getReference().child("message").removeValue();
                                Toast.makeText(getApplicationContext(),"All Chats Deleted",Toast.LENGTH_LONG).show();
                            }
                        });

                        // Setting Negative "NO" Button
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to invoke NO event

                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                        break;

                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(context, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, }, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
