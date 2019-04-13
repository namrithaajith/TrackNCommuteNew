package com.mobioetech.trackncommute.trackncommute;

public class Ride {
    private String rideid;
    private double sourcelatitude;
    private double sourcelongitude;
    private double destinationlatitude;
    private double destinationlongitude;
    private String userid;
    private String driverid;

    public String getRide_status() {
        return ride_status;
    }

    public void setRide_status(String ride_status) {
        this.ride_status = ride_status;
    }

    private String ride_status;

    public Ride() {
    }

    public String getRideid() {
        return rideid;
    }

    public void setRideid(String rideid) {
        this.rideid = rideid;
    }

    public double getSourcelatitude() {
        return sourcelatitude;
    }

    public void setSourcelatitude(double sourcelatitude) {
        this.sourcelatitude = sourcelatitude;
    }

    public double getSourcelongitude() {
        return sourcelongitude;
    }

    public void setSourcelongitude(double sourcelongitude) {
        this.sourcelongitude = sourcelongitude;
    }

    public double getDestinationlatitude() {
        return destinationlatitude;
    }

    public void setDestinationlatitude(double destinationlatitude) {
        this.destinationlatitude = destinationlatitude;
    }

    public double getDestinationlongitude() {
        return destinationlongitude;
    }

    public void setDestinationlongitude(double destinationlongitude) {
        this.destinationlongitude = destinationlongitude;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }
}
