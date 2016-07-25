package com.example.luoxinwei.weatherinfopro.model;

/**
 * Created by luoxinwei on 2016/7/23.
 */
public class Province {
    private String provinceName;
    private String provinceCode;
    private int provinceId;

    public String getProvinceCode() {
        return provinceCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
