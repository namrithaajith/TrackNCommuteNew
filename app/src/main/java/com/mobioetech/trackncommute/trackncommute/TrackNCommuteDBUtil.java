package com.mobioetech.trackncommute.trackncommute;

import com.google.firebase.database.FirebaseDatabase;


public class TrackNCommuteDBUtil {

    private static FirebaseDatabase VehiclesDBInstance ;

    public static FirebaseDatabase getInstance()
    {
        if(VehiclesDBInstance == null)
        {
            VehiclesDBInstance = FirebaseDatabase.getInstance();
            VehiclesDBInstance.setPersistenceEnabled(true);
        }
        return VehiclesDBInstance;
    }

    private TrackNCommuteDBUtil() {
    }
}
