package com.goodweather.app.activity;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodweather.app.R;
import com.goodweather.app.db.GoodWeatherDB;
import com.goodweather.app.model.City;
import com.goodweather.app.model.County;
import com.goodweather.app.model.Province;
import com.goodweather.app.util.HttpCallbackListener;
import com.goodweather.app.util.HttpUtil;
import com.goodweather.app.util.MyHandler;
import com.goodweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private GoodWeatherDB goodWeatherDB;
    private List<String> dataList = new ArrayList<String>();
    
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //选中的省、市、级别
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private String cityCode;
    
    private List<Province> provinces;
    private List<City> cities;
    private List<County> counties;
    
    private MyHandler handler;
    
    private boolean isFromWeatherActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        titleText = (TextView)findViewById(R.id.title_text);
        listView = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
//        setScroll();
        goodWeatherDB = GoodWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }
                else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
                else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(position).getWeatherCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryFromServer();
    }
    private void queryProvinces(){
        provinceList = goodWeatherDB.loadProvinces();
        dataList.clear();
        for(Province province : provinceList){
            dataList.add(province.getProvinceName());
         }
         //如果适配器的内容改变时,需要强制调用getView来刷新每个Item的内容
         adapter.notifyDataSetChanged();
         titleText.setText("中国");
         currentLevel = LEVEL_PROVINCE;
    }
    
    private void queryCities(){
        if(!goodWeatherDB.existCity(selectedProvince.getProvinceCode())){
            Utility.saveCities(goodWeatherDB, selectedProvince.getProvinceCode(), cities);
        }
        cityList = goodWeatherDB.loadCities(selectedProvince.getProvinceCode());
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            //如果适配器的内容改变时,需要强制调用getView来刷新每个Item的内容
            adapter.notifyDataSetChanged();
            //直接跳转到第一个item
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
    }
    
    private void queryCounties(){
        if(!goodWeatherDB.existCounty(selectedCity.getCityCode()))
            Utility.saveCounties(goodWeatherDB, selectedCity.getCityCode(), counties);
        countyList = goodWeatherDB.loadCounties(selectedCity.getCityCode());
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            //如果适配器的内容改变时,需要强制调用getView来刷新每个Item的内容
            adapter.notifyDataSetChanged();
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
    }
    public void queryFromServer(){
        String address;
        address = "http://10.0.2.2:8080/goodWeather/city.xml";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
         public void onFinish(String response) {
             XMLReader reader = null;
             SAXParserFactory factory = SAXParserFactory.newInstance();
             try{
                 reader = factory.newSAXParser().getXMLReader();
                 handler = new MyHandler();
                 reader.setContentHandler(handler);
                 reader.parse(new InputSource(new StringReader(response)));
             }
             catch(Exception e){
                 e.printStackTrace();
             }
             ChooseAreaActivity.this.runOnUiThread(new Runnable(){
                 @Override
                public void run() {
                     provinces = handler.getProvinces();
                     cities = handler.getCities();
                     counties = handler.getCounties();
                     closeProgressDialog();
                     if(!goodWeatherDB.existProvince())
                         Utility.saveProvinces(goodWeatherDB, provinces);
                     queryProvinces();
                }
             });
        }
            public void onError(Exception e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    public void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载……");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    public void closeProgressDialog(){
        if(progressDialog != null)
            progressDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_PROVINCE){
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
        else if(currentLevel == LEVEL_CITY)
            queryProvinces();
        else
            queryCities();
    }
}
