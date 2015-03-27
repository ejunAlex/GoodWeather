package com.goodweather.app.db;
/*
 * ��װ���ݿ��������
 * ����ģʽ
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;

public class GoodWeatherDB {
    public static final String DB_NAME = "good_weather";
    public static final int VERSION = 1;
    private static GoodWeatherDB goodWeatherDB;
    private SQLiteDatabase db;
    
    private GoodWeatherDB(Context context){
        GoodWeatherOpenHelper dbHelper = new GoodWeatherOpenHelper(context, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }
    public synchronized GoodWeatherDB getInstance(Context context){
        if(goodWeatherDB == null){
            goodWeatherDB = new GoodWeatherDB(context);
        }
        return goodWeatherDB;
    }
    //��Provinceʵ���洢������
    public void saveProvince(Province province){
        if(province != null){
            ContentValues cv = new ContentValues();
            cv.put("province_name", province.getProvinceName());
            cv.put("province_code", province.getProvinceCode());
            db.insert("Province", null, cv);
        }
    }
    //�����ݿ��ж�ȡ����ʡ����Ϣ
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        if(cursor != null)
            cursor.close();
        return list;
    }
   //��Cityʵ���洢������
    public void saveCity(City city){
        if(city != null){
            ContentValues cv = new ContentValues();
            cv.put("city_name", city.getCityName());
            cv.put("city_code", city.getCityCode());
            db.insert("City", null, cv);
        }
    }
    //�����ݿ��ж�ȡĳʡ�µ����г�����Ϣ
    public List<City> loadcities(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{provinceId + ""}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                list.add(city);
            }while(cursor.moveToNext());
        }
        if(cursor != null)
            cursor.close();
        return list;
    }
  //��Countyʵ���洢������
    public void saveCounty(County county){
        if(county != null){
            ContentValues cv = new ContentValues();
            cv.put("county_name", county.getCountyName());
            cv.put("county_code", county.getCountyCode());
            db.insert("County", null, cv);
        }
    }
    //�����ݿ��ж�ȡĳ�����µ���������Ϣ
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "City_id = ?", new String[]{cityId + ""}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                list.add(county);
            }while(cursor.moveToNext());
        }
        if(cursor != null)
            cursor.close();
        return list;
    }
    
}
