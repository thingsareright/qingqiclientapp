package com.example.qingqiclient.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


import com.example.qingqiclient.All_EI_Info;
import com.example.qingqiclient.MainActivity;
import com.example.qingqiclient.R;
import com.example.qingqiclient.utils.CheckInputUtils;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.JsonUtils;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.smssdk.SMSSDK.getSupportedCountries;
import static cn.smssdk.SMSSDK.getVerificationCode;
import static cn.smssdk.SMSSDK.submitVerificationCode;
import static java.security.AccessController.getContext;

/**
 *这个碎片主要用来进行用户的注册
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private Button send_checkCode_btn;  //  发送验证码的btn
    private Button submmit; //提交按钮
    private EditText checkCode; //验证码
    private EditText usertel;   //用户输入手机号注册
    private EditText password;  //用户输入密码
    private EditText password_confirm;  //用户再次输入密码确认
    //全局变量Handler，处理验证码回调
    private EventHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        //先初始化各个控件
        checkCode  = (EditText) v.findViewById(R.id.check_Code);
        submmit = (Button) v.findViewById(R.id.submmit);
        send_checkCode_btn = (Button) v.findViewById(R.id.send_checkCode);
        usertel = (EditText) v.findViewById(R.id.register_tel);
        password = (EditText) v.findViewById(R.id.register_password);
        password_confirm = (EditText) v.findViewById(R.id.register_password_confirm);


        send_checkCode_btn.setOnClickListener(this);
        submmit.setOnClickListener(this);

        //在onCreate的方法里对SDK进行初始化
        //MobSDK.init(getContext(), "21bdeae252f01","27d63bf79a9c63d7839de041f8603422");
        handler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"验证成功",Toast.LENGTH_SHORT).show();
                                //下面执行验证码验证成功之后的逻辑，实行注册
                                sendRegisterWithOkHttp(Constant.getServer() + "/user/register?tel=" + usertel.getText().toString() +
                                "&password=" + password.getText().toString());
                            }
                        });

                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"验证码已发送",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    try {
                        JSONObject obj = new JSONObject(throwable.getMessage());
                        final String des = obj.optString("detail");
                        if (!TextUtils.isEmpty(des)){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"提交错误信息",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        SMSSDK.registerEventHandler(handler);

        return v;
    }


    /**
     * 执行注册的网络请求逻辑
     * @param s
     */
    private void sendRegisterWithOkHttp(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //发送请求，获得数字
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            //指定访问的远程服务器
                            .url(s).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    if (responseData.equals("1")){
                        //注册成功
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //要注意先把tel和password的值存入SharedPreferences中，我们这里用了一个开源库进行加密
                                SecuredPreferenceStore preferenceStore = SecuredPreferenceStore.getSharedInstance();
                                preferenceStore.edit().putString("tel",usertel.getText().toString()).apply();
                                preferenceStore.edit().putString("password", password.getText().toString()).apply();
                                Intent intent = new Intent(RegisterFragment.this.getActivity(),All_EI_Info.class);
                                startActivity(intent);
                            }
                        });
                    }else {
                        //注册失败逻辑
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void play(View view, String tel) {
        //获取验证码
        SMSSDK.getVerificationCode("86",tel);
    }

    public void tijiao(View view, String tel) {
        //在提交按钮的监听事件里先通过EditText获取到用户所输入的验证码，然后和刚才的那个手机号一起提交到后台验证
        String number = checkCode.getText().toString();
        SMSSDK.submitVerificationCode("86",tel,number);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_checkCode:
                //先申请一下各种运行时权限
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},1);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_PHONE_STATE},2);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},3);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECEIVE_SMS},4);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_SMS},5);
                }
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},6);
                }

                //判断用户手机号合法不合法
                String tel = usertel.getText().toString();
                Log.e("Tel", tel);
                if (CheckInputUtils.checkTel(tel)){
                    play(view, tel);
                } else {
                    Toast.makeText(getContext(), "手机号输入不合法，请输入注入:18895621356的形式", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.submmit:
                //检测两次输入的密码是否合法且相同
                if (CheckInputUtils.checkPassword(password.getText().toString()) && CheckInputUtils.checkPassword(password_confirm.getText().toString())){
                    if (password.getText().toString().equals(password_confirm.getText().toString())){
                        //判断用户手机号合法不合法，要注意这里也要对手机号进行验证，因为可能会出现用户在申请验证码之后更改手机号的状况
                        String tel_submmit = usertel.getText().toString();
                        if (CheckInputUtils.checkTel(tel_submmit)){
                            //我们把网络请求的方法写在提交之中
                            tijiao(view,tel_submmit);
                        }
                    }else {
                        Toast.makeText(getContext(), "对不起，请再次确认密码！", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "对不起，密码格式错误!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            case 6:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }else {
                    Toast.makeText(getContext(), "权限不够，系统不能为您正常服务", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
