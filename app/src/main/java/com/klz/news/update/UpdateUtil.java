package com.klz.news.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.klz.news.network.NetworkUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created on 2016/12/9.
 */

public class UpdateUtil {
    //安装apk
    public static void installApk(Context c, File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        c.startActivity(intent);
    }

    /*
 * 获取当前程序的版本号
 */
    public static int getVersionCode(Context c) {
        try {
            //获取packagemanager的实例
            PackageManager packageManager = c.getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getVersionName(Context c) {
        try {
            //获取packagemanager的实例
            PackageManager packageManager = c.getPackageManager();
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(c.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), "updata.apk");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                //获取当前下载量
                pd.setProgress(total);
            }
            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            return null;
        }
    }

    /*
     * 从服务器中下载APK
     */
    protected static void downLoadWarning(final Context c, final appInfo info) {
        int netWorkType = NetworkUtil.getNetWorkType(c);
        if (netWorkType == NetworkUtil.NETWORKTYPE_WIFI) {
            downLoadApk(c, info);
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(c);
            b.setCancelable(false);
            b.setTitle("提示");
            b.setMessage("您当前不是WiFi网络，是否下载更新？");
            b.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downLoadApk(c, info);
                }
            });
            b.setNegativeButton("我再考虑一下", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            b.create().show();
        }

    }


    /**
     * 下载最新版的App文件
     *
     * @param info 最新版本信息
     */
    protected static void downLoadApk(final Context c, final appInfo info) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(c);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = UpdateUtil.getFileFromServer(info.getContent().getUrl(), pd);
                    sleep(1000);
                    UpdateUtil.installApk(c, file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {

                }
            }
        }.start();
    }


    /*
     *
     * 弹出对话框通知用户更新程序
     *
     * 弹出对话框的步骤：
     *  1.创建alertDialog的builder.
     *  2.要给builder设置属性, 对话框的内容,样式,按钮
     *  3.通过builder 创建一个对话框
     *  4.对话框show()出来
     */
    public static void showUpdataDialog(final Context c, final appInfo info) {
        AlertDialog.Builder builer = new AlertDialog.Builder(c);
        builer.setTitle(info.getDescription());

        String tips = info.getContent().getTips();
        builer.setMessage(tips);
        builer.setCancelable(false);
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadWarning(c, info);
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }

}
