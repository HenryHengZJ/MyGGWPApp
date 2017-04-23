package com.awesome.zhen.mylanceapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {


    private Button mbtnSubmit;
    private EditText mPostTitle, mPostDescrip, mPostTel;
    private ImageButton mImagebtn1;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    private ProgressDialog mProgress;
    private Button mLocationbtn;
    private Button mCategorybtn;

    private DatabaseReference mBlog;
    private DatabaseReference mUser;
    private DatabaseReference mUserSubsribe;

    // variable to track event time
    private long mLastClickTime = 0;

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrenUser;

    private boolean mImgClick = false;

    private Uri mImageUri= null;

    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mbtnSubmit = (Button)findViewById(R.id.btnSubmit);
        mImagebtn1 = (ImageButton) findViewById(R.id.imageButton1);
        mPostTitle = (EditText) findViewById(R.id.postTitle);
        mPostDescrip = (EditText) findViewById(R.id.postDescrip);
        mPostTel = (EditText) findViewById(R.id.postTel);
        mLocationbtn = (Button) findViewById(R.id.Locationbtn);
        mCategorybtn = (Button) findViewById(R.id.categorybtn);

        mLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showRadioButtonDialogLocation();
            }
        });

        mCategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showRadioButtonDialogCategory();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mCurrenUser = mAuth.getCurrentUser();

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mUser =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users").child(mCurrenUser.getUid());
        mUserSubsribe =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UserSubscribe").child(mCurrenUser.getUid());

        mImagebtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/* ");
                startActivityForResult(galleryIntent,GALLERY_INTENT );
            }
        });


        mbtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
    }

    private void startPosting(){

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDescrip.getText().toString().trim();
        final String tel_val = mPostTel.getText().toString().trim();
        final String location_val = mLocationbtn.getText().toString().trim();
        final String category_val = mCategorybtn.getText().toString().trim();

        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(tel_val)&&!TextUtils.isEmpty(location_val)&&!TextUtils.isEmpty(category_val)&&mImageUri!=null) {

            mProgress.setMessage("Uploading..");
            mProgress.show();

            if (mImgClick == true) {

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://my-gg-app.appspot.com");
                StorageReference filepath = storageRef.child("Photos").child(mImageUri.getLastPathSegment());

                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        final DatabaseReference allBlog = mBlog.push();

                        final DatabaseReference newCategory = mBlog.child(category_val);

                        final DatabaseReference newLocation = newCategory.child(location_val);
                        final DatabaseReference newPost = newLocation.push();
                        final String keyval = newPost.getKey();
                        final String allval = allBlog.getKey();

                        mUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                mUserSubsribe.child(keyval).setValue(true);
                                mUserSubsribe.child("uid").setValue(mCurrenUser.getUid());

                                Map<String,Object> checkoutData=new HashMap<>();
                                checkoutData.put("time",ServerValue.TIMESTAMP);
                                newPost.setValue(checkoutData);
                                allBlog.setValue(checkoutData);

                                allBlog.child("category").setValue(category_val);

                                newPost.child("key").setValue(allval);
                                allBlog.child("key").setValue(keyval);

                                newPost.child("title").setValue(title_val);
                                allBlog.child("title").setValue(title_val);

                                newPost.child("desc").setValue(desc_val);
                                allBlog.child("desc").setValue(desc_val);

                                newPost.child("telephone").setValue(tel_val);
                                allBlog.child("telephone").setValue(tel_val);

                                newPost.child("location").setValue(location_val);
                                allBlog.child("location").setValue(location_val);

                                newPost.child("uid").setValue(mCurrenUser.getUid());
                                allBlog.child("uid").setValue(mCurrenUser.getUid());

                                newPost.child("image").setValue(downloadUrl.toString());
                                allBlog.child("image").setValue(downloadUrl.toString());

                                newPost.child("userimage").setValue(dataSnapshot.child("image").getValue());
                                allBlog.child("userimage").setValue(dataSnapshot.child("image").getValue());

                                allBlog.child("username").setValue(dataSnapshot.child("name").getValue());
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            finish();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(PostActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();

                    }
                });

                filepath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                });
            }
        }

        else if (!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&!TextUtils.isEmpty(tel_val)&&!TextUtils.isEmpty(location_val)&&!TextUtils.isEmpty(category_val)&&mImageUri==null){

            mProgress.setMessage("Uploading..");
            mProgress.show();

            if(mImgClick == false) {

                final DatabaseReference allBlog = mBlog.push();

                final DatabaseReference newCategory = mBlog.child(category_val);

                final DatabaseReference newLocation = newCategory.child(location_val);
                final DatabaseReference newPost = newLocation.push();
                final String keyval = newPost.getKey();
                final String allval = allBlog.getKey();

                mUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mUserSubsribe.child(keyval).setValue(true);
                        mUserSubsribe.child("uid").setValue(mCurrenUser.getUid());

                        Map<String,Object> checkoutData=new HashMap<>();
                        checkoutData.put("time",ServerValue.TIMESTAMP);
                        newPost.setValue(checkoutData);
                        allBlog.setValue(checkoutData);

                        allBlog.child("category").setValue(category_val);

                        newPost.child("key").setValue(allval);
                        allBlog.child("key").setValue(keyval);

                        newPost.child("title").setValue(title_val);
                        allBlog.child("title").setValue(title_val);

                        newPost.child("desc").setValue(desc_val);
                        allBlog.child("desc").setValue(desc_val);

                        newPost.child("telephone").setValue(tel_val);
                        allBlog.child("telephone").setValue(tel_val);

                        newPost.child("location").setValue(location_val);
                        allBlog.child("location").setValue(location_val);

                        newPost.child("uid").setValue(mCurrenUser.getUid());
                        allBlog.child("uid").setValue(mCurrenUser.getUid());

                        newPost.child("image").setValue("default");
                        allBlog.child("image").setValue("default");

                        newPost.child("userimage").setValue(dataSnapshot.child("image").getValue());
                        allBlog.child("userimage").setValue(dataSnapshot.child("image").getValue());

                        allBlog.child("username").setValue(dataSnapshot.child("name").getValue());
                        newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    finish();
                                }
                            }
                        });

                    }
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }

            Toast.makeText(PostActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
            mProgress.dismiss();
        }

        else {
            Toast.makeText(PostActivity.this, "Please fill in all the fields", Toast.LENGTH_LONG).show();
        }

}



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRadioButtonDialogLocation() {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        List<String> stringList=new ArrayList<>();  // here is list
        stringList.add("DUBLIN");
        stringList.add("CORK");
        stringList.add("LIMERICK");
        stringList.add("GALWAY");
        stringList.add("WATERFORD");

        stringList.add("ANTRIM");
        stringList.add("AMAGH");
        stringList.add("CARLOW");
        stringList.add("CAVAN");
        stringList.add("CLARE");
        stringList.add("DONEGAL");
        stringList.add("FERMANAGH");
        stringList.add("KERRY");
        stringList.add("KILDARE");
        stringList.add("KILKENNY");
        stringList.add("LAOIS");
        stringList.add("LEITRIM");
        stringList.add("LONDONDERRY");
        stringList.add("LONGFORD");
        stringList.add("MAYO");
        stringList.add("MEATH");
        stringList.add("MONAGHAN");
        stringList.add("OFFALY");
        stringList.add("ROSCOMMON");
        stringList.add("SLIGO");
        stringList.add("TIPPERARY");
        stringList.add("WESTMEATH");
        stringList.add("WEXFORD");

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        RadioButton rdo1 = (RadioButton) dialog.findViewById(R.id.rdo1);
        rdo1.setText(stringList.get(0));
        RadioButton rdo2 = (RadioButton) dialog.findViewById(R.id.rdo2);
        rdo2.setText(stringList.get(1));
        RadioButton rdo3 = (RadioButton) dialog.findViewById(R.id.rdo3);
        rdo3.setText(stringList.get(2));
        RadioButton rdo4 = (RadioButton) dialog.findViewById(R.id.rdo4);
        rdo4.setText(stringList.get(3));

        RadioButton rdo5 = (RadioButton) dialog.findViewById(R.id.rdo5);
        rdo5.setText(stringList.get(4));
        RadioButton rdo6 = (RadioButton) dialog.findViewById(R.id.rdo6);
        rdo6.setText(stringList.get(5));
        RadioButton rdo7 = (RadioButton) dialog.findViewById(R.id.rdo7);
        rdo7.setText(stringList.get(6));
        RadioButton rdo8 = (RadioButton) dialog.findViewById(R.id.rdo8);
        rdo8.setText(stringList.get(7));

        RadioButton rdo9 = (RadioButton) dialog.findViewById(R.id.rdo9);
        rdo9.setText(stringList.get(8));
        RadioButton rdo10 = (RadioButton) dialog.findViewById(R.id.rdo10);
        rdo10.setText(stringList.get(9));
        RadioButton rdo11 = (RadioButton) dialog.findViewById(R.id.rdo11);
        rdo11.setText(stringList.get(10));
        RadioButton rdo12 = (RadioButton) dialog.findViewById(R.id.rdo12);
        rdo12.setText(stringList.get(11));

        RadioButton rdo13 = (RadioButton) dialog.findViewById(R.id.rdo13);
        rdo13.setText(stringList.get(12));
        RadioButton rdo14 = (RadioButton) dialog.findViewById(R.id.rdo14);
        rdo14.setText(stringList.get(13));
        RadioButton rdo15 = (RadioButton) dialog.findViewById(R.id.rdo15);
        rdo15.setText(stringList.get(14));
        RadioButton rdo16 = (RadioButton) dialog.findViewById(R.id.rdo16);
        rdo16.setText(stringList.get(15));

        RadioButton rdo17 = (RadioButton) dialog.findViewById(R.id.rdo17);
        rdo17.setText(stringList.get(16));
        RadioButton rdo18 = (RadioButton) dialog.findViewById(R.id.rdo18);
        rdo18.setText(stringList.get(17));
        RadioButton rdo19 = (RadioButton) dialog.findViewById(R.id.rdo19);
        rdo19.setText(stringList.get(18));
        RadioButton rdo20 = (RadioButton) dialog.findViewById(R.id.rdo20);
        rdo20.setText(stringList.get(19));

        RadioButton rdo21 = (RadioButton) dialog.findViewById(R.id.rdo21);
        rdo21.setText(stringList.get(20));
        RadioButton rdo22 = (RadioButton) dialog.findViewById(R.id.rdo22);
        rdo22.setText(stringList.get(21));
        RadioButton rdo23 = (RadioButton) dialog.findViewById(R.id.rdo23);
        rdo23.setText(stringList.get(22));
        RadioButton rdo24 = (RadioButton) dialog.findViewById(R.id.rdo24);
        rdo24.setText(stringList.get(23));

        RadioButton rdo25 = (RadioButton) dialog.findViewById(R.id.rdo25);
        rdo25.setText(stringList.get(24));
        RadioButton rdo26 = (RadioButton) dialog.findViewById(R.id.rdo26);
        rdo26.setText(stringList.get(25));
        RadioButton rdo27 = (RadioButton) dialog.findViewById(R.id.rdo27);
        rdo27.setText(stringList.get(26));
        RadioButton rdo28 = (RadioButton) dialog.findViewById(R.id.rdo28);
        rdo28.setText(stringList.get(27));

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Toast.makeText(getApplicationContext(), btn.getText().toString(), Toast.LENGTH_SHORT).show();
                        mLocationbtn.setText(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void showRadioButtonDialogCategory() {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.category_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        List<String> stringList = new ArrayList<>();  // here is list
        stringList.add("招聘 ");
        stringList.add("租房 ");
        stringList.add("二手 ");
        stringList.add("商务 ");

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        RadioButton rdo1 = (RadioButton) dialog.findViewById(R.id.rdo1);
        rdo1.setText(stringList.get(0));
        RadioButton rdo2 = (RadioButton) dialog.findViewById(R.id.rdo2);
        rdo2.setText(stringList.get(1));
        RadioButton rdo3 = (RadioButton) dialog.findViewById(R.id.rdo3);
        rdo3.setText(stringList.get(2));
        RadioButton rdo4 = (RadioButton) dialog.findViewById(R.id.rdo4);
        rdo4.setText(stringList.get(3));

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        btn.setChecked(true);
                        Toast.makeText(getApplicationContext(), btn.getText().toString(), Toast.LENGTH_SHORT).show();
                        mCategorybtn.setText(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            Uri imageuri = data.getData();

            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMaxCropResultSize(2500,3000)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);

        }

            //set the image into imageview
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImgClick = true;
                mImageUri = result.getUri();
                Picasso.with(PostActivity.this).load(mImageUri).fit().centerCrop().into(mImagebtn1);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                mImgClick = false;
                Exception error = result.getError();
            }
            else{
                mImgClick = false;
                mBlog.child(mAuth.getCurrentUser().getUid()).child("image").setValue("default");
            }
        }
    }
}