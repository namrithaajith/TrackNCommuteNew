package com.mobioetech.trackncommute.trackncommute;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobioetech.trackncommute.trackncommute.Driver.MainActivityOwner;
import com.mobioetech.trackncommute.trackncommute.User.MainActivityUser;
import com.mobioetech.trackncommute.trackncommute.Driver.OwnerWaitingApproval;

import butterknife.BindView;

public class SplashActivity extends AppCompatActivity {

    private static final String LOG = "applogoactivity";
    private FirebaseDatabase database = null;
    private DatabaseReference ref_registered,ref_verified;
    private FirebaseAuth auth;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    ConstraintLayout mConstraintlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final int SPLASH_TIME_OUT = 3000;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        database = TrackNCommuteDBUtil.getInstance();
        auth = FirebaseAuth.getInstance();
        //Log.i(LOG,"current user....>"+auth.getCurrentUser().getPhoneNumber());
        ref_registered = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.REGISTEREDOWNERS);
        ref_verified = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.VERIFIEDREGISTEREDOWNERS);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launchActivity();
                finish();
            }
        }, SPLASH_TIME_OUT);


    }

    private void launchActivity() {

        if(auth.getCurrentUser() != null){
            Log.i(LOG,"auth.getCurrentUser(------->"+auth.getCurrentUser().getPhoneNumber());
            ref_registered.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(auth.getCurrentUser().getPhoneNumber())){
                        //if owner is registered
                        ref_verified.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(auth.getCurrentUser().getPhoneNumber())){
                                    Log.i(LOG,"owner logged in ,registered,verified");
                                    //Intent intent = new Intent(SplashActivity.this, MainActivityOwner.class);
                                    Intent intent = new Intent(SplashActivity.this, MainActivityOwner.class);
                                    startActivity(intent);
                                }
                                else{
                                    Log.i(LOG,"owner logged in ,registered,waiting for approval");
                                    Intent intent = new Intent(SplashActivity.this, OwnerWaitingApproval.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                    else{
                        Log.i(LOG,"owner logged in , not registered");
                        Intent intent = new Intent(SplashActivity.this, MainActivityUser.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else{
            Log.i(LOG,"owner not logged in");
            Intent intent = new Intent(SplashActivity.this ,LoginActivity.class);
            startActivity(intent);
        }

    }
}
