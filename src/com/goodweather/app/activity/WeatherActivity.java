package com.goodweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodweather.app.R;
import com.goodweather.app.R.id;
import com.goodweather.app.service.AutoUpdateService;
import com.goodweather.app.util.HttpCallbackListener;
import com.goodweather.app.util.HttpUtil;
import com.goodweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    
    private Button switchCity;
    private Button refreshWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refres_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)){
            publishText.setText("同步中……");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(countyCode);
        }
        else{
            showWeather();
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
        case R.id.switch_city:
            Intent intent = new Intent(this, ChooseAreaActivity.class);
            intent.putExtra("from_weather_activity", true);
            startActivity(intent);
            finish();
            break;
        case R.id.refres_weather:
            publishText.setText("同步中……");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String weatherCode = prefs.getString("weather_code", "");
            if(!TextUtils.isEmpty(weatherCode))
                queryWeatherInfo(weatherCode);
            break;
        default:
                break;
        }
        
    }
    public void queryWeatherInfo(String countyCode){
        String address = "http://www.weather.com.cn/adat/cityinfo/" + countyCode + ".html";
        HttpUtil.sendHttpRequest2(address, new HttpCallbackListener(){
         public void onFinish(String response) {
             Utility.handleWeatherResponse(WeatherActivity.this, response);
             runOnUiThread(new Runnable(){
                 @Override
                public void run() {
                    showWeather();
                }
             });
        }
            public void onError(Exception e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
    public void showWeather(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = sp.getString("city_name", "");
        String publishTime = sp.getString("publish_time", "");
        String weatherDesp = sp.getString("weather_desp", "");
        String temp1 = sp.getString("temp1", "");
        String temp2 = sp.getString("temp2", "");
        String currentDate = sp.getString("current_date", "");
        cityNameText.setText(cityName);
        publishText.setText(publishTime);
        weatherDespText.setText(weatherDesp);
        temp1Text.setText(temp2);
        temp2Text.setText(temp1);
        currentDateText.setText(currentDate);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
