package com.awesome.zhen.mylanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class Login extends AppCompatActivity {

    private EditText mEmailField, mPasswordField;
    private Button mEmailloginBtn, mRegisterBtn;
    private LoginButton mFbloginBtn;
    private SignInButton mGoogleBtn;
    private CircleImageView mprofileImage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mcallbackManager;

    // variable to track event time
    private long mLastClickTime = 0;

    private ProgressDialog mProgress;
    private ProgressDialog authProgress;

    public User user;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = Login.class.getName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(this);
        authProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");

        mEmailloginBtn = (Button) findViewById(R.id.emailoginBtn);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mFbloginBtn = (LoginButton) findViewById(R.id.fbloginBtn);
        mGoogleBtn = (SignInButton) findViewById(R.id.googleloginBtn);

        mcallbackManager = CallbackManager.Factory.create();
        mFbloginBtn.setReadPermissions("email", "public_profile");

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                authProgress.setMessage("Authenticating");
                authProgress.show();
                signIn();
            }
        });


        mFbloginBtn.registerCallback(mcallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });


        mEmailloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signupIntent = new Intent(Login.this, LoginwithEmail.class);
                signupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupIntent);
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerIntent = new Intent(Login.this, RegisterActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        FirebaseUser mUser= mAuth.getCurrentUser();

        if (mUser != null) {
            // User is signed in
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            String uid = mAuth.getCurrentUser().getUid();
            intent.putExtra("user_id", uid);

            /*if(image!=null || image!=""){
                 intent.putExtra("profile_picture",image);*/

            startActivity(intent);
            finish();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // log in
                    //Intent mainIntent = new Intent(Login.this, MainActivity.class);
                    //String email = user.getEmail();
                    //String name = user.getDisplayName();
                    //mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //startActivity(mainIntent);
                    //finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {


                }

            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Login gError", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            //String image="https://graph.facebook.com/" + task.getResult().getUser().getUid() + "/picture?type=large";
                            String image=task.getResult().getUser().getPhotoUrl().toString();

                            User user = new User(uid,name,email,null);

                            mDatabase.child(uid).setValue(user);
                            mDatabase.child(uid).child("image").setValue(image);

                            // Go to MainActivity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("user_id",uid);
                            //intent.putExtra("profile_picture",image);
                            startActivity(intent);
                            finish();
                        }
                        mProgress.dismiss();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mcallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            authProgress.dismiss();

            mProgress.setMessage("Signing In");
            mProgress.show();

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(), "Sign in Error", Toast.LENGTH_LONG).show();
                mProgress.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        mProgress.setMessage("Signing In");
        mProgress.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login xError", Toast.LENGTH_LONG).show();
                } else {
                    /*String uid=task.getResult().getUser().getUid();
                    String name=task.getResult().getUser().getDisplayName();
                    DatabaseReference current_user_db = mDatabase.child(uid);
                    current_user_db.child("name").setValue(name);
                    current_user_db.child("image").setValue("default");*/

                    String uid=task.getResult().getUser().getUid();
                    String name=task.getResult().getUser().getDisplayName();
                    String email=task.getResult().getUser().getEmail();
                    //String image="https://graph.facebook.com/" + task.getResult().getUser().getUid() + "/picture?type=large";
                    String image=task.getResult().getUser().getPhotoUrl().toString();


                    User user = new User(uid,name,email,null);

                    mDatabase.child(uid).setValue(user);
                    mDatabase.child(uid).child("image").setValue(image);

                    // Go to MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user_id",uid);
                    //intent.putExtra("profile_picture",image);
                    startActivity(intent);
                    finish();

                }
                mProgress.dismiss();
            }
        });
    }
}






        /*  final DatabaseReference mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://my-gg-app.firebaseio.com/Users");
        mListView = (ListView)findViewById(R.id.listView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mUsername);
        mListView.setAdapter(arrayAdapter);
        mRootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getValue(String.class);
                mUsername.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendData.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View view) {
               String value = mValueField.getText().toString();
               String key = mkeyValue.getText().toString();
               DatabaseReference mRefChild = mRootRef.child(key);
               mRefChild.setValue(value);
            }
           });

        getData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                mgetRootRef.addValueEventListener(new ValueEventListener() {
                   @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        mValueView.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        */
