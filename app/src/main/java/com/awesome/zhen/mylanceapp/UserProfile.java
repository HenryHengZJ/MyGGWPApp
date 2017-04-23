package com.awesome.zhen.mylanceapp;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends Fragment {

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUserInfo;
    private DatabaseReference mDatabaseCurrentUser;

    private String userkey;

    private EditText meditName, meditTelephone, meditEmail, meditPlace, meditGender;

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_user_profile, container, false);
        context = getActivity();

        meditName = (EditText) rootView.findViewById(R.id.editName);
        meditTelephone = (EditText) rootView.findViewById(R.id.editTelephone);
        meditEmail = (EditText) rootView.findViewById(R.id.editEmail);
        meditPlace = (EditText) rootView.findViewById(R.id.editPlace);
        meditGender = (EditText) rootView.findViewById(R.id.editGender);

        mAuth = FirebaseAuth.getInstance();

        OtherUser activity = (OtherUser) getActivity();
        userkey = activity.getUserKey();

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mDatabaseCurrentUser.keepSynced(true);

        mDatabaseUserInfo = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
        mDatabaseUserInfo.keepSynced(true);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabaseCurrentUser.child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")){
                    String data = dataSnapshot.child("name").getValue(String.class);
                    meditName.setText(data);
                }
                if (dataSnapshot.hasChild("email")){
                    String data = dataSnapshot.child("email").getValue(String.class);
                    meditEmail.setText(data);
                }
                else{

                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        mDatabaseUserInfo.child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("telephone")){
                    String data = dataSnapshot.child("telephone").getValue(String.class);
                    meditTelephone.setText(data);
                }
                if (dataSnapshot.hasChild("location")){
                    String data = dataSnapshot.child("location").getValue(String.class);
                    meditPlace.setText(data);
                }
                if (dataSnapshot.hasChild("gender")){
                    String data = dataSnapshot.child("gender").getValue(String.class);
                    meditGender.setText(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



