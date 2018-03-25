package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 29.01.2018.
 */

public class Birthday implements Serializable {

    private String key;
    private String contactKey;
    private String childName;
    private Date bdDate = new Date();
    private String description;
    private int kidsCount;
    private boolean mk;
    private boolean aqua;
    private boolean cinema;
    private Date createdAt = new Date();

    public Birthday() {
    }

    public Birthday(final String contactKey, final String childName) {
        this.contactKey = contactKey;
        this.childName = childName;
    }


    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getContactKey() {
        return contactKey;
    }

    public void setContactKey(final String contactKey) {
        this.contactKey = contactKey;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(final String childName) {
        this.childName = childName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getBdDate() {
        return bdDate;
    }

    public void setBdDate(final Date bdDate) {
        this.bdDate = bdDate;
    }

    public int getKidsCount() {
        return kidsCount;
    }

    public void setKidsCount(final int kidsCount) {
        this.kidsCount = kidsCount;
    }

    public boolean getMk() {
        return mk;
    }

    public void setMk(final boolean mk) {
        this.mk = mk;
    }

    public boolean getCinema() {
        return cinema;
    }

    public void setCinema(final boolean cinema) {
        this.cinema = cinema;
    }

    public boolean getAqua() {
        return aqua;
    }

    public void setAqua(final boolean aqua) {
        this.aqua = aqua;
    }
}
