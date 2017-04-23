package com.awesome.zhen.mylanceapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Other_userpost extends Fragment {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private DatabaseReference mBlog;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUsername;
    private DatabaseReference mDatabaseCurrentUser;
    private DatabaseReference mCommentLike;

    private Query mQueryCurrentUser;

    private boolean mProcessLike = false;
    private boolean mProcessCommentLike = false;

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.single_userpost, container, false);
        context=getActivity();

        OtherUser activity = (OtherUser) getActivity();
        final String userkey = activity.getUserKey();
        final String currentusername = activity.getCurrentUserName();
        final String currentuserimage = activity.getCurrentUserImage();

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mUsername = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
        mBlog.keepSynced(true);

        mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(userkey);

        mBlogList = (RecyclerView)rootView.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        FirebaseRecyclerAdapter<bLOG,OtherUser.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bLOG, OtherUser.BlogViewHolder>(
                bLOG.class,
                R.layout.blog_row,
                OtherUser.BlogViewHolder.class,
                mQueryCurrentUser

        ) {

            @Override
            protected void populateViewHolder(OtherUser.BlogViewHolder viewHolder, bLOG model, int position) {

                final String post_key = getRef(position).getKey();//alblog key
                final String keyval = model.getKey();//category,locationblog key
                final String location_key = model.getLocation();
                final String category_key = model.getCategory();
                final String owneruid = model.getUid();
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
                                            newPost.child("username").setValue(currentusername);
                                            newPost.child("userimage").setValue(currentuserimage);
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
        return rootView;
    }
}
