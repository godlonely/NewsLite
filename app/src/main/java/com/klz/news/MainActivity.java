package com.klz.news;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.klz.news.adapter.MyAdapter;
import com.klz.news.model.Joke;
import com.klz.news.network.HttpUtils;
import com.klz.news.update.UpdateUtil;
import com.klz.news.update.appInfo;
import com.klz.news.util.ChannelUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.klz.news.util.SettingFile.showApiUrl;

@ContentView(R.layout.activity_main)
public class MainActivity extends Activity implements MyAdapter.MyItemClickListener, MyAdapter.MyItemLongClickListener {
    static String maxNum = "10";
    int num = 1;
    @ViewInject(R.id.recyclerview)
    private XRecyclerView mRecyclerview;
    @ViewInject(R.id.txt_version)
    private TextView version;
    @ViewInject(R.id.text_empty)
    private TextView mEmptyView;

    private ArrayList<Joke> listData;
    private ArrayList<Joke> newListData;
    private MyAdapter myAdapter;
    private boolean mIsRefreshing;
    private boolean mIsLoading;

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
                        UpdateUtil.showUpdataDialog(MainActivity.this, ifn);
                    }
                    break;
                case 8888:
                    Toast.makeText(MainActivity.this, "访问失败！请稍候重新尝试。", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initView();
        start();
    }

    private void initView() {
        //显示版本号
        String versionName = UpdateUtil.getVersionName(MainActivity.this);
        String label = DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        String channel = ChannelUtil.getChannel(this);
        version.setText(versionName + "\n" + label + "\n" + channel);
    }

    private void start() {
        update();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerview.setLayoutManager(layoutManager);

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
                        if (!TextUtils.isEmpty(jsonResult)) {
                            Log.d("MainActivity", jsonResult);
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

    @Override
    public void onItemClick(View view, int postion) {
        Joke lau = listData.get(postion - 2);
        if (lau != null) {
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
                if (!TextUtils.isEmpty(rs)) {
                    Log.d("MainActivity", "" + rs);
                    appInfo app = null;
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(rs);
                        app = JSONObject.parseObject(jsonObject.toString(), appInfo.class);
                        Message m = Message.obtain();
                        m.obj = app;
                        m.what = 9999;
                        handler.sendMessage(m);
                    } catch (Exception e) {
                        Message m = Message.obtain();
                        m.what = 8888;
                        handler.sendMessage(m);
                        e.printStackTrace();
                    }
                } else {
                    Message m = Message.obtain();
                    m.obj = rs;
                    m.what = -1;
                    handler.sendMessage(m);
                }
            }
        }).start();
    }
}
