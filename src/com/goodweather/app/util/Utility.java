package com.goodweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

/**
 * 解析服务器返回的数据，并存储到数据库里
 * @author DELL
 *
 */
public class Utility {
    public synchronized static void saveProvinces(GoodWeatherDB goodWeatherDB, List<Province> provinces){
        for(Province p : provinces)
            goodWeatherDB.saveProvince(p);
    }
    public synchronized static void saveCities(GoodWeatherDB goodWeatherDB, String provinceCode, List<City> cities){
        for(City c : cities){
            if(c.getProvinceCode().equals(provinceCode))
                goodWeatherDB.saveCity(c);
        }
    }
    public synchronized static void saveCounties(GoodWeatherDB goodWeatherDB, String cityCode, List<County> counties){
        for(County c : counties){
            if(c.getCityCode().equals(cityCode))
                goodWeatherDB.saveCounty(c);
        }
    }
    public static void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        }
            catch(JSONException e){
                e.printStackTrace();
            }
        
    }
    //将服务器返回的所有天气信息存储到SharedPreferences文件中
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, 
            String temp2, String weatherDesp, String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
