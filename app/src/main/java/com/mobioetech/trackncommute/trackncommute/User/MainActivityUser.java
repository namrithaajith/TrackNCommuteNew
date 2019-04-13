package com.mobioetech.trackncommute.trackncommute.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobioetech.trackncommute.trackncommute.CircleTransform;
import com.mobioetech.trackncommute.trackncommute.Driver.OwnerWaitingApproval;
import com.mobioetech.trackncommute.trackncommute.LoginActivity;
import com.mobioetech.trackncommute.trackncommute.Driver.MainActivityOwner;
import com.mobioetech.trackncommute.trackncommute.R;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteConstants;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteDBUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivityUser extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final String LOG = "mainactivityuser";
    private static final int RC_SIGN_IN = 100;
    private static final String UNCHANGED_CONFIG_VALUE = "CHANGE-ME";
    private FirebaseAuth auth;
    private ActionBarDrawerToggle mDrawerToggle;
    private View headerview;
    private TextView loginbtn;
    private ImageView avatarImageView;
    private static final int REQUEST_LOCATION = 0;
    private GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static double currentlatitude;
    public static double currentlongitude;
    private FirebaseDatabase database = null;
    private DatabaseReference ref_registered,ref_verified;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.vehicletypes_rcv)
    RecyclerView vehicletypes_rcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = TrackNCommuteDBUtil.getInstance();
        buildGoogleApiClient();

        ref_registered = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.REGISTEREDOWNERS);
        ref_verified = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.VERIFIEDREGISTEREDOWNERS);

        if (mNavigationView != null) {

            setupDrawerContent(mNavigationView);
            headerview = mNavigationView.getHeaderView(0);
            loginbtn = (TextView) headerview.findViewById(R.id.login_text);
            avatarImageView = (ImageView) headerview.findViewById(R.id.avatar);
            Picasso.get()
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .transform(new CircleTransform())
                    .into(avatarImageView);
            Log.i(LOG, "User logged in....--->" + auth.getCurrentUser().getUid());
            //loginbtn.setText("Welcome " + auth.getCurrentUser().getDisplayName());
            loginbtn.setText("Welcome " + auth.getCurrentUser().getPhoneNumber());

            vehicletypes_rcv.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
            vehicletypes_rcv.setLayoutManager(layoutManager);

            ArrayList vehicleTypes = prepareData();
            VehicletypesAdapterUser adapter = new VehicletypesAdapterUser(getApplicationContext(),vehicleTypes);
            vehicletypes_rcv.setAdapter(adapter);

        }
    }


    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mDrawerLayout, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private ArrayList prepareData() {
        ArrayList vehicle_category = new ArrayList<>();
        String[] categoriesArray = getResources().getStringArray(R.array.vehicletypes);
        String[] urlsArray = getResources().getStringArray(R.array.vehicles_images_url);
        for(int i=0;i<categoriesArray.length;i++)
        {
            VehicleCategories tayCategory = new VehicleCategories();
            tayCategory.setVehicle_category(categoriesArray[i]);
            tayCategory.setVehicle_category_url(urlsArray[i]);
            Log.i(LOG,"Category---->"+categoriesArray[i]);

            vehicle_category.add(tayCategory);
        }
        return vehicle_category;
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                mDrawerLayout.closeDrawer(mNavigationView);
                                break;
                            case R.id.nav_regOwner:

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
                                                        Intent intent = new Intent(MainActivityUser.this, MainActivityOwner.class);
                                                        startActivity(intent);
                                                    }
                                                    else{
                                                        Log.i(LOG,"owner logged in ,registered,waiting for approval");
                                                        Intent intent = new Intent(MainActivityUser.this, OwnerWaitingApproval.class);
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
                                            Intent intent = new Intent(MainActivityUser.this, OwnerRegistrationActivity.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
//                                Log.i(LOG, "New owner registeration");
//                                Intent intent = new Intent(MainActivityUser.this, OwnerRegistrationActivity.class);
//                                startActivity(intent);
                                break;
                            case R.id.nav_profile:
                                //startActivity(AdminMainActivity.createIntent(getApplicationContext()));
                                break;
                            case R.id.nav_settings:
                                break;
                            case R.id.nav_logout:
                                AuthUI.getInstance()
                                        .signOut(MainActivityUser.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(MainActivityUser.this,LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.i(LOG, getResources().getString(R.string.sign_out_failed));
                                                }
                                            }
                                        });


                                break;
                            case R.id.nav_delete:
                                if (auth.getCurrentUser() != null) {
                                    AlertDialog dialog = new AlertDialog.Builder(MainActivityUser.this)
                                            .setMessage("Are you sure you want to delete this account?")
                                            .setPositiveButton("Yes, nuke it!", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    deleteAccount();
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .create();

                                    dialog.show();
                                }


                                break;

                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                System.out.println("Inside on drawer opened");
                //getSupportActionBar().setTitle(getString(R.string.drawer_opened));
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                System.out.println("Inside on drawer closed");

                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void deleteAccount() {
        FirebaseAuth.getInstance()
                .getCurrentUser()
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivityUser.this , LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.i(LOG, getResources().getString(R.string.delete_account_failed));
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
            mDrawerLayout.closeDrawer(mNavigationView);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);

            } else {
                mDrawerLayout.openDrawer(mNavigationView);
            }
            return true;
        }

        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission))
                        .setMessage(getString(R.string.permissions_not_granted_location_access))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //re-request
                                ActivityCompat.requestPermissions(MainActivityUser.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }


        } else {
            getCurrentLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();

            } else {
                requestAlert(getString(R.string.permissions_not_granted_location_access));
            }
        }
    }

    private void requestAlert(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.permission))
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivityUser.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            currentlatitude = mLastLocation.getLatitude();
            currentlongitude = mLastLocation.getLongitude();

            Log.i(LOG, "currentlatitude---currentlongitude--" + currentlatitude + "," + currentlongitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG," google API Client Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG," google API Client Connection failed");
    }

}
