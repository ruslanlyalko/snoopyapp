package com.ruslanlyalko.snoopyapp.data.models;

public class Mk {

    private String key;
    private String title1;
    private String title2;
    private String description;
    private String imageUri;
    private String link;
    private String userId;
    private String userName;

    public Mk() {
        //firebase required
    }

    public Mk(String key, String title1, String title2, String description, String link, String imageUri, String userId, String userName) {
        this.key = key;
        this.title1 = title1;
        this.title2 = title2;
        this.description = description;
        this.imageUri = imageUri;
        this.link = link;
        this.userId = userId;
        this.userName = userName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}