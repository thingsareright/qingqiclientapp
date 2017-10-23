package com.example.qingqiclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.qingqiclient.entity.EI;
import com.example.qingqiclient.utils.CheckInputUtils;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddEIActivity extends AppCompatActivity {

    //各种控件
    private EditText awb;
    private EditText tel;
    private EditText sms;
    private Spinner smsaddressSpinner;
    private EditText address;
    private Button add_singleEI;
    private EditText name;
    //默认短信地址选择
    private static Long smsaddress = new Long(0);

    //日志标识
    private static final String LOG = "AddEIActivity";


    private static Map<Long, String> spinnerMap = new HashMap<>();
    static {
        spinnerMap.put(new Long(0), "西门"); spinnerMap.put(new Long(1), "东门"); spinnerMap.put(new Long(2), "菊二分拣区");
        spinnerMap.put(new Long(3), "菊二自助区（东）"); spinnerMap.put(new Long(4), "菊二自助区（北）"); spinnerMap.put(new Long(5), "菊二顺丰专区");
        spinnerMap.put(new Long(6), "菊二2-2延时区"); spinnerMap.put(new Long(7), "菊二前台"); spinnerMap.put(new Long(8), "京东快递");
        spinnerMap.put(new Long(9), "菊五自助一区"); spinnerMap.put(new Long(10), "菊五自助二区"); spinnerMap.put(new Long(11), "菊五分拣区");
        spinnerMap.put(new Long(12), "荷园洗浴中心二楼");
        spinnerMap.put(new Long(13), "其它");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ei);

        //为防止和其他XML文件中的控件冲突，所以有add_前缀
        awb = (EditText) findViewById(R.id.add_awb);
        tel = (EditText) findViewById(R.id.add_tel);
        sms = (EditText) findViewById(R.id.add_sms);
        smsaddressSpinner = (Spinner) findViewById(R.id.add_smsaddress);
        address = (EditText) findViewById(R.id.add_address);
        add_singleEI = (Button) findViewById(R.id.add_singleEI);
        name = (EditText) findViewById(R.id.name);
        smsaddress = new Long(0);
        smsaddressSpinner.setOnItemSelectedListener( new OnItemSelectedListenerImpl());

        add_singleEI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //加入对输入的手机号的检查
                if (!CheckInputUtils.checkTel(tel.getText().toString())){
                    Toast.makeText(AddEIActivity.this, "输入的手机号不合法，请输入不带空格的十一位手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (awb.getText().toString().isEmpty()){
                    Toast.makeText(AddEIActivity.this, "输入的物流单号为空，请输入正确的物流单号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (sms.getText().toString().isEmpty()){
                    Toast.makeText(AddEIActivity.this, "输入的快递短信为空，请输入正确的快递短信", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address.getText().toString().isEmpty()){
                    Toast.makeText(AddEIActivity.this, "输入的收货地址为空，请输入正确的收货地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.getText().toString().isEmpty()){
                    Toast.makeText(AddEIActivity.this, "输入的快递收件人为空，请输入正确的收件人姓名", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendSaveEIWithOkHttp(smsaddress);
                Intent intent = new Intent(AddEIActivity.this, All_EI_Info.class);
                startActivity(intent);
            }
        });
    }

    //下拉框选择事件
    private class OnItemSelectedListenerImpl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            //将用户选择的信息转换为对应的Long
            String smsAddressData = parent.getItemAtPosition(position).toString();
            ArrayList smsaddressList = (ArrayList) getKey(spinnerMap, smsAddressData);
            if (smsaddressList.size() != 0){
                smsaddress = (Long) smsaddressList.get(0);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }

    }


    /**
     * 下面进行网络请求
     * 我们使用OkHttp开源库
     */
    private void sendSaveEIWithOkHttp(final Long smsaddress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //先获得请求必须要的参数tel和password
                    SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                    String usertel = new String();
                    usertel = preferenceStore.getString("tel", "");
                    String password = preferenceStore.getString("password","");
                    if (usertel.equals("") || password.equals(""))
                        return;

                    //发送请求，获得数字
                    OkHttpClient client = new OkHttpClient();
                    //访问路径
                    String urlStr = Constant.getServer() + "/ei/saveData?&usertel="+ usertel + "&password=" + password + "&awb="
                            +  awb.getText().toString() + "&tel=" + tel.getText().toString() + "&sms="+
                            sms.getText().toString() + "&address=" + address.getText().toString() +
                            "&smsaddress=" + smsaddress + "&name=" + name.getText().toString();
                    Log.e(LOG, "网络请求地址：" + urlStr);
                    Request request = new Request.Builder()
                            //指定访问的远程服务器
                            .url(urlStr).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().toString();
                    response.body().close();
                    if (responseData.equals(1)){
                        //在下面这个方法中执行界面更新
                        UIchange();
                    } else {
                        //TODO 请求失败的操作跳转页面
                        Log.e(LOG,"ERROR");
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void UIchange() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //执行一波更新页面的操作，实际上是跳转到All_EI_Info界面
                Intent intent = new Intent(AddEIActivity.this, All_EI_Info.class);
                startActivity(intent);
            }
        });
    }


    public Object getKey(Map map, Object value) {
        Object o = null;
        ArrayList all = new ArrayList(); // 建一个数组用来存放符合条件的KEY值

        /*
         * 这里关键是那个entrySet()的方法,它会返回一个包含Map.Entry集的Set对象.
         * Map.Entry对象有getValue和getKey的方法,利用这两个方法就可以达到从值取键的目的了 *
         */

        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(value)) {
                o = entry.getKey();
                all.add(o); // 把符合条件的项先放到容器中,下面再一次性打印出
            }
        }
        return all;
    }
}
