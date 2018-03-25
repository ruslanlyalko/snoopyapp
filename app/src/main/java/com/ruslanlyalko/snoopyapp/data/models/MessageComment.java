package com.ruslanlyalko.snoopyapp.data.models;

import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 21.01.2018.
 */

public class MessageComment {

    private String key;
    private String message;
    private String userId;
    private String userName;
    private String file;
    private String thumbnail;
    private Date date;
    private boolean removed;
    private String userAvatar;

    public MessageComment() {
    }

    public MessageComment(final String key, final String message, User user) {
        this.key = key;
        this.message = message;
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userAvatar = user.getAvatar();
        this.date = new Date();
    }

    public MessageComment(final String key, final String message, final String file, final String thumbnail, FirebaseUser user) {
        this.key = key;
        this.file = file;
        this.thumbnail = thumbnail;
        this.message = message;
        this.userId = user.getUid();
        this.userName = user.getDisplayName();
        this.date = new Date();
    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getMessage() != null ? getMessage().hashCode() : 0);
        result = 31 * result + (getUserId() != null ? getUserId().hashCode() : 0);
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getFile() != null ? getFile().hashCode() : 0);
        result = 31 * result + (getThumbnail() != null ? getThumbnail().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getRemoved() ? 1 : 0);
        result = 31 * result + (getUserAvatar() != null ? getUserAvatar().hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageComment)) return false;
        MessageComment that = (MessageComment) o;
        if (getRemoved() != that.getRemoved()) return false;
        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null)
            return false;
        if (getMessage() != null ? !getMessage().equals(that.getMessage()) : that.getMessage() != null)
            return false;
        if (getUserId() != null ? !getUserId().equals(that.getUserId()) : that.getUserId() != null)
            return false;
        if (getUserName() != null ? !getUserName().equals(that.getUserName()) : that.getUserName() != null)
            return false;
        if (getFile() != null ? !getFile().equals(that.getFile()) : that.getFile() != null)
            return false;
        if (getThumbnail() != null ? !getThumbnail().equals(that.getThumbnail()) : that.getThumbnail() != null)
            return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null)
            return false;
        return getUserAvatar() != null ? getUserAvatar().equals(that.getUserAvatar()) : that.getUserAvatar() == null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFile() {
        return file;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public boolean getRemoved() {
        return removed;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(final String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public void setRemoved(final boolean removed) {
        this.removed = removed;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }
}
