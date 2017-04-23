package com.awesome.zhen.mylanceapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class SinglePost extends AppCompatActivity {

    private DatabaseReference mBlog;
    private DatabaseReference mComment;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUser;
    private DatabaseReference mCommentLike;
    private DatabaseReference  mUserSubscribe;
    private DatabaseReference  mUsersInfo;
    private Query mQueryCurrentUser;

    private ImageView mBlogSingleImage;
    private CircleImageView mBlogSingleUserImage;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleDesc;
    private TextView mBlogSingleUsername;
    private TextView mBlogSingleTime;
    private TextView mtxtTel;
    private TextView mtxtLocation;
    private TextView mtxtlikeNum;
    private TextView mtxtCommentNum;
    private ImageButton mLikebtn;
    private ImageButton mActionbtn;
    private Button mpostComment;
    private EditText meditComment;
    private ScrollView scrollView;

    private Toolbar mToolbar;

    private RecyclerView mCommentList;
    private LinearLayoutManager mLayoutManager;

    private boolean mProcessLike = false;
    private boolean mProcessCommentLike = false;

    private String post_key, location_key, category_key, post_uid, allblogkey,commentusername, commentuserimage, post_username, post_image;
    private long numcomment = 0;
    private int count = 0;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private static final String TAG = "SinglePost";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        //mToolbar = (Toolbar)findViewById(R.id.nav_action);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgress = new ProgressDialog(this);

        post_key = getIntent().getStringExtra("blog_id");
        location_key = getIntent().getStringExtra("location_id");
        category_key = getIntent().getStringExtra("category_id");

        mAuth = FirebaseAuth.getInstance();

        mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");;
        mCommentLike.keepSynced(true);

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
        mBlog.keepSynced(true);

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mComment =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Comment");
        mComment.keepSynced(true);

        mUser =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUser.keepSynced(true);

        mUserSubscribe =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UserSubscribe");
        mUserSubscribe.keepSynced(true);

        mUsersInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
        mUsersInfo.keepSynced(true);

        mBlogSingleTitle = (TextView) findViewById(R.id.txtTitle);
        mBlogSingleDesc = (TextView) findViewById(R.id.txtDesc);
        mBlogSingleUsername = (TextView) findViewById(R.id.txtUsername);
        mBlogSingleTime = (TextView) findViewById(R.id.txtTime);
        mtxtTel = (TextView) findViewById(R.id.txtTel);
        mtxtLocation = (TextView) findViewById(R.id.txtLocation);
        mtxtlikeNum = (TextView) findViewById(R.id.txtlikeNum);
        mtxtCommentNum = (TextView) findViewById(R.id.txtcommentNum);
        meditComment = (EditText) findViewById(R.id.editComment);

        scrollView = (ScrollView) findViewById(R.id.scrollview);

        mBlogSingleImage = (ImageView) findViewById(R.id.postImage);
        mBlogSingleUserImage = (CircleImageView) findViewById(R.id.userimage);

        mActionbtn = (ImageButton) findViewById(R.id.actionBtn);
        mLikebtn = (ImageButton) findViewById(R.id.likeBtn);
        mpostComment = (Button) findViewById(R.id.postCommentbtn);
        mpostComment.setEnabled(false);

        final FirebaseUser mCurrenUser = mAuth.getCurrentUser();
        if (mCurrenUser != null) {

            mUser.child(mAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentuserimage = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });

            mUser.child(mAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentusername = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
        }

        meditComment.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0) {
                    mpostComment.setEnabled(true);
                    mpostComment.setAlpha(1);
                }
                else{
                    mpostComment.setEnabled(false);
                    mpostComment.setAlpha(0.5f);
                }
            }
        });


        mCommentList = (RecyclerView)findViewById(R.id.comment_list);
        //mCommentList.setHasFixedSize(true);
        mCommentList.setFocusable(false);
        mCommentList.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());

        mpostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startCommenting();
                scrollView.postDelayed(new Runnable() {
                    @Override

                    public void run() {

                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                },300);
            }
        });

        mActionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAction();
            }
        });



        mBlog.child(category_key).child(location_key).child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                post_image = (String) dataSnapshot.child("image").getValue();
                String post_userimage = (String) dataSnapshot.child("userimage").getValue();
                post_username = (String) dataSnapshot.child("username").getValue();
                String post_tel = (String) dataSnapshot.child("telephone").getValue();
                String post_location = (String) dataSnapshot.child("location").getValue();
                post_uid = (String) dataSnapshot.child("uid").getValue();
                allblogkey = (String) dataSnapshot.child("key").getValue();

                if(dataSnapshot.hasChild("time")){
                Long time = (Long) dataSnapshot.child("time").getValue();
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
                    Picasso.with(SinglePost.this).load(post_userimage).fit().centerCrop().into(mBlogSingleUserImage);
                }

                if(post_image != null && post_image.equals("default")){
                    mBlogSingleImage.setVisibility(View.GONE);
                }
                else {
                    mBlogSingleImage.setVisibility(View.VISIBLE);
                    Picasso.with(SinglePost.this).load(post_image).fit().centerCrop().into(mBlogSingleImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mBlogSingleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoViewAttacher pAttacher;
                final Dialog nagDialog = new Dialog(SinglePost.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                nagDialog.setContentView(R.layout.preview_image);
                RelativeLayout mRlay = (RelativeLayout)nagDialog.findViewById(R.id.Rlay);
                final TouchImageView ivPreview = (TouchImageView)nagDialog.findViewById(R.id.iv_preview_image);
                Picasso.with(getApplicationContext()).load(post_image).into(ivPreview);

                mRlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nagDialog.dismiss();
                    }
                });
                nagDialog.show();
            }
        });

        mBlogSingleUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent singleuserintent = new Intent(SinglePost.this, OtherUser.class);
                singleuserintent.putExtra("blog_id",post_key);
                singleuserintent.putExtra("userid",post_uid);
                startActivity(singleuserintent);
            }
        });


        mComment.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                mtxtCommentNum.setText(String.valueOf(count));
                Log.d(TAG, "Comment: " + count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                    mLikebtn.setImageResource(R.drawable.red_love);
                    mLikebtn.setImageAlpha(255);

                } else {

                    mLikebtn.setImageResource(R.drawable.grey_love);
                    mLikebtn.setImageAlpha(200);

                }
                long count = dataSnapshot.child(post_key).getChildrenCount();
                mtxtlikeNum.setText(String.valueOf(count));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProcessLike = true;
                mProcessCommentLike = true;

                final DatabaseReference newPost = mCommentLike.child(post_uid).push();

                mCommentLike.child(post_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessCommentLike) {
                            for (DataSnapshot getSnapshot : dataSnapshot.getChildren()) {
                                String blogkey = getSnapshot.child("blogpost").getValue().toString();
                                String message = getSnapshot.child("message").getValue().toString();
                                String userid = getSnapshot.child("uid").getValue().toString();

                                if(userid.equals(mAuth.getCurrentUser().getUid())){
                                    if(message.equals(" has liked your post. ")){
                                        if(blogkey.equals(post_key)){
                                            getSnapshot.getRef().removeValue();
                                            mProcessCommentLike = false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessLike) {

                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProcessLike = false;
                            }
                            else {
                                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                if (post_uid.equals(mAuth.getCurrentUser().getUid())) {
                                }
                                else{
                                    Map<String, Object> checkoutData = new HashMap<>();
                                    checkoutData.put("time", ServerValue.TIMESTAMP);
                                    newPost.setValue(checkoutData);
                                    newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                    newPost.child("username").setValue(commentusername);
                                    newPost.child("userimage").setValue(commentuserimage);
                                    newPost.child("location").setValue(location_key);
                                    newPost.child("category").setValue(category_key);
                                    newPost.child("pressed").setValue("false");
                                    newPost.child("message").setValue(" has liked your post. ");
                                    newPost.child("blogpost").setValue(post_key);
                                    mProcessCommentLike = false;
                                }
                                mProcessLike = false;
                            }
                        }

                    }
                    @Override
                    public void onCancelled (DatabaseError databaseError){

                    }
                });
            }
        });

        final FirebaseRecyclerAdapter<Comment,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comment,BlogViewHolder>(
                Comment.class,
                R.layout.comment_row,
                BlogViewHolder.class,
                mComment.child(post_key)

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Comment model, int position) {

                final String comment_key = getRef(position).getKey();
                final String comment_uid = model.getUid();

                viewHolder.setComment(model.getComment());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setuserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setTime(post_key,comment_key);

                viewHolder.mactionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showCommentaction(comment_key, comment_uid);
                    }
                });

                viewHolder.post_userimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mComment.child(post_key).child(comment_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String userid = dataSnapshot.child("uid").getValue().toString();
                                Intent singleuserintent = new Intent(SinglePost.this, OtherUser.class);
                                singleuserintent.putExtra("userid",userid);
                                startActivity(singleuserintent);
                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }
                        });
                    }
                });
            }
        };

        mCommentList.setAdapter(firebaseRecyclerAdapter);
        mCommentList.setLayoutManager(mLayoutManager);

    }

    private void showCommentaction(final String comment_key, final String comment_uid) {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.actionbtn_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button mReportbtn = (Button) dialog.findViewById(R.id.reportBtn);
        mReportbtn.setText("Report");
        Button mRemovebtn = (Button) dialog.findViewById(R.id.removeBtn);
        mRemovebtn.setText("Remove");

        dialog.show();

        if (mAuth.getCurrentUser().getUid().equals(post_uid)){
            mRemovebtn.setVisibility(View.VISIBLE);
        }

        if(mAuth.getCurrentUser().getUid().equals(comment_uid)){

            mRemovebtn.setVisibility(View.VISIBLE);

        }

        mRemovebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mComment.child(post_key).child(comment_key).removeValue();

                mCommentLike.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                            for (DataSnapshot deleteSnapshot2: deleteSnapshot.getChildren()) {
                                if (deleteSnapshot2.hasChild("commentkey")) {
                                    String commentkey = deleteSnapshot2.child("commentkey").getValue().toString();
                                    if (commentkey.equals(comment_key)) {
                                        deleteSnapshot2.getRef().removeValue();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                mComment.child(post_key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long xcount = dataSnapshot.getChildrenCount();
                        Log.d(TAG, "count: " + xcount);
                        for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                            String commentuserid = deleteSnapshot.child("uid").getValue().toString();
                            if(commentuserid.equals(comment_uid)){
                                xcount++;
                            }
                            else{
                                xcount--;
                            }
                        }
                        Log.d(TAG, "newcount: " + xcount);
                        if(xcount == 0){
                            if(comment_uid.equals(post_uid)){

                            }
                            else{
                                mUserSubscribe.child(comment_uid).child(post_key).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                dialog.dismiss();
            }
        });

        mReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SinglePost.this, "Thanks for reporting. We will take action against it soon.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
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

    private void showAction() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.actionbtn_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        Button mReportbtn = (Button) dialog.findViewById(R.id.reportBtn);
        mReportbtn.setText("Report");
        Button mRemovebtn = (Button) dialog.findViewById(R.id.removeBtn);
        mRemovebtn.setText("Remove");
        Button mEditbtn = (Button) dialog.findViewById(R.id.editBtn);
        mEditbtn.setText("Edit");

        dialog.show();

        if(mAuth.getCurrentUser().getUid().equals(post_uid)){

            mRemovebtn.setVisibility(View.VISIBLE);
            mEditbtn.setVisibility(View.VISIBLE);

        }

        mRemovebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBlog.child(category_key).child(location_key).child(post_key).removeValue();
                mBlog.child(allblogkey).removeValue();
                mDatabaseLike.child(post_key).removeValue();
                mComment.child(post_key).removeValue();

                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(post_image);
                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });

                mCommentLike.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot: dataSnapshot.getChildren()) {
                            for (DataSnapshot deleteSnapshot2: deleteSnapshot.getChildren()) {
                                String blogkey = deleteSnapshot2.child("blogpost").getValue().toString();
                                if(blogkey.equals(post_key)){
                                    deleteSnapshot2.getRef().removeValue();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }});

                mUserSubscribe.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot deleteSnapshot2: dataSnapshot.getChildren()) {
                            if(deleteSnapshot2.hasChild(post_key)){
                                deleteSnapshot2.getRef().child(post_key).removeValue();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }});

                dialog.dismiss();
                onBackPressed();
            }
        });

        mEditbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent editintent = new Intent(SinglePost.this, EditPost.class);
                editintent.putExtra("blog_id",post_key);
                editintent.putExtra("category_id",category_key);
                editintent.putExtra("location_id",location_key);
                startActivity(editintent);

                dialog.dismiss();

            }
        });

        mReportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SinglePost.this, "Thanks for reporting. We will take action against it soon.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private void startCommenting() {

        //FirebaseMessaging.getInstance().subscribeToTopic(post_key);

        final String comment_val = meditComment.getText().toString().trim();

        final String token = FirebaseInstanceId.getInstance().getToken();

        meditComment.setText("");

        mProgress.setMessage("Commenting..");

        if (!TextUtils.isEmpty(comment_val)) {

            mProgress.show();

            final DatabaseReference newPost = mComment.child(post_key).push();
            final String commentkey = newPost.getKey();
            final DatabaseReference newSubsribe = mUserSubscribe.child(mAuth.getCurrentUser().getUid());

            mUser.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String,Object> checkoutData=new HashMap<>();
                    checkoutData.put("time",ServerValue.TIMESTAMP);
                    newPost.setValue(checkoutData);
                    newPost.child("comment").setValue(comment_val);
                    newPost.child("uid").setValue(dataSnapshot.child("id").getValue());
                    newPost.child("userimage").setValue(dataSnapshot.child("image").getValue());
                    newPost.child("username").setValue(dataSnapshot.child("name").getValue());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mUserSubscribe.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                        if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(post_key)){

                        }
                        else{
                            mUserSubscribe.child(mAuth.getCurrentUser().getUid()).child(post_key).setValue(true);
                        }
                    }
                    else{
                        newSubsribe.child("uid").setValue(mAuth.getCurrentUser().getUid());
                        newSubsribe.child(post_key).setValue(true);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mUserSubscribe.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot getSnapshot: dataSnapshot.getChildren()) {
                        final String userid = getSnapshot.child("uid").getValue().toString();
                        if(userid.equals(mAuth.getCurrentUser().getUid())) {
                        }
                        else{
                            if(getSnapshot.hasChild(post_key)){
                                if(userid.equals(post_uid)){
                                    final DatabaseReference newCommentLike = mCommentLike.child(userid).push();
                                    Map<String, Object> checkTime = new HashMap<>();
                                    checkTime.put("time", ServerValue.TIMESTAMP);
                                    newCommentLike.setValue(checkTime);
                                    newCommentLike.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                    newCommentLike.child("username").setValue(commentusername);
                                    newCommentLike.child("comment").setValue(" '" + comment_val + "' ");
                                    newCommentLike.child("userimage").setValue(commentuserimage);
                                    newCommentLike.child("location").setValue(location_key);
                                    newCommentLike.child("category").setValue(category_key);
                                    newCommentLike.child("pressed").setValue("false");
                                    newCommentLike.child("commentkey").setValue(commentkey);
                                    newCommentLike.child("comment").setValue(comment_val);
                                    newCommentLike.child("message").setValue(" has commented on your post. ");
                                    newCommentLike.child("blogpost").setValue(post_key);
                                    newCommentLike.child(post_key).setValue(true);


                                }
                                else{
                                    final DatabaseReference newCommentLike = mCommentLike.child(userid).push();
                                    Map<String, Object> checkTime = new HashMap<>();
                                    checkTime.put("time", ServerValue.TIMESTAMP);
                                    newCommentLike.setValue(checkTime);
                                    newCommentLike.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                    newCommentLike.child("username").setValue(commentusername);
                                    newCommentLike.child("comment").setValue(" '" + comment_val + "' ");
                                    newCommentLike.child("userimage").setValue(commentuserimage);
                                    newCommentLike.child("location").setValue(location_key);
                                    newCommentLike.child("category").setValue(category_key);
                                    newCommentLike.child("pressed").setValue("false");
                                    newCommentLike.child("commentkey").setValue(commentkey);
                                    newCommentLike.child("comment").setValue(comment_val);
                                    newCommentLike.child("message").setValue(" has also commented on "+post_username+"'s post");
                                    newCommentLike.child("blogpost").setValue(post_key);
                                    newCommentLike.child(post_key).setValue(true);

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mProgress.dismiss();
        }
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        TextView post_username;

        CircleImageView post_userimage;

        ImageButton mactionBtn;

        DatabaseReference mComment,mBlog;
        FirebaseAuth mAuth;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mactionBtn = (ImageButton)mView.findViewById(R.id.actionBtn);
            post_username = (TextView)mView.findViewById(R.id.postName);
            post_userimage = (CircleImageView) mView.findViewById(R.id.userimage);

            post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("MainACTIVITY", "Some Text");
                }
            });

            mAuth = FirebaseAuth.getInstance();

            mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
            mBlog.keepSynced(true);

            mComment =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Comment");
            mComment.keepSynced(true);

        }

        public void setTime(final String post_key, final String comment_key){

            mComment.child(post_key).child(comment_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("time")) {

                        Long time = (Long) dataSnapshot.child("time").getValue();

                        Long tsLong = System.currentTimeMillis();

                        CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, 0);

                        //String value = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").
                        //format(new java.util.Date(time));

                        TextView post_time = (TextView) mView.findViewById(R.id.txtTime);
                        post_time.setText(result);
                    }
                    else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public  void setUsername(String username){
            TextView post_username = (TextView)mView.findViewById(R.id.postName);
            post_username.setText(username);
        }

        public  void setComment(String comment){
            TextView post_comment = (TextView)mView.findViewById(R.id.txtuserComment);
            post_comment.setText(comment);
            Log.d(TAG, "Comment: " + comment);
        }

        public void setuserImage(Context ctx, String userimage){
            if (userimage != null && userimage.equals("default")){
                post_userimage.setImageResource(R.mipmap.profilepic);
            }
            else {
                Picasso.with(ctx).load(userimage).fit().centerCrop().into(post_userimage);
            }
        }
    }
}
