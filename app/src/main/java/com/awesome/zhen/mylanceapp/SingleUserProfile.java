package com.awesome.zhen.mylanceapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class SingleUserProfile extends Fragment {

    private FirebaseAuth mAuth;

    private DatabaseReference mUsername;
    private DatabaseReference mDatabaseCurrentUser;

    private String currentUserId;

    private EditText meditName, meditTelephone, meditEmail, meditPlace,meditGender;

    Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.singleuser_profile, container, false);
        context = getActivity();

        meditName = (EditText) rootView.findViewById(R.id.editName);
        meditTelephone = (EditText) rootView.findViewById(R.id.editTelephone);
        meditEmail = (EditText) rootView.findViewById(R.id.editEmail);
        meditPlace = (EditText)rootView.findViewById(R.id.editPlace);
        meditGender = (EditText)rootView.findViewById(R.id.editGender);

        meditPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRadioButtonDialogLocation();
            }
        });

        meditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRadioButtonDialogGender();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        mUsername = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("Users");
        mUsername.keepSynced(true);

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReferenceFromUrl("https://my-gg-app.firebaseio.com").child("UsersInfo");
        mDatabaseCurrentUser.keepSynced(true);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                startSaving();
            }
        });

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
        stringList.add("-");
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

        RadioButton rdo29 = (RadioButton) dialog.findViewById(R.id.rdo29);
        rdo29.setVisibility(VISIBLE);
        rdo29.setText(stringList.get(28));

        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Toast.makeText(getApplicationContext(), btn.getText().toString(), Toast.LENGTH_SHORT).show();
                        meditPlace.setText(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void showRadioButtonDialogGender() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gender_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        List<String> stringList=new ArrayList<>();  // here is list
        stringList.add("MALE");
        stringList.add("FEMALE");

        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        RadioButton rdo1 = (RadioButton) dialog.findViewById(R.id.rdo1);
        rdo1.setText(stringList.get(0));
        RadioButton rdo2 = (RadioButton) dialog.findViewById(R.id.rdo2);
        rdo2.setText(stringList.get(1));


        dialog.show();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Toast.makeText(getApplicationContext(), btn.getText().toString(), Toast.LENGTH_SHORT).show();
                        meditGender.setText(btn.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void startSaving() {

        final String name_val = meditName.getText().toString().trim();
        final String email_val = meditEmail.getText().toString().trim();
        final String tel_val = meditTelephone.getText().toString().trim();
        final String location_val = meditPlace.getText().toString().trim();
        final String gender_val = meditGender.getText().toString().trim();


        mUsername.child(currentUserId).child("name").setValue(name_val);
        mUsername.child(currentUserId).child("email").setValue(email_val);
        mDatabaseCurrentUser.child(currentUserId).child("telephone").setValue(tel_val);
        if(location_val.equals("-")){
            mDatabaseCurrentUser.child(currentUserId).child("location").setValue("-");
        }
        else{
            mDatabaseCurrentUser.child(currentUserId).child("location").setValue(location_val);
        }
        mDatabaseCurrentUser.child(currentUserId).child("gender").setValue(gender_val);

        FragmentManager fragmentManager2 = getFragmentManager();
        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
        SingleUserProfile fragment2 = new SingleUserProfile();
        fragmentTransaction2.replace(R.id.PostFragment, fragment2);
        fragmentTransaction2.commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        mDatabaseCurrentUser.child(currentUserId).addValueEventListener(new ValueEventListener() {
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
                else{

                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        mUsername.child(currentUserId).addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



