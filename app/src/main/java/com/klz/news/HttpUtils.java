package com.klz.news;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.klz.news.SettingFile.appUpdateUrl;
import static com.klz.news.SettingFile.baiduApiKey;

/**
 * Created by Kong on 2016/6/16 0016.
 */
public class HttpUtils {

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */

    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", baiduApiKey);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

}

