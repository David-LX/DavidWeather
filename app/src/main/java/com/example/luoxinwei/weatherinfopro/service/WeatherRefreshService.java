package com.example.luoxinwei.weatherinfopro.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.luoxinwei.weatherinfopro.activity.WeatherInfoActivity;
import com.example.luoxinwei.weatherinfopro.receiver.WeatherReceiver;
import com.example.luoxinwei.weatherinfopro.util.HttpUtil;
import com.example.luoxinwei.weatherinfopro.util.Utility;

public class WeatherRefreshService extends Service {
    public WeatherRefreshService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("ServiceStart","true");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String weatherCode = preferences.getString("weatherCode","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryWeatherInfo(weatherCode);
            }
        }).start();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long anHour = 2*60*60*1000;
        long triggerTime = SystemClock.elapsedRealtime()+anHour;
        Intent intent1 = new Intent(this, WeatherReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent1,0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    public void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        Log.d("WeatherRefreshService",address);
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handleWeatherResponse(WeatherRefreshService.this,response);
                if (result){
                    Log.d("WeatherRefreshService","Refresh Sussess");
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
