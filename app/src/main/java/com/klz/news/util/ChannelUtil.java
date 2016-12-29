package com.klz.news.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.apache.commons.lang.StringUtils;

/**
 * Created on 2016/12/22.
 */

public class ChannelUtil {
    /**
     * 获取Umeng渠道号
     *
     * @param context
     * @return 渠道号
     */
    public static String getChannel(Context context) {
        String channel = "K_self";
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info != null && info.metaData != null) {
                String metaData = info.metaData.getString("K_CHANNEL");
                if (!StringUtils.isBlank(metaData)) {
                    channel = metaData;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel;
    }
}
