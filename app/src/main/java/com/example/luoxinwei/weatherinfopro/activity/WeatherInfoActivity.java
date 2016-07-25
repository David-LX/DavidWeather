package com.example.luoxinwei.weatherinfopro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.luoxinwei.weatherinfopro.R;
import com.example.luoxinwei.weatherinfopro.service.WeatherRefreshService;
import com.example.luoxinwei.weatherinfopro.util.HttpUtil;
import com.example.luoxinwei.weatherinfopro.util.Utility;

public class WeatherInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switshCity;
    private Button refreshWeather;
    private String weatherCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weatherInfoLayout);
        cityNameText = (TextView)findViewById(R.id.cityName);
        publishText = (TextView)findViewById(R.id.publishText);
        weatherDespText = (TextView)findViewById(R.id.weatherDesp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.currentDate);
        switshCity = (Button)findViewById(R.id.switchCity);
        switshCity.setOnClickListener(this);
        refreshWeather = (Button)findViewById(R.id.refeshWeather);
        refreshWeather.setOnClickListener(this);
        weatherCode = getIntent().getStringExtra("countyCode");
//        Log.d("WeatherInfoActivity",weatherCode);
        if (!TextUtils.isEmpty(weatherCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherInfo(weatherCode);

        }else {
            showWeather();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switchCity:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                MyApplication.isRefresh = true;
                startActivity(intent);
                finish();
                break;
            case R.id.refeshWeather:
                publishText.setText("同步中...");
//                SharedPreferences prefs = PreferenceManager.
//                        getDefaultSharedPreferences(this);
//                String prWeatherCode = prefs.getString("weatherCode", "");
//                if (!TextUtils.isEmpty(prWeatherCode)) {
//                    if (weatherCode.equals(prWeatherCode)){
//
//                    }
//                    queryWeatherInfo(prWeatherCode);
//                }else {
                queryFromServer(weatherCode);
//                }
        }
    }

    public void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        Log.d("WeatherInfoActivity",address);
        queryFromServer(address);
    }

    public void queryFromServer(String address){
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("WeatherInfoActivity",response);
                if (!TextUtils.isEmpty(response)){
                    boolean result =  Utility.handleWeatherResponse(WeatherInfoActivity.this,response);
                    if (result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(sharedPreferences.getString("cityName",""));
        temp1Text.setText(sharedPreferences.getString("temp1",""));
        temp2Text.setText(sharedPreferences.getString("temp2",""));
        weatherDespText.setText(sharedPreferences.getString("weatherDesp",""));
        publishText.setText("今天" + sharedPreferences.getString("publishTime", "") + "发布");
        currentDateText.setText(sharedPreferences.getString("currentDate",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, WeatherRefreshService.class);
        startService(intent);

    }
}
