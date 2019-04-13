package com.mobioetech.trackncommute.trackncommute;

public class RegisteredOwner {

    private String userid;
    private String name;
    private String phoneno;
    private String photo_url;
    private String license_url;
    private String vehicle_type;
    private String vehicle_rc_url;
    private Boolean gotApproval;

    public RegisteredOwner() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getLicense_url() {
        return license_url;
    }

    public void setLicense_url(String license_url) {
        this.license_url = license_url;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getVehicle_rc_url() {
        return vehicle_rc_url;
    }

    public void setVehicle_rc_url(String vehicle_rc_url) {
        this.vehicle_rc_url = vehicle_rc_url;
    }

    public Boolean getGotApproval() {
        return gotApproval;
    }

    public void setGotApproval(Boolean gotApproval) {
        this.gotApproval = gotApproval;
    }
}
