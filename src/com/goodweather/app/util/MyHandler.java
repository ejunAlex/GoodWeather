package com.goodweather.app.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

public class MyHandler extends DefaultHandler{
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    
    public List<Province> getProvinces() {
        return provinces;
    }
    public List<City> getCities() {
        return cities;
    }
    public List<County> getCounties() {
        return counties;
    }
    @Override
    public void startDocument() throws SAXException {
        provinces = new ArrayList<Province>();
        cities = new ArrayList<City>();
        counties = new ArrayList<County>();
    }
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        if("province".equals(localName)){
            Province p = new Province();
            for(int i = 0; i < attributes.getLength(); i++){
                if("name".equals(attributes.getQName(i)))
                    p.setProvinceName(attributes.getValue(i));
                else if("id".equals(attributes.getQName(i)))
                    p.setProvinceCode(attributes.getValue(i));
            }
            provinces.add(p);
        }
        else if("city".equals(localName)){
            City p = new City();
            for(int i = 0; i < attributes.getLength(); i++){
                if("name".equals(attributes.getQName(i)))
                    p.setCityName(attributes.getValue(i));
                else if("id".equals(attributes.getQName(i)))
                    p.setCityCode(attributes.getValue(i));
            }
            p.setProvinceCode(p.getCityCode().substring(0, 2));
            cities.add(p);
        }
        else if("county".equals(localName)){
            County c = new County();
            for(int i = 0; i < attributes.getLength(); i++){
                if("name".equals(attributes.getQName(i)))
                    c.setCountyName(attributes.getValue(i));
                else if("weatherCode".equals(attributes.getQName(i)))
                    c.setWeatherCode(attributes.getValue(i));
                else if("id".equals(attributes.getQName(i)))
                    c.setCountyCode(attributes.getValue(i));
            }
            c.setCityCode(c.getCountyCode().substring(0, 4));
            counties.add(c);
        }
    }
}
