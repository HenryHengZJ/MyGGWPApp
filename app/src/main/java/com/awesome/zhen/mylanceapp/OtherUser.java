package com.awesome.zhen.mylanceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUser extends AppCompatActivity {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private DatabaseReference mCommentLike;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUsername;
    private DatabaseReference mDatabaseCurrentUser;
    private DatabaseReference mFollower;

    private Query mQueryCurrentUser;

    private boolean mProcessLike = false;
    private boolean mProcessCommentLike = false;

    private Toolbar mToolbar;
    private TextView mprofiletxtName;
    private CircleImageView mprofilebg;
    private Button muserpostBtn, muserprofileBtn, mfollowBtn;

    private String user_key, user_image, user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user);

        mAuth = FirebaseAuth.getInstance();

        user_key = getIntent().getExtras().getString("userid");

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mUsername = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mFollower = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("followers");

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(user_key);

        mBlogList = (RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        mfollowBtn = (Button) findViewById(R.id.followBtn);
        muserpostBtn = (Button) findViewById(R.id.userpostBtn);
        muserpostBtn.setAlpha(0.7F);
        muserprofileBtn = (Button) findViewById(R.id.userprofileBtn);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

        mprofiletxtName = (TextView) findViewById(R.id.txtprofileName);
        mprofilebg = (CircleImageView) findViewById(R.id.profilepic);

        mfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = mAuth.getCurrentUser().getUid();

                DatabaseReference current_followed_db = mFollower.child(user_key);
                current_followed_db.child(uid).setValue(true);

            }
        });

        muserprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                muserprofileBtn.startAnimation(buttonClick);
                muserprofileBtn.setAlpha(0.7F);
                muserpostBtn.setAlpha(1F);

                getUserKey();

                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = UserProfile.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.PostFragment, fragment).commit();

            }
        });

        muserpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                muserpostBtn.startAnimation(buttonClick);
                muserpostBtn.setAlpha(0.7F);
                muserprofileBtn.setAlpha(1F);

                getUserKey();
                getCurrentUserName();
                getCurrentUserImage();

                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = Other_userpost.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.PostFragment, fragment).commit();


            }
        });

        mToolbar = (Toolbar)findViewById(R.id.toolbar_other);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_other);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar_other);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("账号");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        if (user_key != null && user_key != "") {

            mUsername.child(user_key).child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String data = dataSnapshot.getValue(String.class);
                    if (data != null && data.equals("default")) {
                        mprofilebg.setImageResource(R.mipmap.profilepic);
                    } else {
                        new ImageLoadTask(data, mprofilebg).execute();
                    }
                }

                //onCancelled is called in case of any error
                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });

            mUsername.child(user_key).child("name").addValueEventListener(new ValueEventListener() {
                //onDataChange is called every time the name of the User changes in your Firebase Database
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Inside onDataChange we can get the data as an Object from the dataSnapshot
                    //getValue returns an Object. We can specify the type by passing the type expected as a parameter
                    String data = dataSnapshot.getValue(String.class);
                    mprofiletxtName.setText(data);
                }

                //onCancelled is called in case of any error
                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });

            mUsername.child(mAuth.getCurrentUser().getUid()).child("image").addValueEventListener(new ValueEventListener() {

                //onDataChange is called every time the name of the User changes in your Firebase Database
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_image = dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });

            mUsername.child(mAuth.getCurrentUser().getUid()).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_name = dataSnapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }


        FirebaseRecyclerAdapter<bLOG,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bLOG, BlogViewHolder>(
                bLOG.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQueryCurrentUser

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, bLOG model, int position) {

                final String post_key = getRef(position).getKey();//all blog post key
                final String imageid = model.getImage();

                final String keyval = model.getKey();//category, lcoation, post key
                final String location_key = model.getLocation();
                final String category_key = model.getCategory();
                final String owneruid = model.getUid();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setTelephone(model.getTelephone());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setuserImage(getApplicationContext(),model.getuserImage());
                viewHolder.setLikeBtn(keyval);
                viewHolder.setCommentBtn(keyval);
                viewHolder.setTime(post_key);

                viewHolder.mRlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleblogintent = new Intent(OtherUser.this, SinglePost.class);
                        singleblogintent.putExtra("blog_id",keyval);
                        singleblogintent.putExtra("category_id",category_key);
                        singleblogintent.putExtra("location_id",location_key);
                        startActivity(singleblogintent);
                    }
                });

                viewHolder.mCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageid.equals("default")){
                            Intent singleblogintent = new Intent(OtherUser.this, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(OtherUser.this, SinglePost.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                    }
                });

                viewHolder.mcommentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageid.equals("default")){
                            Intent singleblogintent = new Intent(OtherUser.this, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(OtherUser.this, SinglePost.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                    }
                });


                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;
                        mProcessCommentLike = true;
                        final DatabaseReference newPost = mCommentLike.child(owneruid).push();

                        mCommentLike.child(owneruid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessCommentLike) {
                                    for (DataSnapshot getSnapshot : dataSnapshot.getChildren()) {
                                        String blogkey = getSnapshot.child("blogpost").getValue().toString();
                                        String message = getSnapshot.child("message").getValue().toString();
                                        String userid = getSnapshot.child("uid").getValue().toString();

                                        if(userid.equals(mAuth.getCurrentUser().getUid())){
                                            if(message.equals(" has liked your post. ")){
                                                if(blogkey.equals(keyval)){
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

                                    if (dataSnapshot.child(keyval).hasChild(mAuth.getCurrentUser().getUid())) {
                                        mDatabaseLike.child(keyval).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;
                                    }
                                    else {
                                        mDatabaseLike.child(keyval).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                        if (owneruid.equals(mAuth.getCurrentUser().getUid())) {
                                        }
                                        else{
                                            Map<String, Object> checkoutData = new HashMap<>();
                                            checkoutData.put("time", ServerValue.TIMESTAMP);
                                            newPost.setValue(checkoutData);
                                            newPost.child("uid").setValue(mAuth.getCurrentUser().getUid());
                                            newPost.child("username").setValue(user_name);
                                            newPost.child("userimage").setValue(user_image);
                                            newPost.child("location").setValue(location_key);
                                            newPost.child("category").setValue(category_key);
                                            newPost.child("pressed").setValue("false");
                                            newPost.child("message").setValue(" has liked your post. ");
                                            newPost.child("blogpost").setValue(keyval);
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
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public String getUserKey() {
        return user_key;
    }
    public String getCurrentUserName() {
        return user_name;
    }
    public String getCurrentUserImage() {
        return user_image;
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

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private CircleImageView mprofileImage;

        public ImageLoadTask(String url, CircleImageView mprofileImage) {
            this.url = url;
            this.mprofileImage = mprofileImage;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            mprofileImage.setImageBitmap(result);
        }

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageButton mLikebtn, mcommentBtn;
        TextView post_username,mtxtlikeNum,mtxtcommentNum,mtxtshareNum;
        ImageView post_image;
        CircleImageView post_userimage;
        RelativeLayout mRlayout, mCommentLayout;

        DatabaseReference mDatabaseLike,mBlog,mComment;
        FirebaseAuth mAuth;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            mtxtlikeNum = (TextView) mView.findViewById(R.id.txtlikeNum);
            mtxtcommentNum = (TextView) mView.findViewById(R.id.txtcommentNum);
            post_username = (TextView)mView.findViewById(R.id.postName);
            mRlayout = (RelativeLayout) mView.findViewById(R.id.Rlayout);
            mCommentLayout = (RelativeLayout) mView.findViewById(R.id.commentLayout);

            post_userimage = (CircleImageView) mView.findViewById(R.id.userimage);
            mLikebtn = (ImageButton) mView.findViewById(R.id.likeBtn);
            mcommentBtn = (ImageButton) mView.findViewById(R.id.commentBtn);

            post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("MainACTIVITY", "Some Text");
                }
            });

            mAuth = FirebaseAuth.getInstance();
            mDatabaseLike = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
            mDatabaseLike.keepSynced(true);
            mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
            mBlog.keepSynced(true);
            mComment = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Comment");
            mComment.keepSynced(true);

        }

        public void setLikeBtn(final String keyval){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final FirebaseUser mUser= mAuth.getCurrentUser();
                    if (mUser != null) {

                        if (dataSnapshot.child(keyval).hasChild(mAuth.getCurrentUser().getUid())) {

                            mLikebtn.setImageResource(R.drawable.red_love);
                            mLikebtn.setImageAlpha(255);

                        } else {

                            mLikebtn.setImageResource(R.drawable.grey_love);
                            mLikebtn.setImageAlpha(200);

                        }
                    }
                    long count = dataSnapshot.child(keyval).getChildrenCount();
                    mtxtlikeNum.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setCommentBtn(final String keyval){

            mComment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.child(keyval).getChildrenCount();
                    mtxtcommentNum.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void setTime(final String post_key){

            mBlog.child(post_key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("time")) {

                        Long time = (Long) dataSnapshot.child("time").getValue();

                        Long tsLong = System.currentTimeMillis();

                        CharSequence result = DateUtils.getRelativeTimeSpanString(time, tsLong, 0);

                        TextView post_time = (TextView) mView.findViewById(R.id.txtTime);
                        post_time.setText(result);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.postTitle);
            post_title.setText(title);
        }
        public  void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.postDescrip);
            post_desc.setText(desc);
        }
        public  void setTelephone(String telephone){
            TextView post_telephone = (TextView)mView.findViewById(R.id.txtTel);
            post_telephone.setText(telephone);
        }
        public  void setLocation(String location){
            TextView post_location = (TextView)mView.findViewById(R.id.txtLocation);
            post_location.setText(location);
        }
        public  void setUsername(String username){
            TextView post_username = (TextView)mView.findViewById(R.id.postName);
            post_username.setText(username);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView)mView.findViewById(R.id.postImage);
            if(image != null && image.equals("default")){
                post_image.setVisibility(View.GONE);
            }
            else {
                post_image.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(image).fit().centerCrop().into(post_image);
            }
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
