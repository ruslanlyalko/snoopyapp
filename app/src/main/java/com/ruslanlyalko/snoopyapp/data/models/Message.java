package com.ruslanlyalko.snoopyapp.data.models;

import java.util.Date;

public class Message {

    private String key;
    private String title1;
    private String title2;
    private String description;
    private String link;
    private String date;
    private Date createdAt = new Date();
    private Date updatedAt = new Date();
    private String lastComment;

    private String userId;
    private String userName;
    private boolean commentsEnabled;

    public Message() {
    }

    public Message(String key, String title1, String title2, String description, String link, String date, String userId, String userName) {
        this.key = key;
        this.title1 = title1;
        this.title2 = title2;
        this.description = description;
        this.link = link;
        this.date = date;
        this.userId = userId;
        this.userName = userName;
    }

    public boolean getCommentsEnabled() {
        return commentsEnabled;
    }

    public void setCommentsEnabled(final boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastComment() {
        return lastComment;
    }

    public void setLastComment(final String lastComment) {
        this.lastComment = lastComment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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