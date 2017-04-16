package com.awesome.zhen.mylanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity {
    private EditText mnewPW, moldPW, mconfirmnewPW;
    private Button mchgpasswordBtn;
    private DatabaseReference mUsername;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mnewPW = (EditText) findViewById(R.id.newPW);
        moldPW = (EditText) findViewById(R.id.oldPW);
        mconfirmnewPW = (EditText) findViewById(R.id.confirmnewPW);

        mchgpasswordBtn = (Button) findViewById(R.id.chgpasswordBtn);

        mUsername = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mchgpasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatepassword();
            }
        });

    }

    private void updatepassword(){


            final String newPW = mnewPW.getText().toString().trim();
            final String oldPW = moldPW.getText().toString().trim();
            final String confirmnewPW = mconfirmnewPW.getText().toString().trim();

        if(!TextUtils.isEmpty(newPW)&&!TextUtils.isEmpty(oldPW)&&!TextUtils.isEmpty(confirmnewPW)){
            mUsername.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("password")){
                        String oldpassword = dataSnapshot.child("password").getValue().toString();
                        if(oldpassword.equals(oldPW)){
                            if(newPW.equals(confirmnewPW)){
                                mUsername.child(mAuth.getCurrentUser().getUid()).child("password").setValue(newPW);
                                Toast.makeText(getApplicationContext(),"Password succesfully changed! ", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Confirmation Password does not match New Password. ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Old Password is incorrect. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Google or Facebook Login Users have no passwords. ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"Please make sure passwords are entered. ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
