package com.example.luoxinwei.weatherinfopro.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by luoxinwei on 2016/7/20.
 */
public class HttpUtil {
    private static String address;
    private static HttpCallbackListener listener;
    public HttpUtil(){

    }
    public HttpUtil(String address,HttpCallbackListener listener){
        this.address = address;
        this.listener = listener;
    }
    public void sendHttpRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(HttpUtil.address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    Log.d("HttpUtil",HttpUtil.address);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (HttpUtil.listener!=null){
                        HttpUtil.listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (HttpUtil.listener!=null){
                        HttpUtil.listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                boolean resquestStatus = false;
                int requestTimes = 5;
                try {
                    while((requestTimes>0)&&!resquestStatus) {
                        Log.d("HttpUtil", address);
                        URL url = new URL(address);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
//                    connection.setRequestProperty("Accept-Language", "zh-CN");//设置编码语言
//                    connection.setDoInput(true);
//                    connection.setDoOutput(true);
                        connection.connect();
                        Log.d("TAG_GET", "" + connection.getResponseCode());
                        if (connection.getResponseCode() == 200) {
                            InputStream in = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            if (!TextUtils.isEmpty(response.toString())){
                                resquestStatus = true;
                            }
                            if (listener != null) {
                                listener.onFinish(response.toString());
                            }
                        } else {
                            resquestStatus = false;
                            Log.d("TAG_GET", "Get方式请求失败");
                        }
                        requestTimes--;
                    }

                } catch (Exception e) {
                    if (listener!=null){
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void setAddress(String address) {
        HttpUtil.address = address;
    }

    public static void setListener(HttpCallbackListener listener) {
        HttpUtil.listener = listener;
    }

    public interface HttpCallbackListener {
        void onFinish(String response);
        void onError(Exception e);
    }

}
