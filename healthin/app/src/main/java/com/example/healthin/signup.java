package com.example.healthin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class signup extends AppCompatActivity {

    EditText edttxtemailid;
    EditText edttxtpassword;
    TextView txtsignup;
    ImageButton imgbtnsignup;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    HashMap map = new HashMap();



    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        txtsignup =findViewById(R.id.txtsignup);
        edttxtemailid =findViewById(R.id.edttxtemailid);
        edttxtpassword =findViewById(R.id.edttxtpassword);
                imgbtnsignup =findViewById(R.id.imgbtnsignup);




        imgbtnsignup.setOnClickListener(new View.OnClickListener() {
             Set<String> emailIdSet = new HashSet<>();
            String userId;

            @Override
            public void onClick(View view) {
                int i =0;
                map.put(i++,"pooja812@gmail.com");
                map.put(i++,"anjali812@gmail.com");
                map.put(i++,"gurpreet812@gmail.com");
                map.put(i++,"parul@gmail.com");
                map.put(i++,"rishika@gmail.com");



                String email=  edttxtemailid.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "enter email address", Toast.LENGTH_SHORT).show();
                } else {
                    if (email.trim().matches(emailPattern)) {

                        Toast.makeText(getApplicationContext(), "valid email address", Toast.LENGTH_SHORT).show();
                        if(emailIdSet.add(email))
                        {

                            userId = new String(i+1 + "-app");
                            map.put(userId,email);
                        }

                        Intent launchactivity = new Intent(signup.this, login.class);
                        startActivity(launchactivity);
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
