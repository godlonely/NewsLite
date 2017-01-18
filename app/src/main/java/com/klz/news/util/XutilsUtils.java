package com.klz.news.util;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created on 2016/12/29.
 */

public class XutilsUtils {
    private static XutilsUtils instance;
    private static ImageOptions options;
    private static DbManager.DaoConfig daoConfig;
    /**
     * get请求
     */
    public static String XUTILS_HTTP_GET = "XutilsUtils_LoadHttpUtils_Get";
    /**
     * post请求
     */
    public static String XUTILS_HTTP_POST = "XutilsUtils_LoadHttpUtils_POST";

    public static XutilsUtils getInstance() {
        if (instance == null) {
            synchronized (XutilsUtils.class) {
                if (instance == null) {
                    instance = new XutilsUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 数据库初始化 建议在appliation初始化
     *
     * @param Dbname    数据库名字
     * @param DbDirpath 自定义数据库储存位置  为null
     *                  则默认存储在/data/data/你的应用程序/database/xxx.db下
     * @param Dbversion 数据库版本号
     */
    public void InitializeDB(String Dbname, String DbDirpath, int Dbversion) {
        daoConfig = new DbManager.DaoConfig().setDbName(Dbname)//创建数据库的名称
                .setDbVersion(Dbversion).setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });//数据库版本号
        if (DbDirpath != null) {
            daoConfig.setDbDir(new File(DbDirpath));
        }
        daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {//数据库更新操作

            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

            }
        });
    }

    /**
     * 使用数据库
     * <BR/>Parent test = db.selector(Parent.class).where("id", "in", new int[]{1, 3, 6}).findFirst();
     * <BR/>long count = db.selector(Parent.class).where("name", "LIKE", "w%").and("age", ">", 32).count();
     * <BR/>List<Parent> testList = db.selector(Parent.class).where("id", "between", new String[]{"1", "5"}).findAll();
     *
     * @return DbManager
     */
    public DbManager GetDbManager() {
        return x.getDb(daoConfig);
    }

    /**
     * xutils回调接口
     */
    public interface XutilsCallback {
        void Cancel(Callback.CancelledException arg0);

        void Error(Throwable arg0, boolean arg1);

        void Finish();

        void Success(Object arg0);
    }
    // 加载本地图片
    // x.image().bind(imgv, "assets://test.gif", options);
    // x.image().bind(iv_big_img, new
    // File("/sdcard/test.gif").toURI().toString(), imageOptions);
    // x.image().bind(iv_big_img, "/sdcard/test.gif", imageOptions);
    // x.image().bind(iv_big_img, "file:///sdcard/test.gif", imageOptions);
    // x.image().bind(iv_big_img, "file:/sdcard/test.gif", imageOptions);

    /**
     * 下载图片添加监听
     *
     * @param path
     * @param imageView
     * @param loadingimageId
     * @param errorimageId
     * @param callback
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void LoadBitmapWithListen(String path, ImageView imageView, int loadingimageId, int errorimageId, final XutilsCallback callback) {
        if (options == null) {
            GetImageOptions(loadingimageId, errorimageId);
        }
        x.image().bind(imageView, path, options, new Callback.CommonCallback<Drawable>() {

            @Override
            public void onSuccess(Drawable arg0) {
                callback.Success(arg0);
            }

            @Override
            public void onFinished() {
                callback.Finish();
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                callback.Error(arg0, arg1);
            }

            @Override
            public void onCancelled(CancelledException arg0) {
                callback.Cancel(arg0);
            }
        });
    }

    /**
     * 下载图片不添加监听
     *
     * @param path
     * @param imageView
     */
    public void LoadBitmapWithoutListen(String path, int loadingimageId, int errorimageId, ImageView imageView) {
        if (options == null) {
            GetImageOptions(loadingimageId, errorimageId);
        }
        x.image().bind(imageView, path, options);
    }

