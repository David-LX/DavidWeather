package com.example.luoxinwei.weatherinfopro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.luoxinwei.weatherinfopro.R;
import com.example.luoxinwei.weatherinfopro.model.*;
import com.example.luoxinwei.weatherinfopro.util.HttpUtil;
import com.example.luoxinwei.weatherinfopro.util.Utility;

import java.util.ArrayList;
import java.util.List;


public class ChooseAreaActivity extends AppCompatActivity {


    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    private TextView titleText;
    private ListView listView;
    private List<Province> procinceList;
    private List<City> cityList;
    private List<County> countyList;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter adapter;
    private WeatherDB weatherDB;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sprefs.getBoolean("citySelected",false) && !MyApplication.isRefresh){
            Intent intent = new Intent(this,WeatherInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);

        titleText = (TextView)findViewById(R.id.titleText);
        titleText.setText("China");
        listView = (ListView)findViewById(R.id.listView);
        weatherDB = WeatherDB.getInstance(this);
//        dataList.add("chongwing");
//        dataList.add("chengdu");
//        dataList.add("liuzhou");
//        dataList.add("shanghai");
//        dataList.add("beijing");
        adapter = new ArrayAdapter<String>(ChooseAreaActivity.this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        queryAllProvinces();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (MyApplication.currentLevel == LEVEL_PROVINCE){
                    selectedProvince = procinceList.get(i);
                    queryAllCities();
                }else if (MyApplication.currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryAllCounties();
                }else if (MyApplication.currentLevel ==  LEVEL_COUNTY){
                    selectedCounty = countyList.get(i);
                    String countyCode = selectedProvince.getProvinceCode()
                            +selectedCity.getCityCode()
                            +selectedCounty.getCountyCode();
                    Log.d("COUNTY_CODE",countyCode);
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherInfoActivity.class);
                    intent.putExtra("countyCode",countyCode);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.isRefresh = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.isRefresh){
                queryAllProvinces();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d("currentlevel",""+MyApplication.currentLevel);
        if (MyApplication.currentLevel == LEVEL_COUNTY){
            queryAllCities();
        }else if (MyApplication.currentLevel == LEVEL_CITY){
            queryAllProvinces();
        }else if (MyApplication.currentLevel == LEVEL_PROVINCE){
            finish();
        }else {
            if (MyApplication.isRefresh){
                Intent intent = new Intent(this,WeatherInfoActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    private void queryAllProvinces(){
        procinceList = weatherDB.loadProvinces();
        if (procinceList.size()>0){
            dataList.clear();
            for (Province province:procinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            MyApplication.currentLevel = LEVEL_PROVINCE;
        }else {
            Log.d("TAG", "queryFromServer");
            queryFromServer(null,"province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryAllCities() {
        Log.d("TAG","AllCities");
        cityList = weatherDB.loadCities(selectedProvince.getProvinceId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            MyApplication.currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryAllCounties() {
        Log.d("TAG","AllCounties");
        countyList = weatherDB.loadCounties(selectedCity.getCityId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            MyApplication.currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode()+selectedCity.getCityCode(), "county");
        }
    }

    private void queryFromServer(final String code,final String type){
        String address="";
        if ("province".equals(type)){
            address = "http://www.weather.com.cn/data/city3jdata/china.html";//获得省份

        }else if ("city".equals(type)){
//            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
            address = "http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";//获得城市
        }else if ("county".equals(type)){
            address = "http://www.weather.com.cn/data/city3jdata/station/"+code+".html";
        }
        Log.d("TAG_ADD1", address);
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.d("TAG_ADD2", response+"");
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(weatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCitiesResponse(weatherDB,response,selectedProvince.getProvinceId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountiesResponse(weatherDB,response,selectedCity.getCityId());
                }
                Log.d("TAG_ADD3", result+"");
                if (result){
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ("province".equals(type)) {
                                queryAllProvinces();
                            } else if ("city".equals(type)) {
                                queryAllCities();
                            } else if ("county".equals(type)) {
                                queryAllCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Log.d("TAG_ADD", "error");
            }
        });
    }
}
