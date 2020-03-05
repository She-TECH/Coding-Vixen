package com.example.healthin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Objects;

public class login extends AppCompatActivity {
    EditText emailid,userid;
    Button btnlogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    signup signupobj = new signup();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //emailid=findViewById(R.id.emailid);
        userid=findViewById(R.id.userid);
        btnlogin=findViewById(R.id.login);
        final HashMap map= new HashMap();
int i=0;

        map.put("1","pooja812@gmail.com");
        map.put("2","anjali812@gmail.com");
        map.put("3","gurpreet812@gmail.com");
        map.put("4","parul@gmail.com");
        //map.put(i++,"rishika@gmail.com");





        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userid.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter userID", Toast.LENGTH_SHORT).show();
                } else if(map.containsKey(userid.getText().toString())) {
                    Toast.makeText(getApplicationContext(),userid.getText().toString(), Toast.LENGTH_SHORT).show();

                    Toast.makeText(getApplicationContext(), "Valid userID", Toast.LENGTH_SHORT).show();
                        Intent launchactivity = new Intent(login.this, MainActivity.class);
                        startActivity(launchactivity);
                    }
                else {
                        Toast.makeText(getApplicationContext(),userid.getText().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Invalid userId", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

}