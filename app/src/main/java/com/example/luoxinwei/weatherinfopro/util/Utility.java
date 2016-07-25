package com.example.luoxinwei.weatherinfopro.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.luoxinwei.weatherinfopro.model.City;
import com.example.luoxinwei.weatherinfopro.model.County;
import com.example.luoxinwei.weatherinfopro.model.Province;
import com.example.luoxinwei.weatherinfopro.model.WeatherDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by luoxinwei on 2016/7/23.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,
                                                               String response){
        if (!TextUtils.isEmpty(response)){
            try{
                int i=0;
                JSONObject jsonObject = new JSONObject(response);
                Iterator<String> names = jsonObject.keys();
                while(names.hasNext()){
                    String key = names.next();
                    i++;
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString(key));
                    province.setProvinceCode(key);
                    weatherDB.saveProvince(province);
                    Log.d("ADD_PROVINCE",i+"-"+key);
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(WeatherDB weatherDB,
                                               String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try{
                int i=0;
                JSONObject jsonObject = new JSONObject(response);
                Iterator<String> names = jsonObject.keys();
                while(names.hasNext()){
                    String key = names.next();
                    i++;
                    City city = new City();
                    city.setCityName(jsonObject.getString(key));
                    city.setCityCode(key);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                    Log.d("ADD_CITY",i+"-"+key);
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(WeatherDB weatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            int i=0;
            try{
                JSONObject jsonObject = new JSONObject(response);
                Iterator<String> names = jsonObject.keys();
                while(names.hasNext()){
                    i++;
                    String key = names.next();
                    County county = new County();
                    county.setCountyName(jsonObject.getString(key));
                    county.setCountyCode(key);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                    Log.d("ADD_COUNTY",i+"-"+key);
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
//            String[] allCounties = response.split(",");
//            if (allCounties != null && allCounties.length > 0) {
//                for (String c : allCounties) {
//                    String[] array = c.split("\\|");
//                    County county = new County();
//                    county.setCountyCode(array[0]);
//                    county.setCountyName(array[1]);
//                    county.setCityId(cityId);
//// 将解析出来的数据存储到County表
//                    weatherDB.saveCounty(county);
//                }
//                return true;
//            }
        }
        return false;
    }

    public static boolean handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String pulishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,pulishTime);
            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void saveWeatherInfo(Context context,String cityName,
                                       String weatherCode,String temp1,String temp2,
                                       String weatherDesp,String publishTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context)
                .edit();
        editor.putBoolean("citySelected",true);
        editor.putString("cityName",cityName);
        editor.putString("weatherCode",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weatherDesp",weatherDesp);
        editor.putString("publishTime",publishTime);
        editor.putString("currentDate",simpleDateFormat.format(new Date()));
        editor.commit();
    }
}
