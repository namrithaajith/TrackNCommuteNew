package com.mobioetech.trackncommute.trackncommute.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobioetech.trackncommute.trackncommute.R;
import com.mobioetech.trackncommute.trackncommute.Ride;
import com.mobioetech.trackncommute.trackncommute.RideAcceptedActivity;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteConstants;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteDBUtil;

public class RideAwaitingAcceptance extends AppCompatActivity {
    private static final String LOG = "rideawaitngacceptance";
    private FirebaseDatabase database = null;
    private DatabaseReference ref_userrequestedride;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_awaiting_acceptance);
        database = TrackNCommuteDBUtil.getInstance();
        }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i(LOG,"Inside onpost resume");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG,"Inside  onstart");
        ref_userrequestedride = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.USERRIDES).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(TrackNCommuteConstants.USERREQUESTEDRIDE);
        Log.i(LOG,"ref_userrequestedride----->"+ref_userrequestedride);

        ref_userrequestedride.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String rideStatus = snapshot.getValue(Ride.class).getRide_status();
                    Log.i(LOG,"rideStatus----->"+rideStatus);
                    if (rideStatus.equals("accepted")){
                        Intent intent = new Intent(RideAwaitingAcceptance.this, RideAcceptedActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        ref_userrequestedride.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                //setMarker(dataSnapshot);
//                Log.i(LOG,"On child added called");
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                //setMarker(dataSnapshot);
//                Log.i(LOG,"On child changed called");
//                Intent intent = new Intent(RideAwaitingAcceptance.this, RideAcceptedActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                Log.i(LOG,"On child removed called");
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.i(LOG,"On child moved called");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(LOG, "Failed to read value.", databaseError.toException());
//            }
//        });
    }
}
