package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsActivity extends AppCompatActivity {
    private CircleImageView mDisplayImage;
    private TextView mdisplayname;
    private TextView mstatus;
    private FirebaseUser mcurrentUser;
    private DatabaseReference mDatabase;
    private Button mChangeStatusBtn,mImageBtn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDisplayImage = (CircleImageView)findViewById(R.id.settings_display_pic);
        mdisplayname=(TextView)findViewById(R.id.settings_display_name);
        mstatus=(TextView)findViewById(R.id.settings_status);

        mChangeStatusBtn = (Button) findViewById(R.id.settings_change_status);
        mImageBtn = (Button) findViewById(R.id.settings_change_img);
        mcurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_userId=mcurrentUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("user").child(current_userId);

      mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String image_tumbnail = dataSnapshot.child("thumb_image").getValue().toString();



               mdisplayname.setText(name);
               mstatus.setText(status);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mChangeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status = mstatus.getText().toString();
                Intent intent = new Intent(SettingsActivity.this,StatusActivity.class);
                intent.putExtra("CurrentStatus",status);
                startActivity(intent);

            }
        });



    }
}