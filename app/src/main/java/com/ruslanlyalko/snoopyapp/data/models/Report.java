package com.ruslanlyalko.snoopyapp.data.models;

import java.util.Date;

public class Report {

    public String userId;
    public String userName;
    public String date;
    public String mkRef;
    public String mkName;

    public int total;
    public int totalRoom;
    public int totalBday;
    public int totalMk;

    public int r60;
    public int r30;
    public int r20;
    public int r10;

    public int b50;
    public int b10;
    public int b30;
    public int bMk;

    public int mk1;
    public int mk2;
    public int mkt1;
    public int mkt2;
    public boolean mkMy = true;
    public String imageUri;

    public String comment;

    private boolean checkedListDone;
    private Date checkedListTime;
    private double checkedListLatitude;
    private double checkedListLongitude;

    private boolean halfSalary;

    public Report() {
    }

    public Report(String userId, String userName, String date) {
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.mkRef = "";
        this.mkName = "";
        this.imageUri = "";
    }

    public double getCheckedListLatitude() {
        return checkedListLatitude;
    }

    public void setCheckedListLatitude(final double checkedListLatitude) {
        this.checkedListLatitude = checkedListLatitude;
    }

    public double getCheckedListLongitude() {
        return checkedListLongitude;
    }

    public void setCheckedListLongitude(final double checkedListLongitude) {
        this.checkedListLongitude = checkedListLongitude;
    }

    public boolean getCheckedListDone() {
        return checkedListDone;
    }

    public void setCheckedListDone(final boolean checkedListDone) {
        this.checkedListDone = checkedListDone;
    }

    public Date getCheckedListTime() {
        return checkedListTime;
    }

    public void setCheckedListTime(final Date checkedListTime) {
        this.checkedListTime = checkedListTime;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getComment() {
        return comment;
    }

    public int getB10() {
        return b10;
    }

    public boolean isMkMy() {
        return mkMy;
    }

    public int getR10() {
        return r10;
    }

    public String getMkRef() {
        return mkRef;
    }

    public String getMkName() {
        return mkName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public int getTotal() {
        return total;
    }

    public int getTotalRoom() {
        return totalRoom;
    }

    public int getTotalBday() {
        return totalBday;
    }

    public int getTotalMk() {
        return totalMk;
    }

    public int getR60() {
        return r60;
    }

    public int getR30() {
        return r30;
    }

    public int getR20() {
        return r20;
    }

    public int getB50() {
        return b50;
    }

    public int getB30() {
        return b30;
    }

    public int getbMk() {
        return bMk;
    }

    public int getMk1() {
        return mk1;
    }

    public int getMk2() {
        return mk2;
    }

    public int getMkt1() {
        return mkt1;
    }

    public int getMkt2() {
        return mkt2;
    }

    public boolean getHalfSalary() {
        return halfSalary;
    }

    public void setHalfSalary(final boolean halfSalary) {
        this.halfSalary = halfSalary;
    }

    public void clearReport(boolean clearMK) {
        r60 = 0;
        r30 = 0;
        r20 = 0;
        r10 = 0;
        b50 = 0;
        b10 = 0;
        b30 = 0;
        bMk = 0;
        mkMy = true;
        mk1 = 0;
        mkt1 = 0;
        mk2 = 0;
        mkt2 = 0;
        comment = "";
        imageUri = "";
        if (clearMK) {
            mkName = "";
            mkRef = "";
        }
        checkedListLatitude = 0;
        checkedListLongitude = 0;
        checkedListDone = false;
        halfSalary = false;
        checkedListTime = null;
    }
}
