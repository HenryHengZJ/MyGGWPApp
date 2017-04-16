package com.awesome.zhen.mylanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginwithEmail extends AppCompatActivity {

    private EditText mEmailField, mPasswordField;
    private Button memailsigninBtn;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // variable to track event time
    private long mLastClickTime = 0;

    private ProgressDialog mProgress;
    private ProgressDialog mAuthProgress;

    public User user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginwith_email);

        mProgress = new ProgressDialog(this);
        mAuthProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);

        memailsigninBtn = (Button) findViewById(R.id.emailoginBtn);

        memailsigninBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                checkLogin();
            }
        });

    }


    private void checkLogin(){
        String email = mEmailField.getText().toString().trim();
        String password = mPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(password)) {
            mProgress.setMessage("Logging In");
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkUserExist();
                        mProgress.dismiss();
                    }
                    else {
                        Toast.makeText(LoginwithEmail.this, "Login Failed", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }

            });
        }else {

            Toast.makeText(LoginwithEmail.this, "Check Your Email & Password", Toast.LENGTH_LONG).show();

        }

    }

    private void checkUserExist(){

        if(mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user_id)) {
                        setUpUser();
                        Intent mainIntent = new Intent(LoginwithEmail.this, Login.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();

                    } else {
                        Toast.makeText(LoginwithEmail.this, "You may need an account", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    protected void setUpUser() {
        user = new User();
        user.setEmail(mEmailField.getText().toString());
        user.setPassword(mPasswordField.getText().toString());
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
