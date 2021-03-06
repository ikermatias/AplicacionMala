package com.angular.gerardosuarez.carpoolingapp.mvp.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PassengerBooking {
    public String address;
    public double latitude;
    public double longitude;
    public String status;

    private String name;
    private String phone;
    private String email;
    private String photoUri;
    private String key;
    private String hourPass;
    private String DatePass;

    public PassengerBooking() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUserAttributes(User user) {
        this.phone = user.phone;
        this.email = user.email;
        this.name = user.name;
        this.photoUri = user.photo_uri;
    }

    public String getHourPass() {
        return hourPass;
    }

    public void setHourPass(String hourPass) {
        this.hourPass = hourPass;
    }

    public String getDatePass() {
        return DatePass;
    }

    public void setDatePass(String datePass) {
        DatePass = datePass;
    }
}
