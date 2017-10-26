package com.example.qingqiclient.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.qingqiclient.All_EI_Info;
import com.example.qingqiclient.R;
import com.example.qingqiclient.utils.CheckInputUtils;
import com.example.qingqiclient.utils.Constant;
import com.example.qingqiclient.utils.CountDownTimerUtils;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


import devliving.online.securedpreferencestore.DefaultRecoveryHandler;
import devliving.online.securedpreferencestore.SecuredPreferenceStore;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 *这个碎片主要用来进行用户的注册
 * 注意权限是批量申请的
 * 一开始是使用Mob，现在改用腾讯云 2017/10/25
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private Button send_checkCode_btn;  //  发送验证码的btn
    private Button submmit; //提交按钮
    private EditText checkCode; //验证码
    private EditText usertel;   //用户输入手机号注册
    private EditText password;  //用户输入密码
    private EditText password_confirm;  //用户再次输入密码确认
    private ImageView background_img;   //背景图片


    //验证码全局变量
    private static String code = new String("0");




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_register_forget_password, container, false);
        //先初始化各个控件
        checkCode  = (EditText) v.findViewById(R.id.check_Code);
        submmit = (Button) v.findViewById(R.id.submmit);
        send_checkCode_btn = (Button) v.findViewById(R.id.send_checkCode);
        usertel = (EditText) v.findViewById(R.id.register_tel);
        password = (EditText) v.findViewById(R.id.register_password);
        password_confirm = (EditText) v.findViewById(R.id.register_password_confirm);
        background_img = (ImageView) v.findViewById(R.id.background_img);
        //提交按钮要先设置为不可见
        submmit.setVisibility(View.INVISIBLE);

        //解决图片太大加载难的问题,使用Glide库动态加载图片
        Glide.with(getContext()).load(R.drawable.register).into(background_img);

        send_checkCode_btn.setOnClickListener(this);
        submmit.setOnClickListener(this);



        //要先对加密开源库进行初始化
        try {
            SecuredPreferenceStore.init(getActivity().getApplicationContext(), new DefaultRecoveryHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    response.body().close();

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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_checkCode:
                //执行发送验证码的逻辑，我们放到一个向其情服务器的网络请求中，请求到的验证码赋值到code中
                sendRequestForCode(usertel.getText().toString());
                break;
            case R.id.submmit:
                //执行验证注册逻辑
                //先判断手机号是否合法
                if (!CheckInputUtils.checkTel(usertel.getText().toString())){
                    //如果不合法，则提示
                    Toast.makeText(getContext(), "手机号不合法，输入的手机号不能带空格", Toast.LENGTH_SHORT).show();
                    break;
                }
                Boolean flag = CheckInputUtils.checkPassword(password.getText().toString()) & CheckInputUtils.checkPassword(password_confirm.getText().toString()); //手机号合法则为TRUE
                if (!flag){
                    Toast.makeText(getContext(), "密码必须是由6到26位的由字母、数字、下划线组成的字符串", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!password.getText().toString().equals(password_confirm.getText().toString())){
                    Toast.makeText(getContext(), "新老密码不相同", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    password_confirm.setText("");
                    break;
                }
                if (code == null){
                    Toast.makeText(getContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!code.equals(checkCode.getText().toString())){
                    Toast.makeText(getContext(), "验证码输入错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                //走到这一步，就可以向服务器发送注册请求了
                //先生成请求路径
                String url = Constant.getServer() + "/user/register?tel=" + usertel.getText().toString() +
                        "&password=" + password.getText().toString();
                sendRegisterWithOkHttp(url);
                break;
            default:
                break;
        }
    }

    /**
     * 这个方法用于向我们的服务器请求验证码
     * @param tel
     * @return
     */
    private void sendRequestForCode(final String tel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //发送请求，获得验证码
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            //指定访问的远程服务器
                            .url(Constant.getServer() + "/sms/getCode?tel=" + tel).build();
                    Response response = client.newCall(request).execute();
                    code = response.body().string();
                    response.body().close();

                    if (!code.equals("0")){
                        //发送验证码成功
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //执行计时逻辑，此处参考博客（http://blog.csdn.net/shineflowers/article/details/50111777）
                                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(send_checkCode_btn, 60000, 1000);
                                mCountDownTimerUtils.start();
                                //把提交按钮设置成可点击
                                submmit.setVisibility(View.VISIBLE);
                            }
                        });
                    }else {
                        //请求验证码失败逻辑
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "请求验证码失败，请检查您的网络", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
