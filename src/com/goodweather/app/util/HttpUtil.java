package com.goodweather.app.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.goodweather.app.application.MyApplication;

public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection = null;
                StringBuilder sb = new StringBuilder();
                try{
                    Context context = MyApplication.getContext();
                    File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + "city");
                    if(!file.exists())
                        file.mkdirs();
                    File city = new File(file, "city.xml");
                    if(!city.exists() || city.length() == 0){
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(address);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        if(httpResponse.getStatusLine().getStatusCode() == 200){
                            HttpEntity entity = httpResponse.getEntity();
                            String response = EntityUtils.toString(entity, "utf-8");
                            FileOutputStream out = new FileOutputStream(city);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                            writer.write(response);
                        }
                    }
                    sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(city)));
                    String line = "";
                    while((line = reader.readLine()) != null)
                        sb.append(line);
                    if(listener != null)
                        listener.onFinish(sb.toString());
                }
                catch(Exception e){
                    e.printStackTrace();
                    listener.onError(e);
                }
                finally{
                    if(connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
    
    public static void sendHttpRequest2(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable(){
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String response = "";
                try{
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(address);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        if(httpResponse.getStatusLine().getStatusCode() == 200){
                            HttpEntity entity = httpResponse.getEntity();
                            response = EntityUtils.toString(entity, "utf-8");
                        }
                    if(listener != null)
                        listener.onFinish(response);
                }
                catch(Exception e){
                    e.printStackTrace();
                    listener.onError(e);
                }
                finally{
                    if(connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
}
