package com.example.qingqiclient;
/**
 * 这个布局用来展示用户的已发送快递信息，用户可直接在此页面上查看各快递信息的状态，当然可以点击某快递信息，在新的详细信息活动中删除那些还未取的快递
 */

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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

        //发送请求，获得数据
        sendRequestforEIList();

    }


    /**
     * 下面进行网络请求
     * 我们使用OkHttp开源库
     */
    private void sendRequestforEIList(){
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

                    //解析请求，获得EI数组
                    eiList = JsonUtils.parseEIListWithGSON(responseData);
                    //在下面这个方法中执行界面更新
                    UIchange(eiList);
                    System.out.println(eiList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void UIchange(final List<EI> eiList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //下面开始组装RecyclerView
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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
