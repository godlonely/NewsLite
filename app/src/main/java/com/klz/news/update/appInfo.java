package com.klz.news.update;

/**
 * Created on 2016/12/9.
 */

public class appInfo {

    /**
     * code : 0
     * description : 检测到最新版本，请及时更新！
     * content : {"versionCode":"1.0","time":1481274588,"url":"http://helpgod.oss-cn-qingdao.aliyuncs.com/appUpdate/news2016-12-09.apk","tips":"1、优化部分界面\n2、修复一些Bug"}
     */

    private int code;
    private String description;
    private ContentBean content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * versionCode : 1.0
         * time : 1481274588
         * url : http://helpgod.oss-cn-qingdao.aliyuncs.com/appUpdate/news2016-12-09.apk
         * tips : 1、优化部分界面
         * 2、修复一些Bug
         */
        private int versionCode;
        private String time;
        private String url;
        private String tips;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }
}
