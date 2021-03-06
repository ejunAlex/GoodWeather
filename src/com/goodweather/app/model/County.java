package com.goodweather.app.model;

public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private String weatherCode;
    private String cityCode;
    
    public String getWeatherCode() {
        return weatherCode;
    }
    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCountyName() {
        return countyName;
    }
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
    public String getCountyCode() {
        return countyCode;
    }
    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }
    public String getCityCode() {
        return cityCode;
    }
    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
    
}
