package com.awesome.zhen.mylanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by zhen on 3/29/2017.
 */

public class Settings extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.Rlay, new SettingsScreen()).commit();
    }

    public static class SettingsScreen extends PreferenceFragment{

        private FirebaseAuth mAuth;
        private DatabaseReference mUserInfo;
        private String current_userid;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);
            mAuth = FirebaseAuth.getInstance();
            current_userid = mAuth.getCurrentUser().getUid();
            final String token = FirebaseInstanceId.getInstance().getToken();
            mUserInfo =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
            mUserInfo.keepSynced(true);
            SwitchPreference memaillike = (SwitchPreference) findPreference("emaillike");

            SwitchPreference memailcomment = (SwitchPreference) findPreference("emailcomment");

            SwitchPreference mpushlike = (SwitchPreference) findPreference("pushlike");

            final SwitchPreference mpushcomment = (SwitchPreference) findPreference("pushcomment");

            Preference mchgpw = (Preference) findPreference("chgpassword");

            mUserInfo.child(current_userid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("notificationTokens").hasChild(token)){
                        mpushcomment.setChecked(true);
                    }
                    else{
                        mpushcomment.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            if (mpushcomment != null) {
                mpushcomment.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference arg0, Object newValue) {
                        boolean isNotificationOn = (Boolean) newValue;
                        if (isNotificationOn) {
                            String token = FirebaseInstanceId.getInstance().getToken();
                            current_userid = mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mUserInfo.child(current_userid);
                            current_user_db.child("notificationTokens").child(token).setValue(true);
                            //Toast.makeText(MainActivity.this, "Current token ["+token+"]", Toast.LENGTH_LONG).show();
                            //Log.d("App", "Token ["+token+"]");
                        }
                        else{
                            final String token = FirebaseInstanceId.getInstance().getToken();
                            current_userid = mAuth.getCurrentUser().getUid();
                            mUserInfo.child(current_userid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("notificationTokens").hasChild(token)){
                                        mUserInfo.child(current_userid).child("notificationTokens").child(token).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        return true;
                    }
                });
            }

            mchgpw.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //code for what you want it to do
                    Intent intent = new Intent(getActivity(), ChangePassword.class);
                    startActivity(intent);
                    return true;
                }
            });

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
