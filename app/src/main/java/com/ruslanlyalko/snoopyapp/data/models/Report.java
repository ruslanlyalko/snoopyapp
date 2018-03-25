package com.ruslanlyalko.snoopyapp.data.models;

import java.util.Date;

public class Report {

    private String key;
    private Date reportDate;
    private String phone;
    private String place;
    private boolean isPinyataOrdered;
    private float orderHours;
    private int orderRate;
    private int orderPinyataPrice;
    private int orderTotal;
    private int childrenCount;
    private int childrenAgesFrom;
    private int childrenAgesTo;
    private String childName;
    private int childAge;
    private String comment;
    private String createdBy;
    private String createdById;
    private Date createdDate;
    private Date updatedDate;

    public Report() {
    }

    public Report(String createdBy, String createdById, String reportDate) {
        this.createdBy = createdBy;
        this.createdById = createdById;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(final Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public boolean getIsPinyataOrdered() {
        return isPinyataOrdered;
    }

    public void setIsPinyataOrdered(final boolean pinyataOrdered) {
        isPinyataOrdered = pinyataOrdered;
    }

    public float getOrderHours() {
        return orderHours;
    }

    public void setOrderHours(final float orderHours) {
        this.orderHours = orderHours;
    }

    public int getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(final int orderRate) {
        this.orderRate = orderRate;
    }

    public int getOrderPinyataPrice() {
        return orderPinyataPrice;
    }

    public void setOrderPinyataPrice(final int orderPinyataPrice) {
        this.orderPinyataPrice = orderPinyataPrice;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(final int orderTotal) {
        this.orderTotal = orderTotal;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(final int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public int getChildrenAgesFrom() {
        return childrenAgesFrom;
    }

    public void setChildrenAgesFrom(final int childrenAgesFrom) {
        this.childrenAgesFrom = childrenAgesFrom;
    }

    public int getChildrenAgesTo() {
        return childrenAgesTo;
    }

    public void setChildrenAgesTo(final int childrenAgesTo) {
        this.childrenAgesTo = childrenAgesTo;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(final String childName) {
        this.childName = childName;
    }

    public int getChildAge() {
        return childAge;
    }

    public void setChildAge(final int childAge) {
        this.childAge = childAge;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(final String createdById) {
        this.createdById = createdById;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(final Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(final Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
