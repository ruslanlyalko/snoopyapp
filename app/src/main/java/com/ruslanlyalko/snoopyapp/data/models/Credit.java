package com.ruslanlyalko.snoopyapp.data.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 11.01.2018.
 */

public class Credit  implements Serializable{

    private String key;
    private int money;
    private String description;
    private Date date;
    private String month;
    private String year;

    public Credit() {
        //required
    }

    public Credit(final int money, final String description, final Date date, final String month, final String year) {
        this.money = money;
        this.description = description;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(final int money) {
        this.money = money;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(final String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }
}
