package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mlogemail,mlogpassword;
    private Button mlogin;
    private ProgressDialog mlogdialog;
    private Toolbar mlogbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("user");
        mAuth = FirebaseAuth.getInstance();
        mlogemail=(TextInputLayout)findViewById(R.id.login_email);
        mlogpassword=(TextInputLayout)findViewById(R.id.login_passwd);
        mlogin=(Button)findViewById(R.id.login_btn);
        mlogdialog=new ProgressDialog(LoginActivity.this);
        mlogbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mlogbar);
       getSupportActionBar().setTitle("Login");
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

mlogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String mail=mlogemail.getEditText().getText().toString();
        String passwd=mlogpassword.getEditText().getText().toString();

        if(!mail.isEmpty() && !passwd.isEmpty()){
            mlogdialog.setTitle("Logging In");
            mlogdialog.setMessage("Please wait while we check your credentials");
            mlogdialog.setCanceledOnTouchOutside(false);
            mlogdialog.show();
            loginuser(mail,passwd);
        }else{
            Toast.makeText(LoginActivity.this,"Please fill out the missing fields",Toast.LENGTH_LONG).show();
        }
    }
});


    }
    private void loginuser(String mail,String passwd){
      mAuth.signInWithEmailAndPassword(mail,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()){
                  mlogdialog.dismiss();

                  String currentUID=mAuth.getCurrentUser().getUid();
                  String deviceToken= FirebaseInstanceId.getInstance().getToken();

                  mUserDatabase.child(currentUID).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {

                          Intent mainintent=new Intent(LoginActivity.this,MainActivity.class);
                          mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(mainintent);
                          finish();

                      }
                  });



              }else {

                  /*Log.w("loginmsg","sigInWithEmail:failure",task.getException());
                  Toast.makeText(LoginActivity.this,"Authentication failed",Toast.LENGTH_LONG).show();
                  String errorMsg=task.getException().getMessage();
                  Toast.makeText(LoginActivity.this,errorMsg,Toast.LENGTH_LONG).show();

                  mlogdialog.hide();*/

                  Toast.makeText(LoginActivity.this,"Cannot Sign in.Please check the form and try again",Toast.LENGTH_LONG).show();
              }
          }
      });
          }


}