    /**
     * 发送get或者post请求
     *
     * @param method   请求方式  XUTILS_HTTP_GET  or  XUTILS_HTTP_POST
     * @param path
     * @param map      请求或上传数据(file,string,object)， 可以为null
     * @param callback
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void LoadHttpUtils(String method, String path, HashMap<String, Object> map, final XutilsCallback callback) {
        RequestParams params = new RequestParams(path);
        params.setAutoResume(true);//设置断点重新发送
        if (map != null) {
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() instanceof File) {
                    params.addBodyParameter((String) entry.getKey(), (File) entry.getValue());
                    params.setMultipart(true);
                } else {
                    params.addParameter((String) entry.getKey(), entry.getValue());
                }
            }
        }
        if (method == null || method.equals(XUTILS_HTTP_GET)) {
            x.http().get(params, GetStringCommonCallback(callback));
        } else if (method.equals(XUTILS_HTTP_POST)) {
            x.http().post(params, GetStringCommonCallback(callback));
        }

    }

    private Callback.CommonCallback<String> GetStringCommonCallback(final XutilsCallback callback) {
        return new Callback.CommonCallback<String>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                callback.Cancel(arg0);
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                callback.Error(arg0, arg1);
            }

            @Override
            public void onFinished() {
                callback.Finish();
            }

            @Override
            public void onSuccess(String arg0) {
                callback.Success(arg0);
            }
        };
    }

    /**
     * 下载文件
     *
     * @param method       请求方式  XUTILS_HTTP_GET  or  XUTILS_HTTP_POST
     * @param path
     * @param savefilepath 文件保存地址
     * @param callback
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void DownloadFile(String method, String path, String savefilepath, XutilsCallback callback) {
        RequestParams params = new RequestParams(path);
        params.setAutoResume(true); //设置断点续传
        params.setSaveFilePath(savefilepath);
        if (method == null || method.equals(XUTILS_HTTP_GET)) {
            x.http().get(params, GetFileCommonCallback(callback));
        } else if (method.equals(XUTILS_HTTP_POST)) {
            x.http().post(params, GetFileCommonCallback(callback));
        }
    }

    private Callback.CommonCallback<File> GetFileCommonCallback(final XutilsCallback callback) {
        return new Callback.CommonCallback<File>() {

            @Override
            public void onCancelled(CancelledException arg0) {
                callback.Cancel(arg0);
            }

            @Override
            public void onError(Throwable arg0, boolean arg1) {
                callback.Error(arg0, arg1);
            }

            @Override
            public void onFinished() {
                callback.Finish();
            }

            @Override
            public void onSuccess(File arg0) {
                callback.Success(arg0);
            }
        };
    }

    /**
     * 上传数据
     *
     * @param method   请求方式  XUTILS_HTTP_GET  or  XUTILS_HTTP_POST
     * @param path
     * @param map
     * @param callback
     */
    public void UploadFile(String method, String path, HashMap<String, Object> map, XutilsCallback callback) {
        RequestParams params = new RequestParams(path);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.addParameter(entry.getKey(), entry.getValue());
            }
        }
        if (method == null || method.equals(XUTILS_HTTP_GET)) {
            x.http().get(params, GetStringCommonCallback(callback));
        } else if (method.equals(XUTILS_HTTP_POST)) {
            x.http().post(params, GetStringCommonCallback(callback));
        }
    }

    /**
     * 设置加载图片的参数
     *
     * @param loadingimageId
     * @param errorimageId
     */
    private void GetImageOptions(int loadingimageId, int errorimageId) {
        // 设置加载图片的参数
        options = new ImageOptions.Builder()
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                // .setCrop(true)
                // 是否忽略GIF格式的图片
                .setIgnoreGif(false)
                //设置显示圆形图片
                //.setCircular(false)
                //设置半径
                //.setRadius(10)
                // 图片缩放模式
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                // 下载中显示的图片
                .setLoadingDrawableId(loadingimageId)
                // 下载失败显示的图片
                .setFailureDrawableId(errorimageId)
                // 得到ImageOptions对象
                .build();
    }
}
