package com.mobioetech.trackncommute.trackncommute;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobioetech.trackncommute.trackncommute.Driver.MainActivityOwner;
import com.mobioetech.trackncommute.trackncommute.User.MainActivityUser;
import com.mobioetech.trackncommute.trackncommute.Driver.OwnerWaitingApproval;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String LOG = "loginactivity";
    private static final int RC_SIGN_IN = 100;
    private FirebaseDatabase database = null;
    private DatabaseReference ref_registered,ref_verified;
    private FirebaseAuth auth;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinator_layout;

    @BindView(R.id.btn_login)
    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        database = TrackNCommuteDBUtil.getInstance();
        Log.i(LOG,"Log in activity");

        ref_registered = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.REGISTEREDOWNERS);
        ref_verified = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.VERIFIEDREGISTEREDOWNERS);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build());
                startActivityForResult(
                        AuthUI.getInstance().createSignInIntentBuilder()
                                .setTheme(R.style.AppTheme)
                                //.setLogo(R.drawable.ic_launcher)
                                .setAvailableProviders(providers)
                                //.setIsSmartLockEnabled(true)
                                .build(),
                        RC_SIGN_IN);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.i(LOG, "Inside requestCode == RC_SIGN_IN");
            handleSignInResponse(resultCode, data);
            return;
        }

        Log.i(LOG, getResources().getString(R.string.unknown_response));
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Log.i(LOG, "Inside handle sign in response");
        // Successfully signed in
        if (resultCode == RESULT_OK) {
//            Log.i(LOG,"owner phone no----"+auth.getCurrentUser().getPhoneNumber()+" , "+auth.getCurrentUser().getUid());
//            //startActivity(MainActivity.createIntent(MainActivity.this));
//            Intent intent = new Intent(this, OwnerRegistrationActivity.class);
//            startActivity(intent);
//            Log.i(LOG,"Successfully Signed in");
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
                                    Intent intent = new Intent(LoginActivity.this, MainActivityOwner.class);
                                    startActivity(intent);
                                }
                                else{
                                    Log.i(LOG,"owner logged in ,registered,waiting for approval");
                                    Intent intent = new Intent(LoginActivity.this, OwnerWaitingApproval.class);
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
                        Intent intent = new Intent(LoginActivity.this, MainActivityUser.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return;
        } else {
            // Sign in failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                Log.i(LOG, getResources().getString(R.string.sign_in_cancelled));
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                Log.i(LOG, getResources().getString(R.string.no_internet_connection));
                return;
            }

            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                Log.i(LOG, getResources().getString(R.string.unknown_error));
                return;
            }
        }

        //showSnackbar(R.string.unknown_sign_in_response);
    }
    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        //Snackbar.make(mCoordinator_layout, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}
