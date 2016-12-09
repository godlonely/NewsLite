package com.klz.news;

/**
 * Created on 2016/12/8.
 */

public class clearContentUtil {
    public static String htmlClearType(String c) {
        if (c != null) {
            String result;
            result = c.replaceAll("<p>","").replaceAll("</p>","");
            return result;
        } else {
            return "空";
        }
    }
}
