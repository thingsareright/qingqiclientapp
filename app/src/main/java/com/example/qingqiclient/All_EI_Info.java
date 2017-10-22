package com.example.qingqiclient;
/**
 * 这个布局用来展示用户的已发送快递信息，用户可直接在此页面上查看各快递信息的状态，当然可以点击某快递信息，在新的详细信息活动中删除那些还未取的快递
 * 这个活动是singletask的，而且每次到这个页面都会刷新，支持下拉刷新
 */

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.qingqiclient.adapter.EIAdapter;
import com.example.qingqiclient.entity.EI;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class All_EI_Info extends AppCompatActivity implements View.OnClickListener{

    //存放获取到的信息
    private static List<EI> eiList = new ArrayList<>();

    //用于表示悬浮按钮
    private FloatingActionButton add_btn;

    //下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    //recyclerview
    private RecyclerView recyclerView;

    //状态标志，为1表示是首次建立此页面，不是1表示是非初次加载此页面，执行刷新操作
    private int state = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_ei_info);

        add_btn = (FloatingActionButton) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(All_EI_Info.this, AddEIActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.recycler_view_refresh);
        //对下拉刷新进行基本的设置
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //执行刷新逻辑
                refreshEIList();
            }
        });

        //发送请求，获得数据
        sendRequestforEIList(1);

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //下面的语句是为了让这个页面具有自动刷新功能
        if (state != 1){
            //执行下拉刷新的逻辑
            refreshEIList();
            state++;
        }
    }

    /**
     * 执行刷新逻辑
     */
    private void refreshEIList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } ;
                sendRequestforEIList(2); //flag为1表示是首次请求，为2表示是刷新请求

            }
        }).start();
    }


    /**
     * 下面进行网络请求
     * 我们使用OkHttp开源库
     */
    private void sendRequestforEIList(final int flag){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //先获得请求必须要的参数tel和password
                    SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                    String tel = new String();
                    tel = preferenceStore.getString("tel", "");
                    String password = preferenceStore.getString("password","");
                    if (tel.equals("") || password.equals(""))
                        return;

                    //发送请求，获得数字
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            //指定访问的远程服务器
                            .url(Constant.getServer() + "/ei/getEIofOneUser?tel=" + tel + "&password=" + password).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    response.body().close();

                    //解析请求，获得EI数组
                    eiList = JsonUtils.parseEIListWithGSON(responseData);
                    //在下面这个方法中执行界面更新
                    if (flag == 1){
                        //首次请求
                        UIchange(eiList);
                    } else if (flag == 2){
                        refresh(eiList);    //下拉刷新请求
                    }

                    System.out.println(eiList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下拉刷新的逻辑
     * @param eiList
     */
    private void refresh(final List<EI> eiList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //下拉刷新请求
                EIAdapter eiAdapter = new EIAdapter(eiList);
                recyclerView.setAdapter(eiAdapter);
                eiAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);  //下拉刷新结束
            }
        });

    }

    private void UIchange(final List<EI> eiList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //下面开始组装RecyclerView
                LinearLayoutManager layoutManager = new LinearLayoutManager(All_EI_Info.this);
                recyclerView.setLayoutManager(layoutManager);
                EIAdapter eiAdapter = new EIAdapter(eiList);
                recyclerView.setAdapter(eiAdapter);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_btn:
                //加入用户按下悬浮式按钮的逻辑，跳入增加信息的活动界面
                Intent intent = new Intent(All_EI_Info.this, AddEIActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
