package com.awesome.zhen.mylanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField, mEmailField, mPasswordField;
    private Button mRegisterBtn;
    private CircleImageView mprofileBtn;

    private static final int GALLERY_INTENT = 2;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Uri mResultUri= null;

    private static final String TAG = "RegisterActivity";
    private ProgressDialog mProgress;

    private boolean mImgClick = false;

    // variable to track event time
    private long mLastClickTime = 0;

    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        mDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");

        mNameField = (EditText)findViewById(R.id.nameField1);
        mEmailField = (EditText)findViewById(R.id.emailField1);
        mPasswordField = (EditText)findViewById(R.id.passwordField1);

        mprofileBtn =(CircleImageView) findViewById(R.id.profile_image);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startRegister();
            }
        });

        mprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT );
            }
        });
    }

    private void startRegister(){
        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();



        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            setUpUser();


            //Toast.makeText(RegisterActivity.this, "Well Done", Toast.LENGTH_LONG).show();
            mProgress.setMessage("Signing Up");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(uid);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("password").setValue(password);
                        current_user_db.child("id").setValue(uid);

                        if (mImgClick == false){
                            current_user_db.child("image").setValue("default");
                        }
                        else{
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://my-gg-app.appspot.com");
                            StorageReference filepath = storageRef.child("Photos").child(mResultUri.getLastPathSegment());
                            filepath.putFile(mResultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = mDatabase.child(uid);
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    current_user_db.child("image").setValue(downloadUrl.toString());
                                }
                            });
                        }

                        // Go to MainActivity
                        mProgress.dismiss();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.putExtra("user_id", uid);
                        //intent.putExtra("profile_picture",image);
                        startActivity(intent);
                        finish();
                    }

                        /*String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("password").setValue(password);
                        current_user_db.child("image").setValue("default");

                        Intent loginIntent = new Intent(RegisterActivity.this, Login.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                        mProgress.dismiss();

                        finish();*/
                    else{
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "Email already existed, please create another one", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }
        else{
            Toast.makeText(RegisterActivity.this, "Fields are Empty", Toast.LENGTH_LONG).show();
        }
    }

    protected void setUpUser() {
        user = new User();
        user.setName(mNameField.getText().toString());
        user.setEmail(mEmailField.getText().toString());
        user.setPassword(mPasswordField.getText().toString());
    }

    private void signOut() {
        mAuth.signOut();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            //mProgress.setMessage("Uploading Image..");
            //mProgress.show();
            Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImgClick = true;
                mResultUri = result.getUri();
                mprofileBtn.setImageURI(mResultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();

            }
            else{
                mImgClick= false;
            }
        }
    }
}
            //StorageReference filepath = storageRef.child("Photos").child(uri.getLastPathSegment());

            //set the image into imageview
            //mImageView.setImageURI(uri);
            //Upload
            /*filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

            */


