package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 29.01.2018.
 */

public class Contact implements Serializable {

    private String key;
    private String name;
    private String phone;
    private String phone2;
    private String childName1;
    private String childName2;
    private String childName3;
    private Date childBd1 = new Date();
    private Date childBd2 = new Date();
    private Date childBd3 = new Date();
    private Date createdAt = new Date();
    private String description;


    public Contact() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(final String phone2) {
        this.phone2 = phone2;
    }

    public String getChildName1() {
        return childName1;
    }

    public void setChildName1(final String childName1) {
        this.childName1 = childName1;
    }

    public String getChildName2() {
        return childName2;
    }

    public void setChildName2(final String childName2) {
        this.childName2 = childName2;
    }

    public String getChildName3() {
        return childName3;
    }

    public void setChildName3(final String childName3) {
        this.childName3 = childName3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getChildBd1() {
        return childBd1;
    }

    public void setChildBd1(final Date childBd1) {
        this.childBd1 = childBd1;
    }

    public Date getChildBd2() {
        return childBd2;
    }

    public void setChildBd2(final Date childBd2) {
        this.childBd2 = childBd2;
    }

    public Date getChildBd3() {
        return childBd3;
    }

    public void setChildBd3(final Date childBd3) {
        this.childBd3 = childBd3;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }
}
