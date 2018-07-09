package com.app.toado.activity.Help;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.toado.R;

public class ContactUs extends AppCompatActivity {

    ImageView back;
    EditText problem;
    ImageView send;

    Button faq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        back=(ImageView)findViewById(R.id.back);
        faq=(Button)findViewById(R.id.faq);
        send=(ImageView)findViewById(R.id.send);
        problem=(EditText)findViewById(R.id.problem);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                startActivity(browserIntent);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(problem.getText().toString().length()>20){
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "sumitmca11@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact Us");
                emailIntent.putExtra(Intent.EXTRA_TEXT,problem.getText().toString() );
                startActivity(Intent.createChooser(emailIntent, "Send your query using"));
                }
                else{
                    Toast.makeText(ContactUs.this, "Please give some more details about your problem", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
