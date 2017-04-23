package com.awesome.zhen.mylanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class PhotoUpload extends AppCompatActivity {


    private Button mSelectImage,mbtnCamera;
    private ImageView mImageView;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_upload);

        mImageView = (ImageView) findViewById(R.id.imageView1);

        mProgress = new ProgressDialog(this);
        /*
        mbtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_REQUEST_CODE );
            }
        });

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT );
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://my-gg-app.appspot.com");
        //name of the image file (add time to have different files to avoid rewrite on the same file)
        StorageReference imagesRef = storageRef.child("filename" + new Date().getTime());

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            mProgress.setMessage("Uploading Image..");
            mProgress.show();
            Uri uri = data.getData();
            StorageReference filepath = storageRef.child("Photos").child(uri.getLastPathSegment());

            //set the image into imageview
            //mImageView.setImageURI(uri);
            //Upload
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Picasso.with(PhotoUpload.this).load(downloadUrl).fit().centerCrop().into(mImageView);
                    Toast.makeText(PhotoUpload.this,"Upload Done",Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
            });

            filepath.putFile(uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Upload failed", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            });

        }

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            mProgress.setMessage("Uploading Image..");
            mProgress.show();

            //get the camera image
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBAOS = baos.toByteArray();

//set the image into imageview
            mImageView.setImageBitmap(bitmap);

//upload image

            UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Upload failed", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"Upload done", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            });
        }
    }
}