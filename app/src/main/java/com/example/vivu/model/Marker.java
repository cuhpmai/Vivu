package com.example.vivu.model;

import com.google.android.gms.maps.model.LatLng;

public class Marker {
    private int mID;
    private double mLat;
    private double mLng;
    private String mInfo;
    private int mLiked;

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLng() {
        return mLng;
    }

    public void setmLng(double mLng) {
        this.mLng = mLng;
    }

    public String getmInfo() {
        return mInfo;
    }

    public void setmInfo(String mInfo) {
        this.mInfo = mInfo;
    }

    public int getmLiked() {
        return mLiked;
    }

    public void setmLiked(int mLiked) {
        this.mLiked = mLiked;
    }

    public Marker() {
    }

    public Marker(double mLat, double mLng, String mInfo, int mLiked) {
        this.mLat = mLat;
        this.mLng = mLng;
        this.mInfo = mInfo;
        this.mLiked = mLiked;
    }

    public Marker(int mID, double mLat, double mLng, String mInfo, int mLiked) {
        this.mID = mID;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mInfo = mInfo;
        this.mLiked = mLiked;
    }
}