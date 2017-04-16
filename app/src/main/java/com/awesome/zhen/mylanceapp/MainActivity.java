package com.awesome.zhen.mylanceapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.category;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private LinearLayoutManager mLayoutManager;

    private TextView mtxtName,mprofiletxtName,mtxtDota, mcountNotification;
    private CircleImageView mprofileImage,mprofilebg;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView nvDrawer;
    private ImageView mtitleimage;
    private ImageButton mhomeBtn, mnotifiBtn, mprofileBtn;
    private Button mhomeRlay, mnotifiRlay, mprofileRlay;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mUsername;
    private DatabaseReference mCommentLike;
    private DatabaseReference mUsersInfo;
    private Query mQueryCurrentUser, mQueryCommentLike;

    private boolean mToken = false;

    private long totalnotificationcount =0;
    private int badgeCount = 0;

    private String user_name,user_image, lastFragTag, Commentuser_name,UserName,Blogkey,Category,Location,OnlyLike,Commentkey,CommentkeyOld = null;
    private String CommentMessage;
    private static final String TAG = "MainActivity";
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_main);
        nvDrawer = (NavigationView)findViewById(R.id.nvView);
        mtitleimage = (ImageView) findViewById(R.id.titleimage);
        mprofiletxtName = (TextView) findViewById(R.id.txtprofileName);
        mtxtDota = (TextView) findViewById(R.id.txtDota);
        mprofilebg = (CircleImageView) findViewById(R.id.profilepic);
        mhomeBtn = (ImageButton) findViewById(R.id.homeBtn);
        mhomeBtn.setAlpha(1F);
        mnotifiBtn = (ImageButton) findViewById(R.id.notificationBtn);
        mprofileBtn = (ImageButton) findViewById(R.id.profileBtn);
        mhomeRlay = (Button) findViewById(R.id.homeRlay);
        mnotifiRlay = (Button) findViewById(R.id.notifiRlay);
        mprofileRlay = (Button) findViewById(R.id.profileRlay);
        mcountNotification = (TextView) findViewById(R.id.txtcountNotification);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("招聘");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        setupDrawerContent(nvDrawer);
        View hView =  nvDrawer.inflateHeaderView(R.layout.navigation_header);
        mtxtName = (TextView) hView.findViewById(R.id.txtName);
        mprofileImage = (CircleImageView) hView.findViewById(R.id.profile_image);

        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mAuth = FirebaseAuth.getInstance();

        mDatabaseLike =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Likes");
        mDatabaseLike.keepSynced(true);

        mUsername =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mCommentLike = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("CommentLike");
        mCommentLike.keepSynced(true);

        mUsersInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
        mUsersInfo.keepSynced(true);

        final FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {

            //Get the uid for the currently logged in User from intent data passed to this activity
            String useruid = getIntent().getExtras().getString("user_id");

            if (useruid != null && useruid != "") {

                mUsername.child(useruid).child("image").addValueEventListener(new ValueEventListener() {

                    //onDataChange is called every time the name of the User changes in your Firebase Database
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Inside onDataChange we can get the data as an Object from the dataSnapshot
                        //getValue returns an Object. We can specify the type by passing the type expected as a parameter
                        user_image = dataSnapshot.getValue(String.class);
                        if (user_image != null && user_image.equals("default")) {
                            mprofilebg.setImageResource(R.mipmap.profilepic);
                            mprofileImage.setImageResource(R.mipmap.profilepic);
                        } else {
                            Picasso.with(MainActivity.this).load(user_image).fit().centerCrop().into(mprofileImage);
                            Picasso.with(MainActivity.this).load(user_image).fit().centerCrop().into(mprofilebg);
                           // new ImageLoadTask(data, mprofileImage).execute();
                           // new ImageLoadTask(data, mprofilebg).execute();
                        }
                    }

                    //onCancelled is called in case of any error
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });

                mUsername.child(useruid).child("name").addValueEventListener(new ValueEventListener() {
                    //onDataChange is called every time the name of the User changes in your Firebase Database
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Inside onDataChange we can get the data as an Object from the dataSnapshot
                        //getValue returns an Object. We can specify the type by passing the type expected as a parameter
                        user_name = dataSnapshot.getValue(String.class);
                        mtxtName.setText( user_name );
                        mprofiletxtName.setText(user_name);
                    }

                    //onCancelled is called in case of any error
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
            }
        }

        mUsersInfo.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("notificationTokens")){
                    mToken = true;
                }
                else{
                    mToken = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Blog");;
        mDatabase.keepSynced(true);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.flContent, new HomeActivity(), "Home");
        tx.commit();

        mQueryCommentLike = mCommentLike.child(mAuth.getCurrentUser().getUid()).orderByChild("pressed").equalTo("false");

        mQueryCommentLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                totalnotificationcount = dataSnapshot.getChildrenCount();

                if(totalnotificationcount == 0){
                    mcountNotification.setVisibility(GONE);
                }
                else{
                    mcountNotification.setVisibility(VISIBLE);
                    mcountNotification.setText(String.valueOf(totalnotificationcount));
                    Log.d(TAG, "totalnotificationcount: " + totalnotificationcount);
                }

                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    UserName = issue.child("username").getValue().toString();
                    if(issue.hasChild("blogpost")){
                        Blogkey = issue.child("blogpost").getValue().toString();
                    }
                    else{
                        return;
                    }
                    Location = issue.child("location").getValue().toString();
                    Category = issue.child("category").getValue().toString();
                    if(issue.hasChild("commentkey")){
                        OnlyLike = "No";
                        Commentkey = issue.child("commentkey").getValue().toString();
                        CommentMessage = issue.child("message").getValue().toString();
                    }
                    else{
                        OnlyLike = "Yes";
                        Commentkey = null;
                    }
                }

                Log.d(TAG, "UserName: " + UserName);
                Log.d(TAG, "Blogkey: " + Blogkey);
                Log.d(TAG, "Location: " + Location);
                Log.d(TAG, "Category: " + Category);
                Log.d(TAG, "OnlyLike: " + OnlyLike);
                Log.d(TAG, "Commentkey: " + Commentkey);
                Log.d(TAG, "CommentMessage: " + CommentMessage);

                if(totalnotificationcount!=0 && Blogkey!=null&& UserName!=null&& OnlyLike!=null&& Location!=null&& Category!=null&& Commentkey!=null){

                    if(Commentkey.equals(CommentkeyOld)){

                    }
                    else{

                        if(mToken==true){
                            badgeCount=(int)totalnotificationcount;
                            ShortcutBadger.applyCount(MainActivity.this, badgeCount); //for 1.1.4
                            startService(
                                    new Intent(MainActivity.this, BadgeIntentService.class).putExtra("badgeCount", badgeCount)
                                            .putExtra("UserName", UserName)
                                            .putExtra("Blogkey", Blogkey)
                                            .putExtra("Location", Location)
                                            .putExtra("Category", Category)
                                            .putExtra("OnlyLike", OnlyLike)
                                            .putExtra("CommentMessage", CommentMessage)
                            );
                        }
                    }
                }

                CommentkeyOld = Commentkey;
                Log.d(TAG, "CommentkeyOld: " + CommentkeyOld);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.7F);

        mhomeRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mhomeRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mhomeRlay.setBackgroundColor(Color.LTGRAY);
                }
                return false;
            }

        });

        mnotifiRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mnotifiRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mnotifiRlay.setBackgroundColor(Color.LTGRAY);
                }
                return false;
            }

        });

        mprofileRlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    mprofileRlay.setBackgroundColor(Color.TRANSPARENT);
                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    mprofileRlay.setBackgroundColor(Color.LTGRAY);
                }
                return false;
            }

        });

        mhomeRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mhomeBtn.setAlpha(1F);
                mnotifiBtn.setAlpha(0.5F);
                mprofileBtn.setAlpha(0.5F);

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    int lastFragEntry =  getSupportFragmentManager().getBackStackEntryCount()-1;
                    lastFragTag = getSupportFragmentManager().getBackStackEntryAt(lastFragEntry).getName();
                    Log.i("Last Fragment Tag->", lastFragTag);

                    if(lastFragTag.equals("Business")){
                        mtitleimage.setImageResource(R.drawable.business_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("商务");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("商务");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("SearchPostBusiness")){
                        mtitleimage.setImageResource(R.drawable.business_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("商务");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("商务");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }

                    else if(lastFragTag.equals("SecondHand")){
                        mtitleimage.setImageResource(R.drawable.secondhand_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("二手");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("二手");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("SearchPostSecondHand")){
                        mtitleimage.setImageResource(R.drawable.secondhand_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("二手");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("二手");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("Rent")){
                        mtitleimage.setImageResource(R.drawable.rent_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("租房");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("租房");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("SearchPostRent")){
                        mtitleimage.setImageResource(R.drawable.rent_background);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("租房");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("租房");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("Home")){
                        mtitleimage.setImageResource(R.drawable.desk_wallpaper);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("招聘");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("招聘");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                    else if(lastFragTag.equals("SearchPostHome")){
                        mtitleimage.setImageResource(R.drawable.desk_wallpaper);
                        mprofilebg.setVisibility(View.INVISIBLE);
                        mtxtDota.setVisibility(VISIBLE);
                        mtxtDota.setText("招聘");
                        mprofiletxtName.setVisibility(View.INVISIBLE);

                        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            boolean isShow = false;
                            int scrollRange = -1;
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                if (scrollRange == -1) {
                                    scrollRange = appBarLayout.getTotalScrollRange();
                                }
                                if (scrollRange + verticalOffset == 0) {
                                    collapsingToolbarLayout.setTitle("招聘");
                                    isShow = true;
                                } else if(isShow) {
                                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                                    isShow = false;
                                }
                            }
                        });
                    }
                }
                else{

                }


            }
        });

        mnotifiRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContent);

                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();

                mnotifiBtn.startAnimation(buttonClick);
                mnotifiBtn.setAlpha(1F);
                mhomeBtn.setAlpha(0.5F);
                mprofileBtn.setAlpha(0.5F);

                totalnotificationcount = 0;
                mcountNotification.setVisibility(GONE);
                mcountNotification.setText(String.valueOf(totalnotificationcount));

                mQueryCurrentUser = mCommentLike.child(mAuth.getCurrentUser().getUid()).orderByChild("pressed").equalTo("false");
                mQueryCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            dsp.getRef().child("pressed").setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(MainActivity.this, Notification.class);
                startActivity(intent);

                ShortcutBadger.removeCount(MainActivity.this);

                if (currentFragment.getTag().equals("Business")
                        ||currentFragment.getTag().equals("SearchPostBusiness")
                        ||currentFragment.getTag().equals("SecondHand")
                        ||currentFragment.getTag().equals("SearchPostSecondHand")
                        ||currentFragment.getTag().equals("Rent")
                        ||currentFragment.getTag().equals("SearchPostRent")
                        ||currentFragment.getTag().equals("Home")
                        ||currentFragment.getTag().equals("SearchPostHome"))
                {
                    mnotifiBtn.setAlpha(0.5F);
                    mhomeBtn.setAlpha(1F);
                    mprofileBtn.setAlpha(0.5F);
                }
                else if (currentFragment.getTag().equals("SingleUser")){
                    mnotifiBtn.setAlpha(0.5F);
                    mhomeBtn.setAlpha(0.5F);
                    mprofileBtn.setAlpha(1F);
                }



            }
        });

        mprofileRlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContent);

                mprofileBtn.setAlpha(1F);
                mnotifiBtn.setAlpha(0.5F);
                mhomeBtn.setAlpha(0.5F);

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

                mtitleimage.setImageResource(R.drawable.profilebackground);
                mprofilebg.setVisibility(VISIBLE);
                mtxtDota.setVisibility(View.INVISIBLE);
                mprofiletxtName.setVisibility(VISIBLE);

                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = SingleUser.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (currentFragment.getTag().equals("Business"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("Business").commit();
                }
                else if (currentFragment.getTag().equals("SearchPostBusiness"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("SearchPostBusiness").commit();
                }
                else if (currentFragment.getTag().equals("SecondHand"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("SecondHand").commit();
                }
                else if (currentFragment.getTag().equals("SearchPostSecondHand"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("SearchPostSecondHand").commit();
                }
                else if (currentFragment.getTag().equals("Rent"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("Rent").commit();
                }
                else if (currentFragment.getTag().equals("SearchPostRent"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("SearchPostRent").commit();
                }
                else if (currentFragment.getTag().equals("Home"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("Home").commit();
                }
                else if (currentFragment.getTag().equals("SearchPostHome"))
                {
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SingleUser").addToBackStack("SearchPostHome").commit();
                }
            }
        });
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            mDrawerLayout.openDrawer((GravityCompat.START));
            return true;
        }

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_toolbar);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        if(menuItem.getItemId()==R.id.nav_logout){
            logout();
        }

        if(menuItem.getItemId()==R.id.nav_settings){
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        }

        if(menuItem.getItemId()==R.id.nav_about){
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
        }

        if(menuItem.getItemId()==R.id.nav_business){
            mhomeBtn.setAlpha(1F);
            mnotifiBtn.setAlpha(0.5F);
            mprofileBtn.setAlpha(0.5F);

            getUserName();
            getUserPic();

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle("商务");
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
            mtitleimage.setImageResource(R.drawable.business_background);
            mprofilebg.setVisibility(View.INVISIBLE);
            mtxtDota.setVisibility(VISIBLE);
            mtxtDota.setText("商务");
            mprofiletxtName.setVisibility(View.INVISIBLE);

            fragmentClass = BusinessFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clear stack
            int count = fragmentManager.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fragmentManager.popBackStack();
            }

            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Business").commit();
        }

        if(menuItem.getItemId()==R.id.nav_secondhand){
            mhomeBtn.setAlpha(1F);
            mnotifiBtn.setAlpha(0.5F);
            mprofileBtn.setAlpha(0.5F);

            getUserName();
            getUserPic();

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle("二手");
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
            mtitleimage.setImageResource(R.drawable.secondhand_background);
            mprofilebg.setVisibility(View.INVISIBLE);
            mtxtDota.setVisibility(VISIBLE);
            mtxtDota.setText("二手");
            mprofiletxtName.setVisibility(View.INVISIBLE);

            fragmentClass = SecondHandFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clear stack
            int count = fragmentManager.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fragmentManager.popBackStack();
            }

            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "SecondHand").commit();
        }

        if(menuItem.getItemId()==R.id.nav_work){
            mhomeBtn.setAlpha(1F);
            mnotifiBtn.setAlpha(0.5F);
            mprofileBtn.setAlpha(0.5F);

            getUserName();
            getUserPic();

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle("招聘");
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
            mtitleimage.setImageResource(R.drawable.desk_wallpaper);
            mprofilebg.setVisibility(View.INVISIBLE);
            mtxtDota.setVisibility(VISIBLE);
            mtxtDota.setText("招聘");
            mprofiletxtName.setVisibility(View.INVISIBLE);

            fragmentClass = HomeActivity.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clear stack
            int count = fragmentManager.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fragmentManager.popBackStack();
            }
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Home").commit();
        }

        if(menuItem.getItemId()==R.id.nav_rent){
            mhomeBtn.setAlpha(1F);
            mnotifiBtn.setAlpha(0.5F);
            mprofileBtn.setAlpha(0.5F);

            getUserName();
            getUserPic();

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle("租房");
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
            mtitleimage.setImageResource(R.drawable.rent_background);
            mprofilebg.setVisibility(View.INVISIBLE);
            mtxtDota.setVisibility(VISIBLE);
            mtxtDota.setText("租房");
            mprofiletxtName.setVisibility(View.INVISIBLE);

            fragmentClass = RentFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Clear stack
            int count = fragmentManager.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fragmentManager.popBackStack();
            }

            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "Rent").commit();
        }

        // Highlight the selected item has been done by Navigat
        // ionView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }


    private void logout(){

        mAuth.signOut();

        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    public String getUserName() {
        return user_name;
    }

    public String getUserPic() {
        return user_image;
    }

    public Fragment currentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.flContent);
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

}