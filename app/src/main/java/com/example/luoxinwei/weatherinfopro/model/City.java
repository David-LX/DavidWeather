package com.example.luoxinwei.weatherinfopro.model;

/**
 * Created by luoxinwei on 2016/7/23.
 */
public class City {
    private  String cityName;
    private  String cityCode;
    private int cityId;
    private int provinceId;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public int getProvinceId() {
        return provinceId;
    }
}
