package com.luozenglin.myweather.common;

import org.litepal.crud.DataSupport;

public class Location extends DataSupport {
    private int id;
    private String county;
    private String weatherId;
    private boolean isMyLocation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public boolean isMyLocation() {
        return isMyLocation;
    }

    public void setMyLocation(boolean myLocation) {
        isMyLocation = myLocation;
    }
}
