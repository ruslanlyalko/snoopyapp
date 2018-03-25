package com.ruslanlyalko.snoopyapp.data.models;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 12.01.2018.
 */
public class Notification {

    private String key;
    private Date createdAt = new Date();

    public Notification() {
    }

    public Notification(final String key) {
        this.key = key;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification notification = (Notification) o;
        return getKey() != null ? getKey().equals(notification.getKey()) : notification.getKey() == null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }
}
