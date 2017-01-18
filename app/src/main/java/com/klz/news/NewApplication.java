package com.klz.news;

import android.app.Application;

import com.klz.news.util.XutilsUtils;

import org.xutils.x;

/**
 * Created on 2016/12/29.
 */

public class NewApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        XutilsUtils.getInstance().InitializeDB("news_db", null, 1);
    }
}

