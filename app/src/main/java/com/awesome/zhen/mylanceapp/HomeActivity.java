package com.awesome.zhen.mylanceapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeActivity extends Fragment {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private Button mLocationbtn, msearchRlay;
    private  ImageButton mSearch;

    private DatabaseReference mBlog;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mCommentLike;
    private DatabaseReference mUsers;

    private boolean mProcessLike = false;
    private boolean mProcessCommentLike = false;

    private Query mQueryCurrentUser;

    private String location_key;
    private String category_key;
    private String user_name;
    private String user_image;

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        context=getActivity();

        mAuth = FirebaseAuth.getInstance();

        mLocationbtn = (Button) rootView.findViewById(R.id.Locationbtn);
        location_key = "DUBLIN";
        category_key = "招聘";
        mLocationbtn.setText(location_key);

        mSearch = (ImageButton) rootView.findViewById(R.id.Searchbtn);
        msearchRlay = (Button)  rootView.findViewById(R.id.searchRlay);

        mLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRadioButtonDialogLocation();
            }
        });

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

        msearchRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    msearchRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    msearchRlay.setBackgroundColor(Color.parseColor("#FF45CBF7"));
                }
                return false;
            }

        });
        msearchRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSearch.startAnimation(buttonClick);
                location_key = mLocationbtn.getText().toString().trim();

                if (!TextUtils.isEmpty(location_key)){
                    Bundle bundle = new Bundle();
                    bundle.putString("location_id",location_key);
                    bundle.putString("category_id",category_key);

                    FragmentManager fragmentManager2 = getFragmentManager();
                    FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                    Search_post fragment2 = new Search_post();
                    fragment2.setArguments(bundle);
                    fragmentTransaction2.replace(R.id.flContent, fragment2,"SearchPostHome");
                    fragmentTransaction2.commit();
                }
              else{
                    Toast.makeText(getApplicationContext(),"Please Enter Location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
        mBlog.keepSynced(true);

        mCommentLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mUsers =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsers.keepSynced(true);

        mBlogList = (RecyclerView)rootView.findViewById(R.id.blog_list);
        mBlogList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        mBlogList.setLayoutManager(mLayoutManager);

        final FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {

            mUsers.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("name")) {
                        user_name = dataSnapshot.child("name").getValue(String.class);
                    }
                    if (dataSnapshot.hasChild("image")) {
                        user_image = dataSnapshot.child("image").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        final FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,PostActivity.class));
            }
        });

        FirebaseRecyclerAdapter<bLOG,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<bLOG, BlogViewHolder>(
                bLOG.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mBlog.child(category_key).child(location_key)

        ) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, bLOG model, int position) {

                final String post_key = getRef(position).getKey();
                final String owneruid = model.getUid();
                final String imageid = model.getImage();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setTelephone(model.getTelephone());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setImage(context.getApplicationContext(),model.getImage());
                viewHolder.setuserImage(context.getApplicationContext(),model.getuserImage());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setCommentBtn(post_key);
                viewHolder.setTime(category_key,location_key,post_key);


                viewHolder.mRlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageid.equals("default")){
                            Intent singleblogintent = new Intent(context, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",post_key);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(context, SinglePost.class);
                            singleblogintent.putExtra("blog_id",post_key);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                    }
                });

                viewHolder.mCommentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageid.equals("default")){
                            Intent singleblogintent = new Intent(context, SinglePostNoImage.class);
                            singleblogintent.putExtra("blog_id",post_key);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(context, SinglePost.class);
                            singleblogintent.putExtra("blog_id",post_key);
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
                            singleblogintent.putExtra("blog_id",post_key);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                        else{
                            Intent singleblogintent = new Intent(context, SinglePost.class);
                            singleblogintent.putExtra("blog_id",post_key);
                            singleblogintent.putExtra("category_id",category_key);
                            singleblogintent.putExtra("location_id",location_key);
                            startActivity(singleblogintent);
                        }
                    }
                });


                viewHolder.post_userimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mBlog.child(category_key).child(location_key).child(post_key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild("uid")) {
                                    String userid = dataSnapshot.child("uid").getValue().toString();
                                    Intent singleuserintent = new Intent(context, OtherUser.class);
                                    singleuserintent.putExtra("userid", userid);
                                    startActivity(singleuserintent);
                                }

                            }
                            @Override
                            public void onCancelled (DatabaseError databaseError){

                            }
                        });
                    }
                });



                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;
                        mProcessCommentLike = true;

                        final DatabaseReference newPost = mCommentLike.child(owneruid).push();
                        /*mQueryCurrentUser = mCommentLike.child(owneruid).orderByChild("blogpost").equalTo(post_key);*/
                        /*for (DataSnapshot getSnapshot: dataSnapshot.getChildren()) {
                                    getSnapshot.getRef().removeValue();
                                }*/

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


            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
        return rootView;
    }

    private void showRadioButtonDialogLocation() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
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
                        mLocationbtn.setText(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
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
            post_userimage = (CircleImageView) mView.findViewById(R.id.userimage);
            mLikebtn = (ImageButton) mView.findViewById(R.id.likeBtn);
            post_image = (ImageView)mView.findViewById(R.id.postImage);
            mCommentLayout = (RelativeLayout) mView.findViewById(R.id.commentLayout);
            mcommentBtn = (ImageButton) mView.findViewById(R.id.commentBtn);

            post_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("MainACTIVITY", "Some Text");
                }
            });

            mAuth = FirebaseAuth.getInstance();
            mBlog =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
            mBlog.keepSynced(true);
            mDatabaseLike = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
            mDatabaseLike.keepSynced(true);
            mComment = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Comment");
            mComment.keepSynced(true);

        }

        public void setLikeBtn(final String post_key){

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.child(post_key).getChildrenCount();
                    mtxtlikeNum.setText(String.valueOf(count));

                    final FirebaseUser mUser= mAuth.getCurrentUser();
                    if (mUser != null) {

                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                            mLikebtn.setImageResource(R.drawable.red_love);
                            mLikebtn.setImageAlpha(255);

                        } else {

                            mLikebtn.setImageResource(R.drawable.grey_love);
                            mLikebtn.setImageAlpha(200);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setCommentBtn(final String post_key){

            mComment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.child(post_key).getChildrenCount();
                    mtxtcommentNum.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTime(final String category_key, final String location_key,final String post_key){

            mBlog.child(category_key).child(location_key).child(post_key).addValueEventListener(new ValueEventListener() {
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
