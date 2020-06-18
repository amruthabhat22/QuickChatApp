package com.example.quickchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mregbtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mregbtn = (Button)findViewById(R.id.start_reg_btn);
        mLoginBtn=(Button)findViewById(R.id.start_login_btn);

        mregbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  regIntent=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(logIntent);
            }
        });
    }
}

