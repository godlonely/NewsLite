package com.klz.news;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.klz.news.adapter.MyAdapter;
import com.klz.news.model.Joke;
import com.klz.news.network.NetworkUtil;
import com.klz.news.update.UpdateUtil;
import com.klz.news.update.appInfo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.klz.news.SettingFile.showApiUrl;

public class MainActivity extends Activity implements MyAdapter.MyItemClickListener, MyAdapter.MyItemLongClickListener {
    static String httpArg = "page=";
    static String maxNum = "10";
    int num = 1;
    private XRecyclerView mRecyclerview;
    private TextView version;

    private ArrayList<Joke> listData;
    private ArrayList<Joke> newListData;
    private MyAdapter myAdapter;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //处理数据到数据
                    String obj = (String) msg.obj;
                    Log.e("DATA", "数据为：" + obj);
                    listData.clear();
                    myAdapter.setDatas(newListData);
                    myAdapter.notifyDataSetChanged();
                    mRecyclerview.refreshComplete();
                    mIsRefreshing = false;
                    break;
                case 1:
                    //处理数据到数据
                    String obj1 = (String) msg.obj;
                    Log.e("DATA", "数据为：" + obj1);
                    myAdapter.setDatas(newListData);
                    myAdapter.notifyDataSetChanged();
                    mRecyclerview.loadMoreComplete();
                    mIsLoading = false;
                    break;
                case -1:
                    Toast.makeText(MainActivity.this, "网络访问失败！稍候重试。", Toast.LENGTH_LONG).show();
                    mIsLoading = false;
                    mIsRefreshing = false;
                    mRecyclerview.loadMoreComplete();
                    mRecyclerview.refreshComplete();
                    break;
                case 9999:
                    //升级操作
                    int versionCode = UpdateUtil.getVersionCode(MainActivity.this);
                    appInfo ifn = (appInfo) msg.obj;
                    if (versionCode < ifn.getContent().getVersionCode()) {
                        showUpdataDialog(ifn);
                    }
                    break;
            }
        }
    };
    private TextView mEmptyView;
    private boolean mIsRefreshing;
    private boolean mIsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        start();
        //        Toast.makeText(this, getUserAgentString(MainActivity.this), Toast.LENGTH_SHORT).show();
    }

    private void start() {
        update();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(layoutManager);

        //mRecyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        ArrowRefreshHeader refreshHeader = new ArrowRefreshHeader(this);
        //refreshHeader.setState(refreshHeader.STATE_RELEASE_TO_REFRESH);
        //refreshHeader.setProgressStyle(ProgressStyle.BallSpinFadeLoader);
        //mRecyclerview.setRefreshHeader(refreshHeader);
        mRecyclerview.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerview.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        mRecyclerview.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerview.setLoadingMoreEnabled(true);

        mRecyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mIsRefreshing || mIsLoading) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header, ((ViewGroup) findViewById(android.R.id.content)), false);
        mRecyclerview.addHeaderView(header);

        mRecyclerview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mIsRefreshing = true;
                num = 1;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jsonResult = HttpUtils.requestShowApp(showApiUrl, maxNum, "" + num);
                        Log.d("MainActivity", jsonResult);
                        if (!TextUtils.isEmpty(jsonResult)) {
                            num++;
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(jsonResult);
                                JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");

                                newListData = (ArrayList<Joke>) JSONObject.parseArray(showapi_res_body.getJSONArray("contentlist").toString(), Joke.class);
                                Log.e("Data", "数据长度：" + newListData.size());
                                Log.e("Data", "数据0：" + newListData.get(0).getTitle());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            //myAdapter.notifyDataSetChanged();
                            //mRecyclerview.refreshComplete();
                            Message m = Message.obtain();
                            m.obj = jsonResult;
                            m.what = 0;
                            handler.sendMessage(m);
                        } else {
                            Message m = Message.obtain();
                            m.obj = jsonResult;
                            m.what = -1;
                            handler.sendMessage(m);
                        }
                    }
                }).start();
            }

            @Override
            public void onLoadMore() {
                mIsLoading = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jsonResult = HttpUtils.requestShowApp(showApiUrl, maxNum, "" + num);
                        if (!TextUtils.isEmpty(jsonResult)) {
                            num++;
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(jsonResult);
                                JSONObject showapi_res_body = jsonObject.getJSONObject("showapi_res_body");

                                newListData = (ArrayList<Joke>) JSONObject.parseArray(showapi_res_body.getJSONArray("contentlist").toString(), Joke.class);
                                Log.e("Data", "数据长度：" + newListData.size());
                                Log.e("Data", "数据0：" + newListData.get(0).getTitle());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //myAdapter.notifyDataSetChanged();
                            //mRecyclerview.refreshComplete();
                            Message m = Message.obtain();
                            m.obj = jsonResult;
                            m.what = 1;
                            handler.sendMessage(m);
                        } else {
                            Message m = Message.obtain();
                            m.obj = jsonResult;
                            m.what = -1;
                            handler.sendMessage(m);
                        }
                    }
                }).start();
            }
        });

        mRecyclerview.setEmptyView(mEmptyView);

        listData = new ArrayList<>();
        myAdapter = new MyAdapter(MainActivity.this, listData);
        mRecyclerview.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(this);
        myAdapter.setOnItemLongClickListener(null);
        mRecyclerview.refresh();
    }

    private void initView() {
        mRecyclerview = (XRecyclerView) findViewById(R.id.recyclerview);
        mEmptyView = (TextView) findViewById(R.id.text_empty);
        version = (TextView) findViewById(R.id.txt_version);
        //显示版本号
        String versionName = UpdateUtil.getVersionName(MainActivity.this);
        version.setText(versionName);
    }

    @Override
    public void onItemClick(View view, int postion) {
        Joke lau = listData.get(postion - 2);
        if (lau != null) {
            //Toast.makeText(this, lau.getText(), Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(lau.getTitle());
            builder.setMessage(lau.getText());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(false);
            builder.setPositiveButton("哈哈", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onItemLongClick(View view, int postion) {
        Joke lau = listData.get(postion);
        if (lau != null) {
            Toast.makeText(this, lau.getText(), Toast.LENGTH_LONG).show();
        }
    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String rs = HttpUtils.requestUpdate();
                Log.d("MainActivity", rs);
                String jsonResult = null;
                try {
                    jsonResult = URLDecoder.decode(rs, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("MainActivity", jsonResult);
                if (!TextUtils.isEmpty(jsonResult)) {
                    appInfo app = null;
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(jsonResult);
                        app = JSONObject.parseObject(jsonObject.toString(), appInfo.class);
                        Message m = Message.obtain();
                        m.obj = app;
                        m.what = 9999;
                        handler.sendMessage(m);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Message m = Message.obtain();
                    m.obj = jsonResult;
                    m.what = -1;
                    handler.sendMessage(m);
                }
            }
        }).start();
    }

    /*
     * 从服务器中下载APK
     */
    protected void downLoadWarning(final appInfo info) {
        int netWorkType = NetworkUtil.getNetWorkType(this);
        if (netWorkType == NetworkUtil.NETWORKTYPE_WIFI) {
            downLoadApk(info);
        } else {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setCancelable(false);
            b.setTitle("提示");
            b.setMessage("您当前不是WiFi网络，是否下载更新？");
            b.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    downLoadApk(info);
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

    protected void downLoadApk(final appInfo info) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = UpdateUtil.getFileFromServer(info.getContent().getUrl(), pd);
                    sleep(1000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    Message msg = new Message();
                    msg.what = -1;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
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
    protected void showUpdataDialog(final appInfo info) {
        AlertDialog.Builder builer = new AlertDialog.Builder(this);
        builer.setTitle(info.getDescription());

        String tips = info.getContent().getTips();
        builer.setMessage(tips);
        builer.setCancelable(false);
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadWarning(info);
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
