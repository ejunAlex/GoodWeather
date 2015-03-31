package com.goodweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.goodweather.app.receiver.AutoUpdateReceiver;
import com.goodweather.app.util.HttpCallbackListener;
import com.goodweather.app.util.HttpUtil;
import com.goodweather.app.util.Utility;

public class AutoUpdateService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //8 hours
        int anHour = 8 * 60 * 60 * 1000;
        long triggerTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest2(address, new HttpCallbackListener(){
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this, response);
            }
            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
