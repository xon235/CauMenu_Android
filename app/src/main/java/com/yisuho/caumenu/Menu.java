package com.yisuho.caumenu;

/**
 * Created by xon23 on 2016-08-24.
 */
public class Menu implements Comparable<Menu>{
    private String mName;
    private String mDescription;
    private String mPrice;
    private String mBuildingCode;
    private SimpleTime mStartTime;
    private SimpleTime mEndTime;
    private int weight;

    public Menu(String name, String description, String price, String buildingCode, int startHour, int startMinute, int endHour, int endMinute, int weight){
        mName = name;
        mDescription = description;
        mPrice = price;
        mBuildingCode = buildingCode;
        mStartTime = new SimpleTime(startHour, startMinute);
        mEndTime = new SimpleTime(endHour, endMinute);
        this.weight = weight;
    }

    public SimpleTime getStartTime() {
        return mStartTime;
    }

    public SimpleTime getEndTime() {
        return mEndTime;
    }

    public String getBuildingCode() {
        return mBuildingCode;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getName() {
        return mName;
    }

    public String getPrice() {
        return mPrice;
    }

    public boolean isAvailableAt(int testTotalMin){
        return (mStartTime.getTotalMin() <= testTotalMin && testTotalMin <= mEndTime.getTotalMin());
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Menu menu) {
        return weight - menu.getWeight();
    }
}
