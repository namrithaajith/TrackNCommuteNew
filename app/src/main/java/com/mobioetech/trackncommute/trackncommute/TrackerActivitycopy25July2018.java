package com.mobioetech.trackncommute.trackncommute;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobioetech.trackncommute.trackncommute.Driver.GetLocationUpdatesService;
import com.mobioetech.trackncommute.trackncommute.Driver.MainActivityOwner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TrackerActivitycopy25July2018 extends AppCompatActivity implements OnMapReadyCallback{

    private static final int REQUEST_LOCATION = 0;
    private static final String LOG = "trackeractivity";
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    FirebaseDatabase database;
    private DatabaseReference ref_track,ref_geofire;
    GeoFire geoFire;
    private FirebaseAuth auth;
    private String current_user;
    Marker marker;
    private ArrayList<LatLng> points;
    Polyline line;
    Intent intent = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            current_user = auth.getCurrentUser().getPhoneNumber();
        }
        intent = new Intent(this , GetLocationUpdatesService.class);
        database = TrackNCommuteDBUtil.getInstance();
        ref_geofire = database.getReference(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.GEOFIRE);
        geoFire = new GeoFire(ref_geofire);


    }
    @Override
    protected void onStart() {
        geoFire.setLocation(current_user, new GeoLocation(MainActivityOwner.currentlatitude, MainActivityOwner.currentlongitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(LOG,"Onstop of trckeractivity called");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG,"Ondestroy of trackeractivity called");
        super.onDestroy();
    }

    public void startLocationUpdates(View view) {


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission)
                        .setMessage(R.string.permissions_not_granted_location_access)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TrackerActivitycopy25July2018.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }

        }
        else{
//            Intent intent = new Intent(this, GetLocationUpdatesService.class);
//            startService(intent);
//            finish();
            startTrackerService();
        }
    }
    private void startTrackerService(){
        //intent = new Intent(this, GetLocationUpdatesService.class);
        Log.i(LOG,"current_user-----"+current_user);
        geoFire.removeLocation(current_user, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error removing the location from GeoFire: " + error);
                } else {
                    System.out.println("Location removed from server successfully!");
                }
            }
        });
        startService(intent);
        //finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
//                Intent intent = new Intent(this, GetLocationUpdatesService.class);
//                startService(intent);
                startTrackerService();

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
                        ActivityCompat.requestPermissions(TrackerActivitycopy25July2018.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION);

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void stopLocationUpdates(View view) {
        Log.i(LOG,"Stop service called");
        //intent = new Intent(this, GetLocationUpdatesService.class);
        stopService(intent);
        Log.i(LOG,"Inside trackeractivity currentlocation_owner"+GetLocationUpdatesService.currentlocation_owner.getLatitude());
        //geoFire.setLocation("current_user", new GeoLocation(GetLocationUpdatesService.currentlocation_owner.getLatitude(), -GetLocationUpdatesService.currentlocation_owner.getLongitude()));
        geoFire.setLocation(current_user, new GeoLocation(GetLocationUpdatesService.currentlocation_owner.getLatitude(), GetLocationUpdatesService.currentlocation_owner.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }

        });

        //finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy");
        String formattedDate = df.format(c);
        if (auth.getCurrentUser() != null) {
            current_user = auth.getCurrentUser().getPhoneNumber();
        }

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        points = new ArrayList<LatLng>();

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
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        ref_track = TrackNCommuteDBUtil.getInstance().getReference("tracklocation").child(current_user).child(formattedDate);
        ref_track.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once

//        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//            String key = snapshot.getKey();
//            HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();
//            double lat = Double.parseDouble(value.get("latitude").toString());
//            double lng = Double.parseDouble(value.get("longitude").toString());
//            LatLng location = new LatLng(lat, lng);
//            if (!mMarkers.containsKey(key)) {
//                mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
//            } else {
//                mMarkers.get(key).setPosition(location);
//            }
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            for (Marker marker : mMarkers.values()) {
//                builder.include(marker.getPosition());
//            }
//            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
//        }


        if(marker!=null){
            marker.remove();
        }

        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);

        points.add(location);

        //mMap.clear();
        drawpath();

        marker = mMap.addMarker(new MarkerOptions().title(key).position(location));

        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, marker);
        } else {
            mMarkers.get(key).setPosition(location);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,24));
    }

    private void drawpath() {

        //googleMap.clear();  //clears all Markers and Polylines

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
            options.add(point);
        }
        //addMarker(); //add Marker in current position

        line = mMap.addPolyline(options); //add Polyline
    }
}
