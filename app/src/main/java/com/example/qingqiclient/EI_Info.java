package com.example.qingqiclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qingqiclient.entity.EI;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.JsonUtils;
import com.mob.tools.utils.LocationHelper;

import java.io.IOException;
import java.util.List;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EI_Info extends AppCompatActivity {

    private TextView awb_state;
    private TextView tel;
    private TextView sms;
    private TextView smsaddress;
    private TextView address;
    private Button delete_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ei__info);

        //获取各个组件
        awb_state = (TextView) findViewById(R.id.awb_state);
        tel = (TextView) findViewById(R.id.tel);
        sms = (TextView) findViewById(R.id.sms);
        smsaddress = (TextView) findViewById(R.id.smsaddress);
        address = (TextView) findViewById(R.id.address);
        delete_btn = (Button) findViewById(R.id.delete);



        //先获取上个活动传来的Intent中的数据
        Intent intent = getIntent();
        Long id = intent.getLongExtra("id", new Long(-1));
        //如果有对应的id
        if (id.longValue() != -1){
            //展开网络查询，获取相应的EI记录的信息
            sendRequestWithOkHttp(id);
        }
    }


    private void sendRequestWithOkHttp(final Long id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //先获得请求必须要的参数tel和password
                    SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                    String tel = new String();
                    tel = preferenceStore.getString("tel", "");
                    String password = preferenceStore.getString("password","");
                    if (tel.equals("") || password.equals(""))
                        return;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Constant.getServer() + "/ei/getSingleEIDataById?&usertel=" + tel + "&password=" + password + "&id=" + id)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    //我们在下面这个方法中更新UI
                    doWithUi(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doWithUi(final String responseData) {
        //我们在这里进行UI操作
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //这里进行UI操作
                //第一步，解析JSON数据
                final EI ei = JsonUtils.parseEIWithGSON(responseData);
                if (ei == null)
                    return;

                //第二步，根据ei数据来填充UI
                awb_state.setText("物流单号：" + ei.getAwb() + "   （" + Constant.stateString.get(ei.getState().intValue()) +"）");
                tel.setText("电话号码：" + ei.getTel());
                sms.setText("短信信息：" + ei.getSms());
                Log.e("*******************", ei.getId().toString());
                smsaddress.setText("取货地址（快递被快递公司送到的位置）：" + Constant.smsAddress.get(ei.getSmsaddress().intValue()));
                address.setText("您指定的收货地址： " + ei.getAddress());
                //如果已经取到货，那么按钮就不会显示
                if (ei.getState() != 0){
                    delete_btn.setVisibility(View.GONE);
                }
                //对按钮设置点击监听器
                delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //删除此记录
                        try {
                            sendDeleteRequestWithOkHttp(ei.getId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void sendDeleteRequestWithOkHttp(final Long id) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //先获得请求必须要的参数tel和password
                    SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                    String tel = new String();
                    tel = preferenceStore.getString("tel", "");
                    String password = preferenceStore.getString("password","");
                    if (tel.equals("") || password.equals(""))
                        return;
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(Constant.getServer() + "/ei/deleteOneEIData?&tel=" + tel + "&password=" + password + "&id=" + id)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    if (responseData.equals("1")){
                        //删除成功则返回EI信息总视图，并弹出Toast
                        goBackToLastActivity();
                    } else {
                        //删除失败则发出失败的TOast提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EI_Info.this,"删除失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void goBackToLastActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //弹出Toast提示删除成功
                Toast.makeText(EI_Info.this, "删除成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EI_Info.this, All_EI_Info.class);
                startActivity(intent);
            }
        });
    }
}
