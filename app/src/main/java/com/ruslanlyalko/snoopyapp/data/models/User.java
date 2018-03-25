package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String birthdayDate = "01.06.1991";
    private String card;
    private String positionTitle = "Інструктор, Майстриня";
    private String workingFromDate = "01.06.2017";
    private String workingStartTime = "10:00";
    private String workingEndTime = "19:00";
    private String avatar;
    private String token;
    private boolean isAdmin;
    private boolean isReceiveNotification;
    private boolean isAllowViewClients;
    private boolean isOnline;

    public User(String id, String fullName, String phone, String email, String birthdayDate, String workingFromDate, String card, boolean isAdmin) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.birthdayDate = birthdayDate;
        this.card = card;
        this.isAdmin = isAdmin;
        this.workingFromDate = workingFromDate;
    }

    public User() {
        // Default constructor required
    }

    public boolean getIsReceiveNotification() {
        return isReceiveNotification;
    }

    public void setIsReceiveNotification(final boolean receiveNotification) {
        this.isReceiveNotification = receiveNotification;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public String getBirthdayDate() {
        return birthdayDate;
    }

    public void setBirthdayDate(String birthdayDate) {
        this.birthdayDate = birthdayDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCard() {
        return this.card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getWorkingFromDate() {
        return workingFromDate;
    }

    public void setWorkingFromDate(String workingFromDate) {
        this.workingFromDate = workingFromDate;
    }

    public String getWorkingStartTime() {
        return workingStartTime;
    }

    public void setWorkingStartTime(String workingStartTime) {
        this.workingStartTime = workingStartTime;
    }

    public String getWorkingEndTime() {
        return workingEndTime;
    }

    public void setWorkingEndTime(String workingEndTime) {
        this.workingEndTime = workingEndTime;
    }

    public boolean getIsAllowViewClients() {
        return isAllowViewClients;
    }

    public void setIsAllowViewClients(final boolean allowViewClients) {
        this.isAllowViewClients = allowViewClients;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(final boolean online) {
        isOnline = online;
    }
}
