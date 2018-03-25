package com.ruslanlyalko.snoopyapp.data.models;

import com.ruslanlyalko.snoopyapp.common.DateUtils;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 21.01.2018.
 */

public class PushNotification {

    private String title;
    private String message;
    private String token;
    private String messageKey;
    private String senderId;
    private String senderName;
    private String receiverName;
    private String type;
    private String date;

    public PushNotification() {
    }

    public PushNotification(final String title, final String message, final String token, final String messageKey, final String senderId, final String senderName, final String receiverName, MessageType messageType) {
        this.title = title;
        this.message = message;
        this.token = token;
        this.messageKey = messageKey;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.date = DateUtils.toString(new Date(), "d-M-yyyy HH:mm:ss");
        this.type = messageType.name().toUpperCase();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(final String receiverName) {
        this.receiverName = receiverName;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(final String messageKey) {
        this.messageKey = messageKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(final String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }
}


