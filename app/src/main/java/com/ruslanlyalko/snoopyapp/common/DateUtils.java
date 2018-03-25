package com.ruslanlyalko.snoopyapp.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ruslan Lyalko
 * on 11.11.2017.
 */

public class DateUtils {

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    }

    public static String getFirstLetters(String displayName) {
        int ind = displayName.indexOf(' ');
        return displayName.substring(0, 1) + displayName.substring(ind + 1, ind + 2);
    }

    public static boolean todayOrFuture(String dateStr) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        try {
            Date date = new SimpleDateFormat("d-M-yyyy", Locale.US).parse(dateStr);
            date.setHours(1);
            return date.getTime() >= today.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //todo write logic here
        return false;
    }

    public static boolean future(Date date) {
        Date today = new Date();
        today.setHours(23);
        today.setMinutes(59);
        today.setSeconds(59);
        date.setHours(1);
        return date.getTime() > today.getTime();
    }

    public static boolean future(String dateStr) {
        Date today = new Date();
        today.setHours(23);
        today.setMinutes(59);
        today.setSeconds(59);
        try {
            Date date = new SimpleDateFormat("d-M-yyyy", Locale.US).parse(dateStr);
            date.setHours(1);
            return date.getTime() > today.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTodayOrFuture(String firstDate, String secondDate) {
        try {
            Date dateFirst = new SimpleDateFormat("d-M-yyyy", Locale.US).parse(firstDate);
            Date dateSecond = new SimpleDateFormat("d-M-yyyy", Locale.US).parse(secondDate);
            return dateFirst.getTime() >= dateSecond.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false; //todo
    }

    public static String getIntWithSpace(int data) {
        String resultStr = data + "";
        if (resultStr.length() > 3)
            resultStr = resultStr.substring(0, resultStr.length() - 3) + " "
                    + resultStr.substring(resultStr.length() - 3, resultStr.length());
        return resultStr;
    }

    public static boolean isCurrentYear(final Date date) {
        return date.getYear() == new Date().getYear();
    }

    public static Date getCurrentMonthFirstDate() {
        Date mCurrentMonth = new Date();
        mCurrentMonth.setDate(1);
        return mCurrentMonth;
    }

    public static String getYearFromStr(String date) {
        int last = date.lastIndexOf('-');
        return date.substring(last + 1);
    }

    public static String getMonthFromStr(String date) {
        int first = date.indexOf('-');
        int last = date.lastIndexOf('-');
        return date.substring(first + 1, last);
    }

    public static int getDifference(String date, String time) {
        if (time == null || time.isEmpty()) return 10;
        SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy HH:mm", Locale.US);
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d2 = format.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Get msec from each, and subtract.
        long diff = d1.getTime() - d2.getTime();
        return (int) TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    public static String getUpdatedAt(final Date updatedAt) {
        if (isTodayOrFuture(updatedAt))
            return toString(updatedAt, "HH:mm");
        return toString(updatedAt, "dd.MM.yy");
    }

    public static boolean isTodayOrFuture(Date date) {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        return date.getTime() >= today.getTime();
    }

    public static String toString(final Date date, final String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    public static String toString(final Date date) {
        return new SimpleDateFormat("d-M-yyyy", Locale.getDefault()).format(date);
    }

    public static String getCurrentYear() {
        return new SimpleDateFormat("yyyy", Locale.US).format(new Date());
    }

    public static String getCurrentMonth() {
        return new SimpleDateFormat("M", Locale.US).format(new Date());
    }

    public static boolean isBetween(final Date bdDate, final String from, final String to) {
        Date fromDate = parse(from, "dd.MM.yyyy");
        fromDate.setHours(0);
        fromDate.setMinutes(0);
        Date toDate = parse(to, "dd.MM.yyyy");
        toDate.setHours(23);
        toDate.setMinutes(59);
        bdDate.setMinutes(5);
        return bdDate.getTime() > fromDate.getTime() && bdDate.getTime() < toDate.getTime();
    }

    public static Date parse(final String text, final String pattern) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat(pattern, Locale.US).parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate(final int year, final int month, final int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    private static long getDateDiff(Date oldDate, Date newDate) {
        try {
            return TimeUnit.DAYS.convert(newDate.getTime() - oldDate.getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getAge(Date dateOfBirth) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        Integer ageInt = Integer.valueOf(age);
        return " (" + ageInt.toString() + "р)";
    }

    public static String getHowLongTime(final Date date, final String pattern) {
        return isToday(date)
                ? "Сьогодні"
                : isTomorrow(date)
                ? "Завтра"
                : isYesterday(date)
                ? "Вчора"
                : toString(date, pattern);
    }

    private static boolean isToday(final Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return today.get(Calendar.YEAR) == calDate.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == calDate.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == calDate.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isTomorrow(final Date date) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return tomorrow.get(Calendar.YEAR) == calDate.get(Calendar.YEAR)
                && tomorrow.get(Calendar.MONTH) == calDate.get(Calendar.MONTH)
                && tomorrow.get(Calendar.DAY_OF_MONTH) == calDate.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean isYesterday(final Date date) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(date);
        return yesterday.get(Calendar.YEAR) == calDate.get(Calendar.YEAR)
                && yesterday.get(Calendar.MONTH) == calDate.get(Calendar.MONTH)
                && yesterday.get(Calendar.DAY_OF_MONTH) == calDate.get(Calendar.DAY_OF_MONTH);
    }
}
