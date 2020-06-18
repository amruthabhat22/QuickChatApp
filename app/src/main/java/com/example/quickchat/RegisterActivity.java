package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar
;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mFullName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button maccbtn;
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ProgressDialog mregprogress;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mFullName = (TextInputLayout) findViewById(R.id.reg_fullname);
        mEmail = (TextInputLayout) findViewById(R.id.login_email);
        mPassword = (TextInputLayout) findViewById(R.id.login_passwd);
        maccbtn = (Button) findViewById(R.id.login_btn);
        mtoolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ProgressDialog
        mregprogress = new ProgressDialog(this);
        maccbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = mFullName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String passwd = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(fullname) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(passwd)) {
                    mregprogress.setTitle("Registering User");
                    mregprogress.setMessage("Please wait while we Create Your Account");
                    mregprogress.setCanceledOnTouchOutside(false);
                    mregprogress.show();
                    reg_user(fullname, email, passwd);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill out the Empty Fields", Toast.LENGTH_LONG).show();
                }
            }


        });

    }

    private void reg_user(final String fullname, String email, String passwd) {
        mAuth.createUserWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mdatabase = FirebaseDatabase.getInstance().getReference().child("user").child(uid);


                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name", fullname);
                    usermap.put("status", "hey there!i'm using quickchat app");
                    usermap.put("image", "default");
                    usermap.put("thumb_image", "default");

                    mdatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mregprogress.dismiss();
                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                    //once the task is finished we shd dismiss the progress dialog

                } else {
                    mregprogress.hide();
                    Toast.makeText(RegisterActivity.this, "Cannot create account.Please check the form and try again", Toast.LENGTH_LONG).show();
                }
            }

        });

    }
}