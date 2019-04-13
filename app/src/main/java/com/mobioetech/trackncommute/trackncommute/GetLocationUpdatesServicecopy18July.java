package com.mobioetech.trackncommute.trackncommute;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetLocationUpdatesServicecopy18July extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FirebaseDatabase database = null;
    private DatabaseReference ref_loctrack;
    private FirebaseAuth auth;
    private static final String LOG = "locationupdateservice";
    private String current_user;
    private String formattedDate;

    public GetLocationUpdatesServicecopy18July() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG,"Oncreate of service called");
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //current_user = String.valueOf(auth.getCurrentUser().getDisplayName());
            current_user = auth.getCurrentUser().getPhoneNumber();
            Log.i(LOG,"current_user------>"+current_user);
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy");
        formattedDate = df.format(c);

        database = TrackNCommuteDBUtil.getInstance();
        //ref_loctrack = database.getReference("tracklocation").child(current_user).child(formattedDate).push();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG,"onStartCommand called");
        Log.i(LOG,"mGoogleApiClient ------>"+mGoogleApiClient);
        if (!mGoogleApiClient.isConnected())
            //mGoogleApiClient.connect();
        Log.i(LOG,"mGoogleApiClient after connecting------>"+mGoogleApiClient);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Disconnecting the client invalidates it.
        Log.i(LOG,"On destroy of service called");
        Log.i(LOG,"mGoogleApiClient disconnected"+mGoogleApiClient);
        stopSelf();
        mGoogleApiClient.disconnect();
        Log.i(LOG,"mGoogleApiClient disconnected"+mGoogleApiClient);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG,"OnBind of service called");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOG,"OnUnBind of service called");
        return super.onUnbind(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //ref_loctrack = database.getReference("tracklocation").child(current_user).child(formattedDate).push();
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000); // Update location every  1 min
        mLocationRequest.setSmallestDisplacement(0.25F);

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
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        client.requestLocationUpdates(mLocationRequest,new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                String currenttime = DateFormat.getTimeInstance().format(new Date());
                Log.i(LOG,"Location at time------>" +location.toString()+ " at "+currenttime);
                if (auth.getCurrentUser() != null) {
                    current_user = auth.getCurrentUser().getPhoneNumber();
                }

                else {
                    Toast.makeText(getApplicationContext(),"Login to track ",Toast.LENGTH_SHORT).show();
                    return;
                }
                ref_loctrack = database.getReference("tracklocation").child(current_user).child(formattedDate).push();

                Log.i(LOG, "ref_loctrack------>"+ref_loctrack);
                //txtOutput.setText(location.toString());
                Log.i(LOG,"Current Location ---->"+location.getLatitude());

//                LiveLocation liveLocation = new LiveLocation();
//                liveLocation.setLatitude(location.getLatitude());
//                liveLocation.setLongitude(location.getLongitude());
//                liveLocation.setTime(currenttime);
//                ref_loctrack.setValue(liveLocation);

                ref_loctrack.setValue(location);

            }
        },null);
    }



    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "GoogleApiClient connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG, "GoogleApiClient connection has failed");
    }

}
