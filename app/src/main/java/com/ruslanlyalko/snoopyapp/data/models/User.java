package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private String userBDay;
    private String userCard;
    private boolean userIsAdmin;
    private String userPositionTitle = "Інструктор, Майстриня";
    private String userFirstDate;
    private String userTimeStart = "10:00";
    private String userTimeEnd = "19:00";

    private int userStavka = 60;
    private int userPercent = 8;
    private int mkBd = 0;
    private int mkBdChild = 8;
    private int mkArtChild = 10;
    private boolean mkSpecCalc = false;
    private String mkSpecCalcDate = "1-8-2017";
    private String avatar;
    private String token;
    private boolean receiveNotifications;
    private boolean showClients;
    private boolean isOnline;

    public User(String userId, String userName, String userPhone, String userEmail, String userBDay, String userFirstDate, String userCard, boolean userIsAdmin) {
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userBDay = userBDay;
        this.userCard = userCard;
        this.userIsAdmin = userIsAdmin;
        this.userFirstDate = userFirstDate;
    }

    public User() {
        // Default constructor required
    }

    public boolean getReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(final boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
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

    public String getMkSpecCalcDate() {
        return mkSpecCalcDate;
    }

    public void setMkSpecCalcDate(String mkSpecCalcDate) {
        this.mkSpecCalcDate = mkSpecCalcDate;
    }

    public boolean getMkSpecCalc() {
        return mkSpecCalc;
    }

    public void setMkSpecCalc(boolean mkSpecCalc) {
        this.mkSpecCalc = mkSpecCalc;
    }

    public int getMkBdChild() {
        return mkBdChild;
    }

    public void setMkBdChild(int mkBdChild) {
        this.mkBdChild = mkBdChild;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserStavka() {
        return userStavka;
    }

    public void setUserStavka(int userStavka) {
        this.userStavka = userStavka;
    }

    public int getUserPercent() {
        return userPercent;
    }

    public void setUserPercent(int userPercent) {
        this.userPercent = userPercent;
    }

    public int getMkArtChild() {
        return mkArtChild;
    }

    public void setMkArtChild(int mkArtChild) {
        this.mkArtChild = mkArtChild;
    }

    public int getMkBd() {
        return mkBd;
    }

    public void setMkBd(int mkBd) {
        this.mkBd = mkBd;
    }

    public boolean getUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    public String getUserBDay() {
        return userBDay;
    }

    public void setUserBDay(String userBDay) {
        this.userBDay = userBDay;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserCard() {
        return this.userCard;
    }

    public void setUserCard(String userCard) {
        this.userCard = userCard;
    }

    public String getUserPositionTitle() {
        return userPositionTitle;
    }

    public void setUserPositionTitle(String userPositionTitle) {
        this.userPositionTitle = userPositionTitle;
    }

    public String getUserFirstDate() {
        return userFirstDate;
    }

    public void setUserFirstDate(String userFirstDate) {
        this.userFirstDate = userFirstDate;
    }

    public String getUserTimeStart() {
        return userTimeStart;
    }

    public void setUserTimeStart(String userTimeStart) {
        this.userTimeStart = userTimeStart;
    }

    public String getUserTimeEnd() {
        return userTimeEnd;
    }

    public void setUserTimeEnd(String userTimeEnd) {
        this.userTimeEnd = userTimeEnd;
    }

    public boolean getShowClients() {
        return showClients;
    }

    public void setShowClients(final boolean showClients) {
        this.showClients = showClients;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(final boolean online) {
        isOnline = online;
    }
}
