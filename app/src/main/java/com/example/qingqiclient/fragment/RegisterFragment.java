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


import com.example.qingqiclient.MainActivity;
import com.example.qingqiclient.R;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

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
    //全局变量Handler，处理验证码回调
    private EventHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        checkCode  = (EditText) v.findViewById(R.id.check_Code);
        submmit = (Button) v.findViewById(R.id.submmit);
        send_checkCode_btn = (Button) v.findViewById(R.id.send_checkCode);


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


    public void play(View view) {
        //获取验证码
        SMSSDK.getVerificationCode("86","18838951998");
    }

    public void tijiao(View view) {
        //在提交按钮的监听事件里先通过EditText获取到用户所输入的验证码，然后和刚才的那个手机号一起提交到后台验证
        String number = checkCode.getText().toString();
        SMSSDK.submitVerificationCode("86","18838951998",number);
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
                play(view);
                break;
            case R.id.submmit:
                tijiao(view);
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
