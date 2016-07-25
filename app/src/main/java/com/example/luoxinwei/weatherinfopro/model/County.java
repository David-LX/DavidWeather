package com.example.luoxinwei.weatherinfopro.model;

/**
 * Created by luoxinwei on 2016/7/23.
 */
public class County {
    private String countyName;
    private String countyCode;
    private int countyId;
    private int cityId;

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public int getCountyId() {
        return countyId;
    }

    public String getCountyName() {
        return countyName;
    }
}
