package com.mobioetech.trackncommute.trackncommute.User;

import android.animation.ArgbEvaluator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobioetech.trackncommute.trackncommute.R;
import com.mobioetech.trackncommute.trackncommute.RegisteredOwner;
import com.mobioetech.trackncommute.trackncommute.Ride;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteConstants;
import com.mobioetech.trackncommute.trackncommute.TrackNCommuteDBUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.key;
import static com.mobioetech.trackncommute.trackncommute.User.MainActivityUser.currentlatitude;
import static com.mobioetech.trackncommute.trackncommute.User.MainActivityUser.currentlongitude;
import static com.mobioetech.trackncommute.trackncommute.User.MainActivityUser.mLastLocation;


public class NearbyAutosMapActivity extends FragmentActivity
        implements GeoQueryEventListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String LOG = "nearbyautosMapActivity";
    private static final int INITIAL_ZOOM_LEVEL = 14;
    private FirebaseDatabase database = null;
    private DatabaseReference geofireref, ref_driver,ref_driverreceivedrequest,ref_driverongoingride,ref_userrequesterride,ref_userongoingride;
    private GoogleMap map;
    private GeoFire geoFire;
    private GeoQuery geoQuery;


    private Map<String, Marker> markers;


    @BindView(R.id.rootFrame)
    FrameLayout rootFrame;

    @BindView(R.id.rootll)
    LinearLayout rootll;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    ArgbEvaluator argbEvaluator;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_autos_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        ButterKnife.bind(this);
        mapFragment.getMapAsync(this);

        argbEvaluator = new ArgbEvaluator();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int devHeight = displayMetrics.heightPixels;
        int devWidth = displayMetrics.widthPixels;

        setUpPagerAdapter();
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(-devWidth / 2);


    }
    private void setUpPagerAdapter() {

        //List<Integer> data = Arrays.asList(0, 1);
        List<Integer> data = Arrays.asList(0);
        NearbyDetailInfoPagerAdapter adapter = new NearbyDetailInfoPagerAdapter(data);
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
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
            map.setMyLocationEnabled(false);
            map.clear();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        map.setMyLocationEnabled(true);
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

        map.setOnMarkerClickListener(this);


        if(mLastLocation != null)
        {
            GeoLocation INITIAL_CENTER = new GeoLocation(currentlatitude, currentlongitude);

            database = TrackNCommuteDBUtil.getInstance();

            geofireref = database.getReference(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.GEOFIRE);

            this.geoFire = new GeoFire(geofireref);

            this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, 30);//The radius has to be decreased

            this.geoQuery.addGeoQueryEventListener(this);

            this.markers = new HashMap<String, Marker>();

            //this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentlatitude, currentlongitude), INITIAL_ZOOM_LEVEL));
            /*this.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                    .target(new LatLng(currentlatitude, currentlongitude))
                    .zoom(INITIAL_ZOOM_LEVEL)
                    .bearing(0)
                    .tilt(45)
                    .build()), 10000, null);*/
        }
    }



    @Override
    protected void onStart() {
        if(geoQuery != null){
            this.geoQuery.addGeoQueryEventListener(this);
        }
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(geoQuery != null) {
            this.geoQuery.removeAllListeners();
        }
        for (Marker marker: this.markers.values()) {
            this.markers.remove(key);
            marker.remove();


        }
        this.markers.clear();

    }

    @Override
    public void onKeyEntered(final String key, final GeoLocation location) {

        final Location autoLoc = new Location("autoLocation");
        autoLoc.setLatitude(location.latitude);
        autoLoc.setLongitude(location.longitude);
        LatLng latLngCenter = new LatLng(autoLoc.getLatitude(), autoLoc.getLongitude());

        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, INITIAL_ZOOM_LEVEL));
        /*this.map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder()
                .target(latLngCenter)
                .zoom(INITIAL_ZOOM_LEVEL)
                .bearing(0)
                .tilt(45)
                .build()), 10000, null);*/

        //final Marker marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icondrive )));
        final Marker marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude)));

        marker.setTag(key);

        this.markers.put(key, marker);


    }

    @Override
    public void onKeyExited(String key) {

        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }

    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Marker marker = this.markers.get(key);

        Log.i(LOG,"On key moved called......");

        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);

        }

    }
    @Override
    public void onGeoQueryError(DatabaseError error) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onGeoQueryReady() {

    }


    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed/DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        final String markerkey = (String) marker.getTag();
//
//        final String[] jewellery_name = new String[1];
        final String[] name = new String[1];
//
        ref_driver = database.getReference(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.VERIFIEDREGISTEREDOWNERS).child(markerkey);


        ref_driver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RegisteredOwner registeredOwner = dataSnapshot.getValue(RegisteredOwner.class);
                if(registeredOwner != null) {
                    name[0] = dataSnapshot.getValue(RegisteredOwner.class).getName();

                    if (name[0] != null)
                        marker.setTitle(name[0]);

                    //TransitionManager.beginDelayedTransition(rootFrame);
                    viewPager.setVisibility(View.VISIBLE);
                    //assert view != null;
                    final ImageView imageView = (ImageView) viewPager
                            .findViewById(R.id.driverimage);


                    TextView tv_drivername = viewPager.findViewById(R.id.tvdrivername);
                    tv_drivername.setText(name[0]);
                    Button btn_bookride = viewPager.findViewById(R.id.btn_bookride);
                    btn_bookride.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ref_driverreceivedrequest = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.DRIVERRIDES).child(markerkey).child(TrackNCommuteConstants.RECIEVEDRIDEREQUEST).push();
                            ref_userrequesterride = database.getReference().child(TrackNCommuteConstants.AUTO).child(TrackNCommuteConstants.USERRIDES).child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child(TrackNCommuteConstants.USERREQUESTEDRIDE).push();
                            Ride ride = new Ride();
                            ride.setRideid(markerkey+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            ride.setDriverid(markerkey);
                            ride.setUserid(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                            ride.setSourcelatitude(MainActivityUser.currentlatitude);
                            ride.setSourcelongitude(MainActivityUser.currentlongitude);
                            ride.setRide_status("requested");
                            ref_driverreceivedrequest.setValue(ride);
                            ref_userrequesterride.setValue(ride);
                            Intent intent = new Intent(NearbyAutosMapActivity.this , RideAwaitingAcceptance.class);
                            startActivity(intent);
                        }
                    });


                    map.setPadding(0, 0, 0, viewPager.getHeight());


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return false;

    }


    @Override
    public void onBackPressed() {

        if (viewPager.getVisibility() == View.VISIBLE) {

            TransitionManager.beginDelayedTransition(rootFrame);
            viewPager.setVisibility(View.INVISIBLE);
            map.setPadding(0, 0, 0, 0);
            return;
        }

        startActivity(new Intent(this, MainActivityUser.class));
        finish();
    }


}
