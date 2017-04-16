package com.awesome.zhen.mylanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import android.support.v4.app.FragmentTransaction;

public class SingleUser extends Fragment {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private DatabaseReference mBlog;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUsername;
    private DatabaseReference mDatabaseCurrentUser;

    private Query mQueryCurrentUser;

    private boolean mProcessLike = false;

    private Button muserpostBtn, muserprofileBtn;

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.single_user, container, false);
        context=getActivity();

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mUsername = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
        mBlog.keepSynced(true);

        String currentUserId = mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);

        mBlogList = (RecyclerView)rootView.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        muserpostBtn = (Button)rootView.findViewById(R.id.userpostBtn);
        muserpostBtn.setAlpha(0.7F);
        muserprofileBtn = (Button)rootView.findViewById(R.id.userprofileBtn);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

        muserprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                muserprofileBtn.startAnimation(buttonClick);
                muserprofileBtn.setAlpha(0.7F);
                muserpostBtn.setAlpha(1F);

               /* Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = UserProfile.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.PostFragment, fragment).commit();*/


                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                SingleUserProfile fragment2 = new SingleUserProfile();
                fragmentTransaction2.replace(R.id.PostFragment, fragment2);
                fragmentTransaction2.commit();


            }
        });

        muserpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                muserpostBtn.startAnimation(buttonClick);
                muserpostBtn.setAlpha(0.7F);
                muserprofileBtn.setAlpha(1F);

                Bundle bundle = new Bundle();

                FragmentManager fragmentManager2 = getFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                Single_userpost fragment2 = new Single_userpost();
                fragment2.setArguments(bundle);
                fragmentTransaction2.replace(R.id.PostFragment, fragment2);
                fragmentTransaction2.commit();


            }
        });

        FirebaseRecyclerAdapter<bLOG,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bLOG, BlogViewHolder>(
                bLOG.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mQueryCurrentUser

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, bLOG model, int position) {

                final String post_key = getRef(position).getKey();
                final String keyval = model.getKey();
                final String category_key = model.getCategory();
                final String location_key = model.getLocation();
                final String imageid = model.getImage();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setTelephone(model.getTelephone());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setImage(context.getApplicationContext(),model.getImage());
                viewHolder.setuserImage(context.getApplicationContext(),model.getuserImage());
                viewHolder.setLikeBtn(keyval);
                viewHolder.setCommentBtn(keyval);
                viewHolder.setTime(post_key);

                viewHolder.mRlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleblogintent = new Intent(context, SinglePost.class);
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
                            Intent singleblogintent = new Intent(context, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(context, SinglePost.class);
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
                            Intent singleblogintent = new Intent(context, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",keyval);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(context, SinglePost.class);
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

                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mProcessLike) {

                                    if (dataSnapshot.child(keyval).hasChild(mAuth.getCurrentUser().getUid())) {

                                        mDatabaseLike.child(keyval).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mProcessLike = false;

                                    } else {
                                        mDatabaseLike.child(keyval).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
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
        return rootView;
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
                    if (dataSnapshot.child(keyval).hasChild(mAuth.getCurrentUser().getUid())) {

                        mLikebtn.setImageResource(R.drawable.red_love);
                        mLikebtn.setImageAlpha(255);

                    } else {

                        mLikebtn.setImageResource(R.drawable.grey_love);
                        mLikebtn.setImageAlpha(200);

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

                        //String value = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").
                        //format(new java.util.Date(time));

                        TextView post_time = (TextView) mView.findViewById(R.id.txtTime);
                        post_time.setText(result);
                    }

                    // }
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
