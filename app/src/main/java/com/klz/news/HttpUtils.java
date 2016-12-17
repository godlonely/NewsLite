package com.klz.news;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.klz.news.SettingFile.appUpdateUrl;
import static com.klz.news.SettingFile.showapi_appid;
import static com.klz.news.SettingFile.showapi_sign;

/**
 * Created by Kong on 2016/6/16 0016.
 */
public class HttpUtils {

    /**
     * 请求笑话的接口
     *
     * @param httpUrl 接口地址
     * @param maxResult 每次返回的数据条数
     * @param page 请求页数
     * @return String 返回数据
     */
    public static String requestShowApp(String httpUrl,String maxResult, String page) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + "showapi_appid="+showapi_appid+"&showapi_sign="+showapi_sign+"&maxResult="+maxResult+"&page="+page;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }
                reader.close();
                result = sbf.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String requestUpdate() {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String updateUrl = appUpdateUrl;
        try {
            URL url = new URL(updateUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.connect();
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                }
                reader.close();
                result = sbf.toString();
                Log.d("HttpUtils", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    /**
     * Android获得UA信息
     *
     * @param ctx
     * @return
     */
    public static String getUserAgentString(Context ctx) {
        WebView webview;
        webview = new WebView(ctx);
        webview.layout(0, 0, 0, 0);
        WebSettings settings = webview.getSettings();
        String ua = settings.getUserAgentString();
        Log.i("UA", ua);
        return ua;
    }

}

