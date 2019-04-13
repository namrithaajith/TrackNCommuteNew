package com.mobioetech.trackncommute.trackncommute;

/**
 * Created by ajithkp on 13/03/18.
 */

public class LiveLocation {
    private String time;
    private double latitude;
    private double longitude;

    public LiveLocation() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
