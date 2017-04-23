package com.awesome.zhen.mylanceapp;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.VISIBLE;

public class Notification extends AppCompatActivity {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private DatabaseReference mCommentLike, mBlog;

    private boolean mProcessLike = false;

    private RelativeLayout mnonotifiRlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_post);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String currentUserId = mAuth.getCurrentUser().getUid();

        mnonotifiRlay = (RelativeLayout) findViewById(R.id.nonotifiRlay);

        mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");
        mBlog.keepSynced(true);

        mBlogList = (RecyclerView)findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        mCommentLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentUserId)){
                    if(dataSnapshot.child(currentUserId).getChildrenCount()==0){
                        mnonotifiRlay.setVisibility(VISIBLE);
                    }
                }
                else{
                    mnonotifiRlay.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<CommentLike,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CommentLike, BlogViewHolder>(
                CommentLike.class,
                R.layout.notification_row,
                BlogViewHolder.class,
                mCommentLike.child(currentUserId)

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, CommentLike model, int position) {

                final String post_key = getRef(position).getKey();
                final String category_key = model.getCategory();
                final String location_key = model.getLocation();
                final String key_val = model.getBlogpost();

                viewHolder.setComment(model.getComment());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setMessage(model.getMessage());
                viewHolder.setUserImage(getApplicationContext(),model.getUserImage());
                viewHolder.setTime(post_key);

                viewHolder.mRlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleblogintent = new Intent(Notification.this, SinglePost.class);
                        singleblogintent.putExtra("blog_id",key_val);
                        singleblogintent.putExtra("category_id",category_key);
                        singleblogintent.putExtra("location_id",location_key);
                        startActivity(singleblogintent);
                    }
                });
            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
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

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView post_username,mmessage,mcomment;
        CircleImageView post_userimage;
        LinearLayout mRlayout;

        DatabaseReference mCommentLike;
        FirebaseAuth mAuth;


        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            post_username = (TextView)mView.findViewById(R.id.postName);
            mmessage = (TextView)mView.findViewById(R.id.txtMessage);
            mcomment = (TextView)mView.findViewById(R.id.txtComment);
            mRlayout = (LinearLayout) mView.findViewById(R.id.Rlayout);
            post_userimage = (CircleImageView) mView.findViewById(R.id.userimage);

            mAuth = FirebaseAuth.getInstance();

            mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");;
            mCommentLike.keepSynced(true);

        }

        public void setTime(final String post_key){

            mCommentLike.child(mAuth.getCurrentUser().getUid()).child(post_key).addValueEventListener(new ValueEventListener() {
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

        public void setMessage(String message){
            mmessage.setText(message);
        }

        public void setComment(String comment){
            mcomment.setVisibility(VISIBLE);
            mcomment.setText(comment);
        }

        public  void setUsername(String username){
            post_username.setText(username);
        }
        public void setUserImage(Context ctx, String userimage){
            if (userimage != null && userimage.equals("default")){
                post_userimage.setImageResource(R.mipmap.profilepic);
            }
            else {
                Picasso.with(ctx).load(userimage).fit().centerCrop().into(post_userimage);
            }
        }
    }
}
