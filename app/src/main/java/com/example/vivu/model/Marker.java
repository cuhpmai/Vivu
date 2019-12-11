package com.example.vivu.model;

public class Marker {
    private int mID;
    private double mLat;
    private double mLng;
    private String mInfo;
    private String mAddr;

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

    public String getmAddr() {
        return mAddr;
    }

    public void setmAddr(String mAddr) {
        this.mAddr = mAddr;
    }

    public Marker() {
    }

    public Marker(double mLat, double mLng, String mInfo, String mAddr) {
        this.mLat = mLat;
        this.mLng = mLng;
        this.mInfo = mInfo;
        this.mAddr = mAddr;
    }

    public Marker(int mID, double mLat, double mLng, String mInfo, String mAddr) {
        this.mID = mID;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mInfo = mInfo;
        this.mAddr = mAddr;
    }



}