package com.awesome.zhen.mylanceapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by zhen on 4/19/2017.
 */

public class EditPost extends AppCompatActivity {
    private DatabaseReference mBlog;
    private DatabaseReference mComment;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUser;
    private DatabaseReference mCommentLike;
    private DatabaseReference mUserSubscribe;
    private DatabaseReference mUsersInfo;
    private Query mQueryCurrentUser;

    private ImageView mBlogSingleImage;
    private CircleImageView mBlogSingleUserImage;
    private EditText mBlogSingleTitle;
    private EditText mBlogSingleDesc;
    private TextView mBlogSingleUsername;
    private TextView mBlogSingleTime;
    private EditText mtxtTel;
    private TextView mtxtLocation;
    private ImageButton mChangebtn;
    private ImageButton mCancelbtn;

    private ScrollView scrollView;
    private RelativeLayout mCancelRlay;
    private RelativeLayout mChangeRlay;

    private Toolbar mToolbar;


    private String post_key, location_key, category_key, post_uid, allblogkey, commentusername, commentuserimage, post_username, post_image;
    private String post_location, post_userimage;
    private long time;
    private int count = 0;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private static final String TAG = "SinglePost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpost);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);

        post_key = getIntent().getStringExtra("blog_id");
        location_key = getIntent().getStringExtra("location_id");
        category_key = getIntent().getStringExtra("category_id");

        mAuth = FirebaseAuth.getInstance();

        mCommentLike = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mBlog = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mBlog.keepSynced(true);

        mDatabaseLike = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mComment = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Comment");
        mComment.keepSynced(true);

        mUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUser.keepSynced(true);

        mUserSubscribe = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UserSubscribe");
        mUserSubscribe.keepSynced(true);

        mUsersInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
        mUsersInfo.keepSynced(true);

        mBlogSingleTitle = (EditText) findViewById(R.id.txtTitle);
        mBlogSingleDesc = (EditText) findViewById(R.id.txtDesc);
        mBlogSingleUsername = (TextView) findViewById(R.id.txtUsername);
        mBlogSingleTime = (TextView) findViewById(R.id.txtTime);
        mtxtTel = (EditText) findViewById(R.id.txtTel);
        mtxtLocation = (TextView) findViewById(R.id.txtLocation);

        mBlogSingleUserImage = (CircleImageView) findViewById(R.id.userimage);
        mBlogSingleImage = (ImageView) findViewById(R.id.postImage);

        mChangeRlay = (RelativeLayout) findViewById(R.id.changeRlay);
        mCancelRlay = (RelativeLayout) findViewById(R.id.cancelRlay);

        mChangebtn = (ImageButton) findViewById(R.id.changeBtn);
        mChangebtn.setEnabled(false);
        mChangebtn.setAlpha(0.5f);
        mCancelbtn = (ImageButton) findViewById(R.id.cancelBtn);

        mBlog.child(category_key).child(location_key).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                post_image = (String) dataSnapshot.child("image").getValue();
                post_userimage = (String) dataSnapshot.child("userimage").getValue();
                post_username = (String) dataSnapshot.child("username").getValue();
                String post_tel = (String) dataSnapshot.child("telephone").getValue();
                post_location = (String) dataSnapshot.child("location").getValue();
                post_uid = (String) dataSnapshot.child("uid").getValue();
                allblogkey = (String) dataSnapshot.child("key").getValue();

                if(dataSnapshot.hasChild("time")){
                    time = (Long) dataSnapshot.child("time").getValue();
                    Long tsLong = System.currentTimeMillis();
                    CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, 0);
                    mBlogSingleTime.setText(result);}

                mBlogSingleTitle.setText(post_title);
                mBlogSingleDesc.setText(post_desc);
                mtxtTel.setText(post_tel);
                mtxtLocation.setText(post_location);
                mBlogSingleUsername.setText(post_username);

                if(post_userimage != null && post_userimage.equals("default")){
                    mBlogSingleUserImage.setImageResource(R.mipmap.profilepic);
                }
                else {
                    Picasso.with(EditPost.this).load(post_userimage).fit().centerCrop().into(mBlogSingleUserImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBlogSingleTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                mChangebtn.setAlpha(1f);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                mChangebtn.setEnabled(true);
               // mChangebtn.setAlpha(1f);
            }
        });

        mBlogSingleDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                mChangebtn.setEnabled(true);
                //mChangebtn.setAlpha(1f);
            }
        });


        mtxtTel.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                mChangebtn.setEnabled(true);
                //mChangebtn.setAlpha(1f);
            }
        });

        mChangeRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mChangeRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mChangeRlay.setBackgroundColor(Color.parseColor("#FF45CBF7"));
                }
                return false;
            }

        });

        mCancelRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mCancelRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mCancelRlay.setBackgroundColor(Color.parseColor("#890e08"));
                }
                return false;
            }

        });

        mChangeRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changepost();
            }
        });

        mChangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changepost();
            }
        });

        mCancelRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        mCancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
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

    private void changepost() {
        final String title_val = mBlogSingleTitle.getText().toString().trim();
        final String desc_val = mBlogSingleDesc.getText().toString().trim();
        final String tel_val = mtxtTel.getText().toString().trim();
        final String location_val = mtxtLocation.getText().toString().trim();

        if(location_val.equals(location_key)){
            mBlog.child(category_key).child(location_key).child(post_key).child("title").setValue(title_val);
            mBlog.child(allblogkey).child("title").setValue(title_val);
            mBlog.child(category_key).child(location_key).child(post_key).child("desc").setValue(desc_val);
            mBlog.child(allblogkey).child("desc").setValue(desc_val);
            mBlog.child(category_key).child(location_key).child(post_key).child("telephone").setValue(tel_val);
            mBlog.child(allblogkey).child("telephone").setValue(tel_val);
        }

        else{
            mBlog.child(category_key).child(location_key).child(post_key).removeValue();

            mBlog.child(allblogkey).child("title").setValue(title_val);
            mBlog.child(allblogkey).child("desc").setValue(desc_val);
            mBlog.child(allblogkey).child("telephone").setValue(tel_val);
            mBlog.child(allblogkey).child("location").setValue(location_val);

            mBlog.child(category_key).child(location_val).child(post_key).child("title").setValue(title_val);
            mBlog.child(category_key).child(location_val).child(post_key).child("desc").setValue(desc_val);
            mBlog.child(category_key).child(location_val).child(post_key).child("location").setValue(location_val);
            mBlog.child(category_key).child(location_val).child(post_key).child("telephone").setValue(tel_val);
            mBlog.child(category_key).child(location_val).child(post_key).child("image").setValue(post_image);
            mBlog.child(category_key).child(location_val).child(post_key).child("userimage").setValue(post_userimage);
            mBlog.child(category_key).child(location_val).child(post_key).child("username").setValue(post_username);
            mBlog.child(category_key).child(location_val).child(post_key).child("uid").setValue(post_uid);
            mBlog.child(category_key).child(location_val).child(post_key).child("time").setValue(time);
            mBlog.child(category_key).child(location_val).child(post_key).child("key").setValue(allblogkey);

        }

        finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
