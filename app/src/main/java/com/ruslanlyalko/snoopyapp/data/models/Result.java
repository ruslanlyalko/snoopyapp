package com.ruslanlyalko.snoopyapp.data.models;

/**
 * Created by Ruslan Lyalko
 * on 11.01.2018.
 */

public class Result {

    private int income;
    private int income80;
    private int salary;
    private int costs;
    private int profit;
    private String year;
    private String month;

    public Result() {
        //required
    }

    public Result(final int income, final int income80, final int salary, final int costs, final int profit, final String year, final String month) {
        this.income = income;
        this.income80 = income80;
        this.salary = salary;
        this.costs = costs;
        this.profit = profit;
        this.year = year;
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(final String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(final String month) {
        this.month = month;
    }

    public int getIncome80() {
        return income80;
    }

    public void setIncome80(final int income80) {
        this.income80 = income80;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(final int income) {
        this.income = income;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(final int salary) {
        this.salary = salary;
    }

    public int getCosts() {
        return costs;
    }

    public void setCosts(final int costs) {
        this.costs = costs;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(final int profit) {
        this.profit = profit;
    }
}
