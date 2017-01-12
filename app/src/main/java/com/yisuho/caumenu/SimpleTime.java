package com.yisuho.caumenu;

/**
 * Created by xon23 on 2016-08-24.
 */
public class SimpleTime {
    private int mHour;
    private int mMinute;
    public static final int TOTAL_MIN_OF_DAY = 1440;
    public static final int HOUR_IN_MIN = 60;

    public SimpleTime(int hour, int minute){
        mHour = hour;
        mMinute = minute;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public int getTotalMin(){
        return ((mHour * 60) + mMinute);
    }

    public String getTimeString(){
        return String.format("%02d:%02d", mHour, mMinute);
    }

    public static int convertToTotalMin(int hour, int minute){
        return (hour * 60) + minute;
    }
}
