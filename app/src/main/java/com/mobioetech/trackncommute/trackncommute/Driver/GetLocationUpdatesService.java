package com.mobioetech.trackncommute.trackncommute.Driver;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteDBUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetLocationUpdatesService extends Service {

    private LocationRequest mLocationRequest;
    private FirebaseDatabase database = null;
    private DatabaseReference ref_loctrack,ref_geofire;
    GeoFire geoFire;
    private FirebaseAuth auth;
    private static final String LOG = "locationupdateservice";
    public static Location currentlocation_owner;
    private String current_user;
    private String formattedDate;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    //int x = 267;

    public GetLocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG, "Oncreate of service called");
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //current_user = String.valueOf(auth.getCurrentUser().getDisplayName());
            current_user = auth.getCurrentUser().getPhoneNumber();
            Log.i(LOG, "current_user------>" + current_user);
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy");
        formattedDate = df.format(c);

        database = TrackNCommuteDBUtil.getInstance();
        client = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(60000); // Update location every  1 min
        mLocationRequest.setSmallestDisplacement(0.25F);


        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentlocation_owner = locationResult.getLastLocation();
                String currenttime = DateFormat.getTimeInstance().format(new Date());
                Log.i(LOG, "Location at time------>" + currentlocation_owner.toString() + " at " + currenttime);
//                if (auth.getCurrentUser() != null) {
//                    current_user = auth.getCurrentUser().getPhoneNumber();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Login to track ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                ref_loctrack = database.getReference("tracklocation").child(current_user).child(formattedDate).push();

                Log.i(LOG, "ref_loctrack------>" + ref_loctrack);
                //txtOutput.setText(location.toString());
                Log.i(LOG, "Current Location ---->" + currentlocation_owner.getLatitude());

//                LiveLocation liveLocation = new LiveLocation();
//                liveLocation.setLatitude(location.getLatitude());
//                liveLocation.setLongitude(location.getLongitude());
//                liveLocation.setTime(currenttime);
//                ref_loctrack.setValue(liveLocation);

                ref_loctrack.setValue(currentlocation_owner);

                //adding the following code to populate geofire

//                ref_geofire = database.getReference(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.GEOFIRE);
//                geoFire = new GeoFire(ref_geofire);
//
//                geoFire.setLocation(current_user+x, new GeoLocation(currentlocation_owner.getLatitude(), currentlocation_owner.getLongitude()), new GeoFire.CompletionListener() {
//                    @Override
//                    public void onComplete(String key, DatabaseError error) {
//                        if (error != null) {
//                            System.err.println("There was an error saving the location to GeoFire: " + error);
//                        } else {
//                            System.out.println("Location saved on server successfully!");
//                        }
//                    }
//
//                });
//                x++;
                //code ends

            }


        };

        client.requestLocationUpdates(mLocationRequest, mLocationCallback,null);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG, "onStartCommand called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Disconnecting the client invalidates it.
        Log.i(LOG,"On destroy of service called");

        //stopSelf();
        client.removeLocationUpdates(mLocationCallback);

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


}
