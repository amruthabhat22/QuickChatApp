package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {
    private CircleImageView mDisplayImage;
    private TextView mdisplayname;
    private TextView mstatus;
    private FirebaseUser mcurrentUser;
    private DatabaseReference mDatabase;
    private Button mChangeStatusBtn,mImageBtn;
    private StorageReference mProfileImage;
    private static final int GALLARY_PIC = 1;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDisplayImage = (CircleImageView)findViewById(R.id.settings_display_pic);
        mdisplayname=(TextView)findViewById(R.id.settings_display_name);
        mstatus=(TextView)findViewById(R.id.settings_status);
        mProfileImage = FirebaseStorage.getInstance().getReference();


        mChangeStatusBtn = (Button) findViewById(R.id.settings_change_status);
        mImageBtn = (Button) findViewById(R.id.settings_change_img);
        mcurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_userId= mcurrentUser.getUid();


        mDatabase= FirebaseDatabase.getInstance().getReference().child("user").child(current_userId);

      mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String image_tumbnail = dataSnapshot.child("thumb_image").getValue().toString();


              Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.dpp).error(R.drawable.dpp).into(mDisplayImage, new Callback() {
                  @Override
                  public void onSuccess() {
//Toast.makeText(SettingsActivity.this,"SUCCESS",Toast.LENGTH_LONG).show();
                  }

                  @Override
                  public void onError(Exception e) {
                      Picasso.get().load(image).placeholder(R.drawable.dpp).error(R.drawable.dpp).into(mDisplayImage);

                  }
              });

               mdisplayname.setText(name);
               mstatus.setText(status);
               if (!image.equals("default")){
                   Picasso.get().load(image).placeholder(R.drawable.dpp).into(mDisplayImage);
               }


               //Picasso.get().load(image).resize(406,448).centerCrop().into(mDisplayImage);
               //Picasso.get().load(image).fit().centerCrop().into(mDisplayImage);
                //Picasso.get().setLoggingEnabled(true);
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

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent,"Select Image"),GALLARY_PIC);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLARY_PIC && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Images..");
                mProgressDialog.setMessage("Please wait a while");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                Uri resultUri = result.getUri();
                String userId=mcurrentUser.getUid();

                final File thumb_filepath = new File(resultUri.getPath());
                final byte[] thumb_byte;


               /* Bitmap bitmapImage = null;
                try {
                    bitmapImage = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if ( bitmapImage != null) {
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                thumb_byte = baos.toByteArray();*/


                StorageReference filepath = mProfileImage.child("profile_images").child(userId + ".jpg");
                //path to store thumb images
                final StorageReference thumb_filePath = mProfileImage.child("profile_images")
                        .child("thumb_image").child(userId + ".jpg");




                StorageReference filePath = mProfileImage.child("profile_images").child(userId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(SettingsActivity.this,"Working", Toast.LENGTH_LONG).show();
                            String download_url= task.getResult().getStorage().getDownloadUrl().toString();

                            mDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                    }
                                }
                            });


                        }else{
                            Toast.makeText(SettingsActivity.this,"Error", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
            }

            }

    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0 ; i < randomLength ; i++){
            tempChar = (char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}

