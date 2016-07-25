package com.example.luoxinwei.weatherinfopro.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.luoxinwei.weatherinfopro.db.WeatherSQLHelper;
import com.example.luoxinwei.weatherinfopro.service.WeatherRefreshService;

/**
 * Created by luoxinwei on 2016/7/25.
 */
public class WeatherReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, WeatherRefreshService.class);
        context.startService(intent1);
    }
}
