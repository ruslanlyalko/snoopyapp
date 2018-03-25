package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;
import java.util.Date;

public class Expense implements Serializable {

    private String key;
    private String title1;
    private String type;
    private int price;
    private Date expenseDate;
    private String image;
    private String userId;
    private String userName;

    public Expense() {
    }

    public Expense(final String type, final Date date, final String userId, final String userName) {
        this.type = type;
        this.expenseDate = date;
        this.userId = userId;
        this.userName = userName;
    }

    public Expense(String title1, String type, Date date, String image, String userId, String userName, int price) {
        this.title1 = title1;
        this.type = type;
        this.expenseDate = date;
        this.image = image;
        this.userId = userId;
        this.userName = userName;
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(final Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}